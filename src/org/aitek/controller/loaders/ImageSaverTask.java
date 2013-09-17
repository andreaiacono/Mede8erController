package org.aitek.controller.loaders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import org.aitek.controller.activities.MainActivity;
import org.aitek.controller.utils.BitmapUtils;
import org.aitek.controller.utils.Constants;
import org.aitek.controller.utils.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 9/3/13
 * Time: 3:54 PM
 */

public class ImageSaverTask extends AsyncTask<String, Void, Bitmap> {

    static public void downloadAndSave(String url, Context context, String fileName, int width, int height) {
        ImageSaverTask task = new ImageSaverTask(context, fileName, width, height);
        task.execute(url);
    }

    private Context context;
    private String fileName;
    private int width;
    private int height;

    public ImageSaverTask(Context context, String fileName, int width, int height) {
        this.context = context;
        this.fileName = fileName;
        this.width = width;
        this.height = height;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        try {
            return BitmapUtils.decodeBitmap(params[0], width, height);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        if (isCancelled()) {
            return;
        }

        FileOutputStream outputStream = null;
        try {
            Logger.log("filename="  + fileName);
            outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            result.compress(Bitmap.CompressFormat.PNG, 90, outputStream);
        }
        catch (FileNotFoundException e) {
            Logger.log("An error has occurred saving image " + fileName + ": " + e.getMessage());
            e.printStackTrace();
        }
        finally {
            if (outputStream != null) {

                try {
                    outputStream.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}