package org.aitek.controller.loaders;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;
import org.aitek.controller.activities.MainActivity;
import org.aitek.controller.utils.Constants;
import org.aitek.controller.utils.ImageDownloader;
import org.aitek.controller.utils.Logger;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 9/3/13
 * Time: 3:54 PM
 */

public class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {

    private MainActivity activity;
    private String fileName;

    public BitmapDownloaderTask(MainActivity activity, String fileName) {

        this.activity = activity;
        this.fileName = fileName;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        return ImageDownloader.downloadBitmap(params[0]);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }

        FileOutputStream outputStream = null;
        try {
            outputStream = activity.openFileOutput(fileName, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream);
            activity.imageSaved();
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