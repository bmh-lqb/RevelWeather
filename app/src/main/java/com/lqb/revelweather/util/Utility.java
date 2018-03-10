package com.lqb.revelweather.util;

import android.content.Context;
import android.content.res.AssetManager;

import com.google.gson.Gson;
import com.lqb.revelweather.bean.CityBean;
import com.lqb.revelweather.bean.CityListBean;
import com.lqb.revelweather.bean.WeatherData.WeatherBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Utility {

    private final static String fileName = "City.json";

    // 读取json文件
    private static String getJson(Context context) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static List<String> getCityId(String province, String leader, Context context) {
        List<String> strList = new ArrayList<>();
        String jsonStr = getJson(context);
        CityListBean bean = new Gson().fromJson(jsonStr, CityListBean.class);

        for (CityBean cityBean : bean.cityList) {
            if (province.equals("")) {
                strList.add(cityBean.provinceZh);
            } else if (cityBean.provinceZh.equals(province) && leader.equals("")) {
                strList.add(cityBean.leaderZh);
            } else if (cityBean.provinceZh.equals(province) && cityBean.leaderZh.equals(leader)) {
                strList.add(cityBean.cityZh);
            }
        }

        List<String> list = new ArrayList<>();
        for (int i = 0; i < strList.size(); i++) {
            if (!list.contains(strList.get(i))) {
                list.add(strList.get(i));
            }
        }
        return list;
    }

    public static List<String> getAllCity(Context context) {
        List<String> strList = new ArrayList<>();
        String jsonStr = getJson(context);
        CityListBean bean = new Gson().fromJson(jsonStr, CityListBean.class);

        for (CityBean cityBean : bean.cityList) {
            strList.add(cityBean.cityZh + "，" + cityBean.leaderZh + "，" + cityBean.provinceZh);
        }
        return strList;
    }

    // 将返回的JSON数据解析成Weather实体类
    public static WeatherBean handleWeatherResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather5");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent, WeatherBean.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
