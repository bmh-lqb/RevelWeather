package com.lqb.revelweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import static android.content.Context.MODE_PRIVATE;

public class SPUtil {
    private static SharedPreferences sp;
    private static SharedPreferences.Editor editor;
    private static final String FILE_NAME = "weahter";
    private static String regularEx = "#";

    public static String[] getSharedPreference(Context context, String key) {
        sp = context.getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        String values = sp.getString(key, "");
        return values.split(regularEx);
    }

    public static void setSharedPreference(Context context, String key, String[] values) {
        StringBuilder str = new StringBuilder();
        sp = context.getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        if (values != null && values.length > 0) {
            for (String value : values) {
                str.append(value);
                str.append(regularEx);
            }
            editor = sp.edit();
            editor.putString(key, str.toString());
            editor.apply();
        }
    }
}