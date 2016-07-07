package org.aitek.controller.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 11/06/14
 * Time: 11.00
 */
public class MiscUtils {


    public static List<String> removeEmptyStrings(List<String> values) {

        Iterator<String> iterator = values.iterator();
        while (iterator.hasNext()) {
            String item = iterator.next();
            if (item == null || item.equals("")) {
                iterator.remove();
            }
        }

        return values;
    }

    public static String getTime(int seconds) {

        int hours = seconds / 3600;
        int minutes = (seconds / 60) % 60;
        int secs = seconds % 60;
        return "" + ((hours < 10) ? "0" : "") + hours + ":" + ((minutes < 10) ? "0" : "") + minutes + ":" + ((secs < 10) ? "0" : "") + secs;
    }


    public static String getFormattedDate(Date date) {
        DateFormat format = new SimpleDateFormat("d/M/yy", Locale.ENGLISH);
        return format.format(date);
    }

    public static Date getDateFromString(String date) throws Exception {
        DateFormat format = new SimpleDateFormat("d/M/yy", Locale.ENGLISH);
        return format.parse(date);
    }
}
