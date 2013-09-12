package org.aitek.controller.utils;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 9/12/13
 * Time: 3:48 PM
 */
public class IoUtils {

    public static String readFile(String filename) throws Exception {

        String line;
        StringBuilder sb = new StringBuilder();

        BufferedReader reader = new BufferedReader(new FileReader(filename));
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }

        return sb.toString();
    }
}
