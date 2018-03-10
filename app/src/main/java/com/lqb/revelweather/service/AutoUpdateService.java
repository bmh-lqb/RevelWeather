package com.lqb.revelweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.lqb.revelweather.activity.MainActivity;
import com.lqb.revelweather.bean.WeatherData.WeatherBean;
import com.lqb.revelweather.util.HttpUtil;
import com.lqb.revelweather.util.SPUtil;
import com.lqb.revelweather.util.Utility;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
    private SharedPreferences sp;
    private String[] arr;
    private int num;
    private String[] cities;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();    //更新天气信息
        updateBingPic();    //更新必应图片
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        int anHour = 8 * 60 * 60 * 100;
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        if (alarmManager != null) {
            alarmManager.cancel(pi);
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    //更新必应图片
    private void updateBingPic() {
        String picUrl = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequests(picUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(
                        AutoUpdateService.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
            }
        });

    }

    //更新天气信息
    private void updateWeather() {
        cities = SPUtil.getSharedPreference(getApplicationContext(), "cityNames");

        arr = new String[cities.length];
        num = cities.length;
        for (String city : cities) {
            requestWeather(city);
        }
    }

    public void requestWeather(final String cityName) {
        String weatherUrl = "https://free-api.heweather.com/v5/weather?city=" +
                cityName + "&key=103b06818255425087dba94ee25576d3";
        HttpUtil.sendOkHttpRequests(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                WeatherBean weatherData = Utility.handleWeatherResponse(responseText);

                if (weatherData != null && "ok".equals(weatherData.status)) {
                    num--;
                    for (int i = 0; i < cities.length; i++) {
                        if (cities[i].equals(weatherData.basic.cityName)) {
                            arr[i] = responseText;
                        }
                    }

                    if (num == 0) {
                        SPUtil.setSharedPreference(AutoUpdateService.this,"sp_weather", arr);
                    }
                }
            }
        });
    }
}
