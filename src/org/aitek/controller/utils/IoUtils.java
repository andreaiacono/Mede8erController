package org.aitek.controller.utils;

import android.app.Activity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;

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

    public static String readPrivateFile(String filename, Activity activity) throws Exception {

        String line;
        StringBuilder sb = new StringBuilder();
        FileInputStream in = activity.openFileInput(filename);
        InputStreamReader inputStreamReader = new InputStreamReader(in);

        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line).append("\n");
        }

        return sb.toString();
    }
}
