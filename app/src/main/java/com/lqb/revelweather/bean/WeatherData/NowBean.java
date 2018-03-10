package com.lqb.revelweather.bean.WeatherData;

import com.google.gson.annotations.SerializedName;

public class NowBean {
    // 温度
    @SerializedName("tmp")
    public String temperature;

    public Cond cond;

    public class Cond {
        @SerializedName("txt")
        public String cond_txt;

        @SerializedName("code")
        public String cond_code;
    }
}
