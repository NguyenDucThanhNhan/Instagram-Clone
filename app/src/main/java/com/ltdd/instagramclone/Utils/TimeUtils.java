package com.ltdd.instagramclone.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TimeUtils {
    public static String getTimeAgo(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());

        try {
            Date date = sdf.parse(dateStr);
            long time = date.getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;

            // Tính toán số giây, phút, giờ, ngày và tuần
            long seconds = TimeUnit.MILLISECONDS.toSeconds(diff);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
            long hours = TimeUnit.MILLISECONDS.toHours(diff);
            long days = TimeUnit.MILLISECONDS.toDays(diff);
            long weeks = days / 7;

            // Kiểm tra và trả về chuỗi thời gian
            if (seconds < 60) {
                return seconds + (seconds == 1 ? " second" : " seconds");
            } else if (minutes < 60) {
                return minutes + (minutes == 1 ? " minute" : " minutes");
            } else if (hours < 24) {
                return hours + (hours == 1 ? " hour" : " hours");
            } else if (days < 7) {
                return days + (days == 1 ? " day" : " days");
            } else {
                SimpleDateFormat sdfOut = new SimpleDateFormat("MMM dd", Locale.getDefault());
                return sdfOut.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }
}
