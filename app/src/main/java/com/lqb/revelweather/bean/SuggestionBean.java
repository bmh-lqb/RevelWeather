package com.lqb.revelweather.bean;

public class SuggestionBean {
    private String type;
    private int image;
    private String intro;
    private String detaile;

    public SuggestionBean(int image, String type, String intro, String detaile) {
        this.image = image;
        this.type = type;
        this.intro = intro;
        this.detaile = detaile;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getDetaile() {
        return detaile;
    }

    public void setDetaile(String detaile) {
        this.detaile = detaile;
    }
}
