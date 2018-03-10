package com.lqb.revelweather.util;

import android.support.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DayUtils {
    private static final int TODAY = 0; // 今天
    private static final int TOMORROWDAT = 1;   // 明天
    private static final int AFTERTOMORROW = 2; // 后天
    private static final int OTHER_DAY = 99;

    private static ThreadLocal<SimpleDateFormat> DateLocal = new ThreadLocal<>();

    // 读取日期的格式
    private static SimpleDateFormat getDateFormat() {
        if (null == DateLocal.get()) {
            DateLocal.set(new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA));
        }
        return DateLocal.get();
    }

    // 调用显示日期
    @Nullable
    public static String getTitleDay(String day){
        try {
            switch (JudgmentDay(day)) {
                case TODAY :
                    return "今天";
                case TOMORROWDAT :
                    return "明天";
                case AFTERTOMORROW :
                    return "后天";
                default:
                    return "昨天";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

     // 判断日期
    private static int JudgmentDay(String day) throws ParseException {
        Calendar pre = Calendar.getInstance();
        Date predate = new Date(System.currentTimeMillis());
        pre.setTime(predate);

        Calendar cal = Calendar.getInstance();
        Date date = getDateFormat().parse(day);
        cal.setTime(date);

        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR) - pre.get(Calendar.DAY_OF_YEAR);

            switch (diffDay) {
                case TODAY : {
                    return TODAY;
                }
                case TOMORROWDAT : {
                    return TOMORROWDAT;
                }

                case AFTERTOMORROW : {
                    return AFTERTOMORROW;
                }
            }
        }
        return OTHER_DAY;
    }

}
