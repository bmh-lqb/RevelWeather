package com.lqb.revelweather.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lqb.revelweather.R;
import com.lqb.revelweather.adapter.RecyclerViewAdapter;
import com.lqb.revelweather.util.DialogUtil;
import com.lqb.revelweather.util.SPUtil;
import com.lqb.revelweather.util.Utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchCityActivity extends Activity implements View.OnClickListener, RecyclerViewAdapter.HolderCilck {
    @Bind(R.id.search_bg)
    ImageView searchBg;

    @Bind(R.id.et_delete)
    ImageView etDelete;

    @Bind(R.id.city_back)
    ImageView btnCityBack;

    @Bind(R.id.title_city_name)
    TextView titleCityName;

    @Bind(R.id.item_back)
    ImageView btnItemBack;

    @Bind(R.id.et_search)
    EditText etSearch;

    @Bind(R.id.btn_search)
    Button btnSearch;

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;

    @Bind(R.id.rl_none)
    RelativeLayout rlNone;

    private RecyclerViewAdapter adapter;
    private List<String> list = new ArrayList<>();
    private String type;
	private InputMethodManager imm;

    //选中的省份
    private String selectedProvince;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  // 隐藏或显示软键盘
        init();

        String[] spWeather = SPUtil.getSharedPreference(SearchCityActivity.this, "sp_weather");
        if (spWeather[0].equals("")) {
            btnCityBack.setVisibility(View.GONE);
            Toast.makeText(SearchCityActivity.this, "请选择城市", Toast.LENGTH_LONG).show();
        }

        btnCityBack.setOnClickListener(this);
        btnItemBack.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        etDelete.setOnClickListener(this);

        queryProvinces();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerViewAdapter(this, list);
        adapter.setHolderCilck(SearchCityActivity.this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(0);

        etSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){
                    Log.e("Tag","失去焦点");
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    etDelete.setVisibility(View.GONE);
                }else {
                    Log.e("Tag","获取焦点");
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    if (etSearch != null) {
                        etDelete.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0){
                    etDelete.setVisibility(View.VISIBLE);
                }else{
                    etDelete.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.city_back:
                Intent intent = new Intent(this, CityManageActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.item_back:
                if (type.equals("leader") || type.equals("search")) {
                    etSearch.setText("");
                    rlNone.setVisibility(View.GONE);
                    queryProvinces();
                    adapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(0);
                } else if (type.equals("city")) {
                    queryLeaders(selectedProvince);
                }
                break;
            case R.id.btn_search:
                SeaerchArea();
                break;
            case R.id.et_delete:
                etSearch.setText(null);
                break;
        }
    }

    private void SeaerchArea() {
        DialogUtil.showProgressDialog(this, "正在搜索...");
        String etText = etSearch.getText().toString();
        List<String> allCityList = new ArrayList<>();
        allCityList.addAll(Utility.getAllCity(this));
        list.clear();
        for (int i = 0; i < allCityList.size(); i++) {
            if (allCityList.get(i).contains(etText) && !etText.equals("")) {
                list.add(allCityList.get(i));
            }
        }
        if (list.size() == 0) {
            rlNone.setVisibility(View.VISIBLE);
        } else {
            rlNone.setVisibility(View.GONE);
        }
        DialogUtil.closeProgressDialog();
        type = "search";
        titleCityName.setText("搜索结果");
        adapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(0);

    }

    public void init() {
        btnCityBack  = findViewById(R.id.city_back);
        titleCityName = findViewById(R.id.title_city_name);
        btnItemBack = findViewById(R.id.item_back);
        etSearch = findViewById(R.id.et_search);
        btnSearch = findViewById(R.id.btn_search);
        recyclerView = findViewById(R.id.recyclerView);
        rlNone = findViewById(R.id.rl_none);
    }

    // recycleview的点击事件
    @Override
    public void click(int position) {
        switch (type) {
            case "province":
                selectedProvince = list.get(position);
                queryLeaders(selectedProvince);
                break;
            case "leader":
                queryCities(selectedProvince, list.get(position));
                break;
            case "city":
            case "search":
                String selectedCity;
                if (type.equals("city")) {
                    selectedCity = list.get(position);
                } else {
                    selectedCity = list.get(position).split("，")[0];
                }

                String[] str = SPUtil.getSharedPreference(SearchCityActivity.this, "cityNames");

                if (str[0].equals("")) {
                    str[0] = selectedCity;
                    SPUtil.setSharedPreference(SearchCityActivity.this, "cityNames", str);
                } else {
                    if (Arrays.asList(str).contains(selectedCity)) {
                        Toast.makeText(SearchCityActivity.this, "已有该城市，请重新选择...", Toast.LENGTH_LONG).show();
                        break;
                    }
                    String[] cityNames = new String[str.length + 1];
                    System.arraycopy(str, 0, cityNames, 1, str.length);
                    cityNames[0] = selectedCity;

                    SPUtil.setSharedPreference(SearchCityActivity.this, "cityNames", cityNames);
                }
                Intent intent = new Intent(SearchCityActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    // 显示所有省
    private void queryProvinces() {
        type = "province";
        list.clear();
        list.addAll(Utility.getCityId("", "", this));
        titleCityName.setText("中国");
    }

    // 显示选中的省的所有市
    private void queryLeaders(String province) {
        type = "leader";
        list.clear();
        list.addAll(Utility.getCityId(province, "", this));
        titleCityName.setText(province);
        adapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(0);
    }

    // 显示选中的市的所有县
    private void queryCities(String province, String Leader) {
        titleCityName.setText(Leader);
        list.clear();
        list.addAll(Utility.getCityId(province, Leader, this));
        type = "city";
        adapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(0);
    }

    // 软键盘
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                //使EditText触发一次失去焦点事件
                v.setFocusable(false);
                v.setFocusableInTouchMode(true);
                return true;
            }
        }
        return false;
    }

    // 用户触摸了返回键
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.e("hehe  ", "按下了back键   onBackPressed()");
    }
}
