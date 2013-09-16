package org.aitek.controller.loaders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.widget.ImageView;
import org.aitek.controller.activities.MainActivity;
import org.aitek.controller.utils.BitmapUtils;
import org.aitek.controller.utils.Constants;
import org.aitek.controller.utils.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import java.io.*;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 9/3/13
 * Time: 3:54 PM
 */

public class ImageShowerTask extends AsyncTask<String, Void, Bitmap> {

    private ImageView imageView = null;

    public ImageShowerTask(ImageView imageView) {
        this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        Integer width = (Integer) imageView.getTag(0);
        Integer height = (Integer) imageView.getTag(1);

        try {
            return BitmapUtils.decodeBitmap(strings[0], width, height);
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
        imageView.setImageBitmap(result);
    }
}