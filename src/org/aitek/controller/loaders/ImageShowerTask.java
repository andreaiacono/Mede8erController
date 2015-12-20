package org.aitek.controller.loaders;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;
import org.aitek.controller.utils.BitmapUtils;

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
        String size = (String) imageView.getTag();

        Integer width = Integer.parseInt(size.substring(0, size.indexOf("x")));
        Integer height = Integer.parseInt(size.substring(size.indexOf("x")+1));

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