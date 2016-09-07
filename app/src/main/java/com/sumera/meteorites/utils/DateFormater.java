package com.sumera.meteorites.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by martin on 07/09/16.
 */

public class DateFormater {

    public static String getDateFromMilliseconds(long milliSeconds, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

}
