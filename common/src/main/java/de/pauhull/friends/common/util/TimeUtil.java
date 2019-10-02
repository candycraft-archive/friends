package de.pauhull.friends.common.util;

import java.util.concurrent.TimeUnit;

public class TimeUtil {

    public static String format(long then, long now) {
        return format(now - then);
    }

    public static String format(long duration) {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        long hours = TimeUnit.MILLISECONDS.toHours(duration);
        long days = TimeUnit.MILLISECONDS.toDays(duration);
        long years = (long) Math.floor((double) days / 360.0);

        if (seconds == 1) {
            return "1 Sekunde";
        } else if (seconds < 60) {
            return seconds + " Sekunden";
        } else if (minutes == 1) {
            return "1 Minute";
        } else if (minutes < 60) {
            return minutes + " Minuten";
        } else if (hours == 1) {
            return "1 Stunde";
        } else if (hours < 24) {
            return hours + " Stunden";
        } else if (days == 1) {
            return "1 Tag";
        } else if (days < 365) {
            return days + " Tagen";
        } else if (years == 1 && days == 365) {
            return "1 Jahr";
        } else if (days % 365 == 0) {
            return years + " Jahren";
        } else {
            return years + " Jahren, " + (days % 365) + " Tagen";
        }
    }

}
