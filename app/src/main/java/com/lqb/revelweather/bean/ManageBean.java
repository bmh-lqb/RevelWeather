package com.lqb.revelweather.bean;

public class ManageBean {
    private String cityName;
    private int cityCode;
    private String tmpMin;
    private String tmpMax;

    public ManageBean(String cityName, int cityCode, String tmpMin, String tmpMax) {
        this.cityName = cityName;
        this.cityCode = cityCode;
        this.tmpMin = tmpMin;
        this.tmpMax = tmpMax;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public String getTmpMin() {
        return tmpMin;
    }

    public void setTmpMin(String tmpMin) {
        this.tmpMin = tmpMin;
    }

    public String getTmpMax() {
        return tmpMax;
    }

    public void setTmpMax(String tmpMax) {
        this.tmpMax = tmpMax;
    }
}
