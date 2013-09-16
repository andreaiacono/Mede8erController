package org.aitek.controller.core;

import android.graphics.Bitmap;
import android.widget.ImageView;
import org.aitek.controller.loaders.ImageShowerTask;
import org.aitek.controller.utils.Logger;

import java.net.URLEncoder;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 8/16/13
 * Time: 2:32 PM
 */
public class Movie extends Element implements Comparable {

    private String address;
    private String title;
    private Bitmap thumbnail;
    private Bitmap image;
    private String genres;
    private String names;
    private String imageName = "about.jpg";

    public Movie(String address, String movieDirectory, String title, Bitmap thumbnail, String genres, String names, String xml) {
        super(Type.MOVIE_FOLDER, movieDirectory, xml);
        this.address = address;
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

    public void showImage(ImageView imageView, int width, int height) throws Exception {

        if (image == null) {
            String url = address + URLEncoder.encode(getBasePath() + "/" + imageName, "utf-8").replace("+", "%20");
            Logger.log("showing image from URL:"  + url);
            imageView.setTag(0, width);
            imageView.setTag(1, height);
            ImageShowerTask task = new ImageShowerTask(imageView);
            task.execute(url);
        }
    }

    public String getGenres() {
        return genres;
    }

    public String getNames() {
        return names;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
