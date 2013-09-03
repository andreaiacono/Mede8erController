package org.aitek.controller.core;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 8/16/13
 * Time: 2:32 PM
 */
public class Movie extends Element implements Comparable{

    private String title;
    private Bitmap thumbnail;
    private String genres;
    private String names;

    public Movie(String absoluteFile, String title, Bitmap thumbnail, String genres, String names) {
        super(Type.MOVIE_FOLDER, absoluteFile);
        this.title = title;
        this.thumbnail = thumbnail;
        this.genres = genres;
        this.names = names;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public int compareTo(Object o) {

        Movie comparedMovie = (Movie) o;
        return this.getTitle().compareTo(comparedMovie.getTitle());
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public Bitmap getImage() throws Exception {
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inSampleSize = 2;

        URL url = new URL("file://" + getPath() + "/about.jpg");
        InputStream inputStream = (InputStream) url.getContent();
        return BitmapFactory.decodeStream(inputStream, null, options);
    }

    public String getGenres() {
        return genres;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getNames() {
        return names;
    }
}
