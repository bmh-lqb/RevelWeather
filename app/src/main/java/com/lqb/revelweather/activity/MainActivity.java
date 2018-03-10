package com.lqb.revelweather.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.PermissionChecker;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.bumptech.glide.Glide;
import com.lqb.revelweather.R;
import com.lqb.revelweather.adapter.HourAdapter;
import com.lqb.revelweather.adapter.SuggestionAdapter;
import com.lqb.revelweather.bean.WeatherData.ForecastBean;
import com.lqb.revelweather.bean.WeatherData.HourlyBean;
import com.lqb.revelweather.bean.WeatherData.WeatherBean;
import com.lqb.revelweather.service.AutoUpdateService;
import com.lqb.revelweather.util.DayUtils;
import com.lqb.revelweather.util.HttpUtil;
import com.lqb.revelweather.util.SPUtil;
import com.lqb.revelweather.view.MyScrollView;
import com.lqb.revelweather.util.Utility;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

public class MainActivity extends Activity {
    @Bind(R.id.title_menu)
    ImageView titleMenu;   // 设置

    @Bind(R.id.bing_pic_img)
    ImageView bgPicture;    // 背景图

    @Bind(R.id.view_line)
    View view_Line;

    @Bind(R.id.title_city_name)
    TextView titleCityName; // 标题城市

    @Bind(R.id.conditions)
    TextView conditions;    // 当前天气情况

    @Bind(R.id.degree_txt)
    TextView degreeTxt;    // 当前温度

    @Bind(R.id.degree)
    TextView degree;    // °

    @Bind(R.id.ll_day)
    LinearLayout llDay;

    @Bind(R.id.weather_scrollView)
    MyScrollView weatherScrollView;

