package org.aitek.controller.loaders;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import org.aitek.controller.activities.MainActivity;
import org.aitek.controller.utils.Constants;
import org.aitek.controller.utils.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 9/3/13
 * Time: 3:24 PM
 */
public class ImageDownloader {

    static public void downloadAndSave(String url, MainActivity activity, String fileName) {
        ImageDownloaderTask task = new ImageDownloaderTask(activity, fileName);
        task.execute(url);
    }
}
