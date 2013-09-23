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
    private String folder;
    private String title;
    private Bitmap thumbnail;
    private Bitmap image;
    private String genres;
    private String names;
    private String imageName = "about.jpg";
    private String dir;

    public Movie(String address, String baseUrl, String folder, String title, Bitmap thumbnail, String genres, String names, String xml) {
        super(Type.MOVIE_FOLDER, baseUrl, folder, xml);
        this.address = address;
        this.folder = folder;
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
            String url = address + URLEncoder.encode(getFolder(), "utf-8").replace("+", "%20").replace("%2F", "/") + imageName;
            Logger.log("showing image from URL:"  + url);
            imageView.setTag(width + "x" + height);
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

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getFolder() {
        return folder;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getDir() {
        return dir;
    }
}
