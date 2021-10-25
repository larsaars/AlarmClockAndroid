package com.larsaars.alarmclock.utils;

import androidx.annotation.NonNull;

public class Utils {
    // returns a string which is beautifully built (with a max len)
    public static String buildBeautifulListing(@NonNull String[] items, int maxStringLen) {
        if(items.length == 0)
            return "";

        StringBuilder outBuilder = new StringBuilder(items[0]);
        String points = "…";
        for(int i = 0; i < items.length; i++) {
            boolean appendPoints = (outBuilder.length() > maxStringLen) && (maxStringLen != -1);
            if(i == items.length - 1 && i != 0 && !appendPoints)
                outBuilder.append(" & ");
            else if(i != 0 && !appendPoints)
                outBuilder.append(", ");

            if(appendPoints) {
                outBuilder.append(points);
                break;
            }else if(i != 0) {
                outBuilder.append(items[i]);
            }
        }

        return outBuilder.toString();
    }
}
