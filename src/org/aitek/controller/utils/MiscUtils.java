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
}
