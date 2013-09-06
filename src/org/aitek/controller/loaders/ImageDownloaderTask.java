package org.aitek.controller.loaders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import org.aitek.controller.activities.MainActivity;
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

public class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {

    private MainActivity activity;
    private String fileName;

    public ImageDownloaderTask(MainActivity activity, String fileName) {

        this.activity = activity;
        this.fileName = fileName;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        return downloadBitmap(params[0]);
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

    public static Bitmap downloadBitmap(String url) {
        final AndroidHttpClient client = AndroidHttpClient.newInstance(Constants.APP_VERSION + " Android");
        final HttpGet getRequest = new HttpGet(url);

        try {
            HttpResponse response = client.execute(getRequest);
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                Logger.log("Error " + statusCode + " while retrieving bitmap from " + url);
                return null;
            }

            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                try {
                    inputStream = entity.getContent();
                    final Bitmap bitmap = BitmapFactory.decodeStream(new FlushedInputStream(inputStream));
                    return bitmap;
                }
                finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    entity.consumeContent();
                }
            }
        }
        catch (Exception e) {
            getRequest.abort();
            Logger.log("Error while retrieving bitmap from " + url + ": " + e.toString());
        }
        finally {
            if (client != null) {
                client.close();
            }
        }
        return null;
    }

    static class FlushedInputStream extends FilterInputStream {

        public FlushedInputStream(InputStream inputStream) {
            super(inputStream);
        }

        @Override
        public long skip(long n) throws IOException {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) {
                    int byteRead = read();
                    if (byteRead < 0) {
                        break;  // we reached EOF
                    }
                    else {
                        bytesSkipped = 1; // we read one byte
                    }
                }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
    }
}