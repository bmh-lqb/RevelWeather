package com.lqb.revelweather.bean.WeatherData;

import com.google.gson.annotations.SerializedName;

public class SuggestionBean {
    @SerializedName("comf")
    public More comfort;   // 舒适度

    @SerializedName("cw")
    public More carWash;   // 洗车

    @SerializedName("drsg")
    public More dress;     // 穿衣

    @SerializedName("flu")
    public More influenza; // 感冒

    public More sport;     // 运动

    @SerializedName("trav")
    public More travel;    // 旅游

    @SerializedName("uv")
    public More ultraviolet;   // 紫外线

    @SerializedName("air")
    public More pollution; // 空气污染

    public class More {
        @SerializedName("brf")
        public String intro;    // 生活指数简介

        @SerializedName("txt")
        public String detaile;  // 生活指数详细描述
    }
}
