package com.lqb.revelweather.bean.WeatherData;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class ForecastBean {
    public Date date; // 日期

    public Cond cond;   // 实况天气状况

    @SerializedName("tmp")
    public Temperature temperature; // 温度

    public class Cond {
        @SerializedName("code_d")
        public String cond_code;

        @SerializedName("txt_d")
        public String cond_txt;
    }

    // 最高温度和最低温度
    public class Temperature {
        @SerializedName("max")
        public String tmp_max;

        @SerializedName("min")
        public String tmp_min;
    }
}
