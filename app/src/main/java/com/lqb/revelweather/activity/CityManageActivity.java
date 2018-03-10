package com.lqb.revelweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.lqb.revelweather.R;
import com.lqb.revelweather.adapter.ManageAdapter;
import com.lqb.revelweather.bean.WeatherData.WeatherBean;
import com.lqb.revelweather.util.SPUtil;
import com.lqb.revelweather.util.Utility;
import com.lqb.revelweather.view.MyManageGridView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CityManageActivity extends Activity implements View.OnClickListener {
    @Bind(R.id.city_add)
    ImageView cityAdd;

    @Bind(R.id.fcity_back)
    ImageView fcityBack;

    private MyManageGridView gridView;
    private ManageAdapter manageAdapter;
    private String[] str;
    private boolean isShowDelete = false;   //删除图标，true是显示，false是不显示

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_city);

        ButterKnife.bind(this);

        //初始化监听
        cityAdd.setOnClickListener(this);
        fcityBack.setOnClickListener(this);

        gridView = findViewById(R.id.gridview_manage);

        show();

        // 短按
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (!isShowDelete) {
                    str = SPUtil.getSharedPreference(CityManageActivity.this, "cityNames");
                    String t = str[0];
                    str[0] = str[i];
                    str[i] = t;
                    SPUtil.setSharedPreference(CityManageActivity.this, "cityNames", str);

                    Intent intent = new Intent(CityManageActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        // 长按
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                String[] cities = SPUtil.getSharedPreference(CityManageActivity.this, "cityNames");
                if (cities.length == 1) {
                    Toast.makeText(CityManageActivity.this, "不能删除该城市...", Toast.LENGTH_LONG).show();
                } else {
                    isShowDelete = !isShowDelete;
                    manageAdapter.setIsShowDelete(isShowDelete);
                    manageAdapter.notifyDataSetChanged();
                }
                return true;
            }
        });

        gridView.setOnTouchInvalidPositionListener(new MyManageGridView.OnTouchInvalidPositionListener() {
            @Override
            public boolean onTouchInvalidPosition(int motionEvent) {
                isShowDelete = false;
                manageAdapter.setIsShowDelete(false);
                manageAdapter.notifyDataSetChanged();
                return false; //不终止路由事件让父级控件处理事件
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //  跳转到添加城市
            case R.id.city_add:
                Intent intent = new Intent(CityManageActivity.this, SearchCityActivity.class);
                startActivity(intent);
                break;
            // 返回主界面
            case R.id.fcity_back:
                intent = new Intent(CityManageActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
    }

    private void show() {
        List<WeatherBean> weatherList = new ArrayList<>();
        String[] cities = SPUtil.getSharedPreference(CityManageActivity.this, "sp_weather");

        for (String city : cities) {
            WeatherBean weather = Utility.handleWeatherResponse(city);
            weatherList.add(weather);
        }

        manageAdapter = new ManageAdapter(CityManageActivity.this, weatherList);
        gridView.setAdapter(manageAdapter);
        manageAdapter.notifyDataSetChanged();
    }

    // 用户触摸了返回键
    @Override
    public void onBackPressed() {
        if (isShowDelete) {
            isShowDelete = false;
            manageAdapter.setIsShowDelete(false);
            manageAdapter.notifyDataSetChanged();
        } else {
            super.onBackPressed();
        }
    }
}