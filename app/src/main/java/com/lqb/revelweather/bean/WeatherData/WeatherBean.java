package com.lqb.revelweather.bean.WeatherData;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherBean {
    public String status;
    public BasicBean basic;
    public NowBean now;
    public SuggestionBean suggestion;

    @SerializedName("hourly_forecast")
    public List<HourlyBean> hourList;

    @SerializedName("daily_forecast")
    public List<ForecastBean> forecastList;
}
