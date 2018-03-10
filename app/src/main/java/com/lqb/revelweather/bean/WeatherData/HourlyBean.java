package com.lqb.revelweather.bean.WeatherData;

import com.google.gson.annotations.SerializedName;

public class HourlyBean {
    @SerializedName("date")
    public String time; // 逐小时

    public Cond cond;

    public class Cond {
        public String code;

        public String txt;
    }

    @SerializedName("tmp")
    public String temperature;   //当前时间的温度
}
