package org.aitek.controller.utils;

import java.util.Iterator;
import java.util.List;

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

        int hours = (int) (seconds / 3600);
        int minutes = (int) (seconds / 60);
        int secs = (int) seconds % 60;
        return "" + ((hours < 10) ? "0" : "") + hours + ":" + ((minutes < 10) ? "0" : "") + minutes + ":" + ((secs < 10) ? "0" : "") + secs;
    }
}
