package org.aitek.controller.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.*;

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

    public static Bitmap readImageFile(String filename) {
        return BitmapFactory.decodeFile(filename);
    }

    public static void saveImageFile(String filename, Bitmap image) {
        try {
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(filename, true));
            image.compress(Bitmap.CompressFormat.PNG, 90, outputStream);
            outputStream.close();
        }
        catch (Exception ex) {
            Logger.log("An error has occurred saving bitmap on [" + filename + "]: " + ex.getMessage());
        }
    }

    public static String normalizeFilename(String name) {
        return name.toLowerCase().replaceAll(" ", "_");
    }

    public static boolean isFileExisting(Context context, String filename) {

        FileInputStream in = null;
        try {
            in = context.openFileInput(filename);
        }
        catch (FileNotFoundException e) {
            return false;
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (IOException e) {
            }
        }
        return true;
    }
}
