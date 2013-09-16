package org.aitek.controller.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 9/16/13
 * Time: 12:29 PM
 */
public class BitmapUtils {


    public static Bitmap decodeBitmap(String url, int width, int height) throws Exception {

        return decodeSampledBitmap(new URL(url), -1, width, height);
    }

    public static Bitmap decodeBitmap(URL url, int width, int height) throws Exception {

        return decodeSampledBitmap(url, -1, width, height);
    }

    public static Bitmap decodeBitmap(Resources res, int resId, int width, int height) throws IOException {

        return decodeSampledBitmap(res, resId, width, height);
    }


    public static Bitmap decodeSampledBitmap(Object source, int resourceId, int width, int height) throws IOException {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        // options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inJustDecodeBounds = true;

        if (source instanceof Resources) {
            BitmapFactory.decodeResource((Resources) source, resourceId, options);
        }
        else if (source instanceof URL) {
            BitmapFactory.decodeStream((InputStream) ((URL)source).getContent(), null, options);
        }

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, width, height);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        if (source instanceof Resources) {
            return BitmapFactory.decodeResource((Resources) source, resourceId, options);
        }
        else if (source instanceof URL) {
            return BitmapFactory.decodeStream((InputStream) ((URL)source).getContent(), null, options);
        }

        // should not arrive here
        return null;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }




//    public static Bitmap downloadBitmap(String url) {
//        Logger.log("downloading image at URL " + url);
//        final AndroidHttpClient client = AndroidHttpClient.newInstance(Constants.APP_VERSION + " Android");
//        final HttpGet getRequest = new HttpGet(url);
//
//        try {
//            HttpResponse response = client.execute(getRequest);
//            final int statusCode = response.getStatusLine().getStatusCode();
//            if (statusCode != HttpStatus.SC_OK) {
//                throw new Exception("Error " + statusCode + " while retrieving bitmap from " + url);
//            }
//
//            final HttpEntity entity = response.getEntity();
//            if (entity != null) {
//                InputStream inputStream = null;
//                try {
//                    inputStream = entity.getContent();
//                    final Bitmap bitmap = BitmapFactory.decodeStream(new BitmapUtils.FlushedInputStream(inputStream));
//                    return bitmap;
//                }
//                finally {
//                    if (inputStream != null) {
//                        inputStream.close();
//                    }
//                    entity.consumeContent();
//                }
//            }
//        }
//        catch (Exception e) {
//            getRequest.abort();
//            Logger.log("Error while retrieving bitmap from " + url + ": " + e.toString());
//        }
//        finally {
//            if (client != null) {
//                client.close();
//            }
//        }
//        return null;
//    }
//
//    public static class FlushedInputStream extends FilterInputStream {
//
//        public FlushedInputStream(InputStream inputStream) {
//            super(inputStream);
//        }
//
//        @Override
//        public long skip(long n) throws IOException {
//            long totalBytesSkipped = 0L;
//            while (totalBytesSkipped < n) {
//                long bytesSkipped = in.skip(n - totalBytesSkipped);
//                if (bytesSkipped == 0L) {
//                    int byteRead = read();
//                    if (byteRead < 0) {
//                        break;  // we reached EOF
//                    } else {
//                        bytesSkipped = 1; // we read one byte
//                    }
//                }
//                totalBytesSkipped += bytesSkipped;
//            }
//            return totalBytesSkipped;
//        }
//    }
}
