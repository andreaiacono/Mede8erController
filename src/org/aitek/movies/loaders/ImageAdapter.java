package org.aitek.movies.loaders;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import org.aitek.movies.core.MoviesManager;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 8/16/13
 * Time: 5:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class ImageAdapter extends BaseAdapter {

    private Context context;

    public ImageAdapter(Context c) {
        context = c;
    }

    public int getCount() {
        return MoviesManager.getMoviesCount();
    }

    @Override
    public Object getItem(int i) {
        return MoviesManager.getMovie(i).getThumbnail();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(180, 220));
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            imageView.setPadding(7, 7, 0, 0);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageBitmap(MoviesManager.getMovie(position).getThumbnail());
        return imageView;
    }

}