    @Bind(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;

    @Bind(R.id.rv_hour)
    RecyclerView rvHour;

    private int month;  // 月
    private int day;    //  日
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private LocationClient mLocationClient = null; // 定位
    private MyLocationListener myLocationListener = new MyLocationListener();
    private String[] str;
    private String[] arr;
    private int num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //处理背景图片没有和状态栏融合在一起的问题
        if (Build.VERSION.SDK_INT > 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        if (HttpUtil.getNetworkState(this) == HttpUtil.NETWORN_NONE) {
            Toast.makeText(this, "当前网络不可用", Toast.LENGTH_LONG).show();
        }

        sp = getSharedPreferences("weahter", MODE_PRIVATE);
        String[] spCity = SPUtil.getSharedPreference(MainActivity.this, "cityNames");
        if (!spCity[0].equals("")) {    // 有保存的城市则获取城市天气
            String[] spWeather = SPUtil.getSharedPreference(MainActivity.this, "sp_weather");
            if (!spWeather[0].equals("")) { // 有缓存时直接解析天气
                WeatherBean weather = Utility.handleWeatherResponse(spWeather[0]);
                showWeatherInfo(weather);
            } else {
                saveCities();
            }
        } else {    // 没保存城市时跳转到选择城市界面
            Intent intent = new Intent(MainActivity.this, SearchCityActivity.class);
            startActivity(intent);
            finish();
        }

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                saveCities();
            }
        });

        //从缓存中获取必应图片
        String bingPic = sp.getString("bing_pic", null);
        if (bingPic != null) {
            Glide.with(getApplicationContext()).load(bingPic).into(bgPicture);
        } else {
            loadBingPic();
        }

        titleMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menu(titleMenu);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        saveCities();
    }

    public void saveCities() {
        str = SPUtil.getSharedPreference(MainActivity.this, "cityNames");
        arr = new String[str.length];
        num = str.length;
        for (String aStr : str) {
            Log.e("aStr", aStr + " ");
            requestWeather(aStr);
        }
    }

    // 判断是否有定位权限
    private boolean checkPermissions() {
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        } else {
            if (getApplicationInfo().targetSdkVersion < 23) {
                return PermissionChecker.checkPermission(this, ACCESS_COARSE_LOCATION,
                        Binder.getCallingPid(), Binder.getCallingUid(), getPackageName()) == PackageManager.PERMISSION_GRANTED;
            } else {
                return checkSelfPermission(ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
            }
        }
    }

    //显示天气信息
    private void showWeatherInfo(WeatherBean weather) {
        titleCityName.setText(weather.basic.cityName);

        conditions.setText(weather.now.cond.cond_txt);
        degreeTxt.setText(weather.now.temperature);
        degree.setText("°");

        List<String> list = new ArrayList<>();
        // 逐小时
        for (HourlyBean hourly : weather.hourList) {
            list.add(hourly.time.split(" ")[1]);
            int picId = getResources().getIdentifier("pic_" + hourly.cond.code, "drawable", getPackageName());
            list.add(picId + "");
            list.add(hourly.cond.txt);
            list.add(hourly.temperature + "°");
        }

        LinearLayoutManager lManager = new LinearLayoutManager(this);
        lManager.setOrientation(LinearLayoutManager.HORIZONTAL);    // 横向布局
        rvHour.setLayoutManager(lManager);
        HourAdapter adapter = new HourAdapter(list);
        rvHour.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        // 3天预报
        llDay.removeAllViews();
        for (ForecastBean forecast : weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.day_item, llDay, false);

            TextView tvDate = view.findViewById(R.id.tv_date);
            ImageView ivDayCode = view.findViewById(R.id.iv_day_code);
            TextView tvDayTxt = view.findViewById(R.id.tv_day_txt);
            TextView maxText = view.findViewById(R.id.max_tmp);
            TextView minText = view.findViewById(R.id.min_tmp);

            getDate(forecast.date);

            @SuppressLint("SimpleDateFormat")
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

            String date = DayUtils.getTitleDay(df.format(forecast.date));
            tvDate.setText(String.format(getResources().getString(R.string.date), date, month + "/" + day));
            int picId = getResources().getIdentifier("pic_" + forecast.cond.cond_code, "drawable", getPackageName());
            ivDayCode.setImageResource(picId);
            tvDayTxt.setText(forecast.cond.cond_txt);
            maxText.setText(forecast.temperature.tmp_max + "°");
            minText.setText(forecast.temperature.tmp_min + "°");
            llDay.addView(view);
        }

        int[] images = {R.drawable.comf, R.drawable.cw, R.drawable.drsg, R.drawable.flu, R.drawable.sport, R.drawable.trav,
                R.drawable.uv, R.drawable.air};
        String[] type = {"舒适度", "洗车", "穿衣", "感冒", "运动", "旅游", "紫外线", "空气污染"};
        String[] intro = {weather.suggestion.comfort.intro, weather.suggestion.carWash.intro, weather.suggestion.dress.intro,
                weather.suggestion.influenza.intro, weather.suggestion.sport.intro, weather.suggestion.travel.intro,
                weather.suggestion.ultraviolet.intro, weather.suggestion.pollution.intro};
        String[] detaile = {weather.suggestion.comfort.detaile, weather.suggestion.carWash.detaile, weather.suggestion.dress.detaile,
                weather.suggestion.influenza.detaile, weather.suggestion.sport.detaile, weather.suggestion.travel.detaile,
                weather.suggestion.ultraviolet.detaile, weather.suggestion.pollution.detaile};

        GridView gridView = findViewById(R.id.gridview_suggest);

        SuggestionAdapter suggestionAdapter = new SuggestionAdapter(this, images, type, intro, detaile);
        gridView.setAdapter(suggestionAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });

        titleMenu.setVisibility(View.VISIBLE);
        view_Line.setVisibility(View.VISIBLE);
        weatherScrollView.smoothScrollTo(0, 0);   // 置顶

        //启动自动更新服务
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

    //根据天气id获取天气信息
    public void requestWeather(String weatherId) {
        swipeRefresh.setRefreshing(true);
        String weatherUrl = "https://free-api.heweather.com/v5/weather?city=" +
                weatherId + "&key=103b06818255425087dba94ee25576d3";
        HttpUtil.sendOkHttpRequests(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,
                                "获取天气信息失败" + e.toString(), Toast.LENGTH_SHORT).show();
                        //刷新结束后，隐藏刷新进度条
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final WeatherBean weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            num--;

                            for (int i = 0; i < str.length; i++) {
                                if (str[i].equals(weather.basic.cityName)) {
                                    arr[i] = responseText;
                                }
                            }

                            if (num == 0) {    // 刷新所有保存的城市
                                SPUtil.setSharedPreference(MainActivity.this, "sp_weather", arr);
                                showWeatherInfo(Utility.handleWeatherResponse(arr[0]));
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "获取天气信息失败噢噢噢噢", Toast.LENGTH_SHORT).show();
                        }
                        //刷新结束后，隐藏刷新进度条
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
    }

    public void menu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.location:
                        if (checkPermissions()) {
                            Locate();
                        } else {
                            Toast.makeText(MainActivity.this, "没有定位权限", Toast.LENGTH_LONG).show();
                        }
                        return true;
                    case R.id.city_management:
                        Intent intent = new Intent(MainActivity.this, CityManageActivity.class);
                        startActivity(intent);
                        return true;
                }
                return false;
            }
        });
        popupMenu.inflate(R.menu.menu_main);
        popupMenu.show();
    }

    public void getDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        this.month = calendar.get(Calendar.MONTH) + 1;
        this.day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    // 定位到当前城市
    private void Locate() {
        mLocationClient = new LocationClient(this);
        mLocationClient.registerLocationListener(myLocationListener);
        initLocation();
        mLocationClient.start();
    }

    void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        option.setScanSpan(0);
        mLocationClient.setLocOption(option);
    }

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            String addr = bdLocation.getAddrStr();    //获取详细地址信息
            String country = bdLocation.getCountry();    //获取国家
            String province = bdLocation.getProvince();    //获取省份
            String city = bdLocation.getCity();    //获取城市
            String district = bdLocation.getDistrict();    //获取区县
            String street = bdLocation.getStreet();    //获取街道信息

            district = district.substring(0, district.length() - 1);
            if (!SameCity(district)) {
                String[] locate = new String[str.length + 1];
                System.arraycopy(str, 0, locate, 1, str.length);
                locate[0] = district;
                SPUtil.setSharedPreference(MainActivity.this, "cityNames", locate);
                saveCities();
            }
            mLocationClient.stop(); //停止定位
        }
    }

    // 是否已经定位过
    public boolean SameCity(String city) {
        str = SPUtil.getSharedPreference(MainActivity.this, "cityNames");
        for (String aStr : str) {
            if (aStr.equals(city)) {
                return true;
            }
        }
        return false;
    }

    // 获取必应每日一图
    private void loadBingPic() {
        final String picUrl = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequests(picUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                sp = getSharedPreferences("weahter", MODE_PRIVATE);
                editor = sp.edit();

                editor.putString("bing_pic", bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(getApplicationContext()).load(bingPic).into(bgPicture);
                    }
                });
            }
        });
    }
}
