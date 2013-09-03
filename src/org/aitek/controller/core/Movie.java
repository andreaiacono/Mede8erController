package org.aitek.controller.core;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import org.aitek.controller.utils.ImageDownloader;

import java.io.InputStream;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 8/16/13
 * Time: 2:32 PM
 */
public class Movie extends Element implements Comparable {

    private String title;
    private Bitmap thumbnail;
    private Bitmap image;
    private String genres;
    private String names;

    public Movie(String movieDirectory, String title, Bitmap thumbnail, String genres, String names) {
        super(Type.MOVIE_FOLDER, movieDirectory);
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

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Bitmap getImage() throws Exception {

        if (image == null) {
            image = ImageDownloader.downloadBitmap(getBasePath() + "/" + getImageName());
        }
        return image;
    }

    public String getGenres() {
        return genres;
    }

    public String getNames() {
        return names;
    }
}
