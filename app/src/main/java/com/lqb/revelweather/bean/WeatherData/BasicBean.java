package com.lqb.revelweather.bean.WeatherData;

import com.google.gson.annotations.SerializedName;

public class BasicBean {
    // 城市名
    @SerializedName("city")
    public String cityName;

    // 城市id
    @SerializedName("id")
    public String weatherId;

    // 上属城市
    @SerializedName("leader")
    public String parent;

}
