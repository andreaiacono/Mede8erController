package org.aitek.controller.core;

import android.graphics.Bitmap;
import android.widget.ImageView;
import org.aitek.controller.loaders.ImageShowerTask;
import org.aitek.controller.utils.Constants;
import org.aitek.controller.utils.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.lang.annotation.ElementType;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 8/16/13
 * Time: 2:32 PM
 */
public class Movie extends Element implements Comparable {

    private String folder;
    private String sortingTitle;
    private String title;
    private Type type;
    private String name;
    private Bitmap thumbnail;
    private String genres;
    private String persons;


    public Movie(Jukebox jukebox, String folder, String xml, Type type, String name) {
        super(jukebox, folder, xml);
        this.folder = folder;
        this.type = type;
        this.name = name;
    }

    public static Movie createFromJson(JSONObject jsonObject, Jukebox jukebox, Type type) throws Exception {
        String folder = jsonObject.optString("folder");
        String xml = jsonObject.optString("xml");
        if (xml.equals("")) {
            xml = new String(folder) +".xml";
        }

        JSONArray video = jsonObject.optJSONArray("video");
//        Type type = null;
        String name = null;
        for (int j = 0; j < video.length(); j++) {
            JSONObject item = video.getJSONObject(j);
//            type = Type.valueOf(item.optString("type").toUpperCase());
            name = item.optString("name");
        }
        return new Movie(jukebox, folder, xml, type, name);
    }

    public static Movie createFromDataFile(String line, Map<String, Jukebox> jukeboxes) {

        String[] fields = line.split(Constants.DATAFILE_FIELD_SEPARATOR_REGEX);
        if (fields.length < 8) {
            return null;
        }
        Jukebox jukebox = jukeboxes.get(fields[0]);
        String sortingTitle = fields[1];
        String title =  fields[2];
        String folder = fields[3];
        String name = fields[4];

        Type type = Type.valueOf(fields[5]);
        String genres = fields[6];
        String persons = fields[7];

        Movie movie = new Movie(jukebox, folder, jukebox.getThumb(), type, name);
        movie.setTitle(title);
        movie.setSortingTitle(sortingTitle);
        movie.setGenres(genres);
        movie.setPersons(persons);
        return movie;
    }

    public String toDataFormat() {
        StringBuilder data = new StringBuilder();
        data.append(getJukebox().getId()).append(Constants.DATAFILE_FIELD_SEPARATOR);
        data.append(getSortingTitle()).append(Constants.DATAFILE_FIELD_SEPARATOR);
        data.append(getTitle()).append(Constants.DATAFILE_FIELD_SEPARATOR);
        data.append(getFolder()).append(Constants.DATAFILE_FIELD_SEPARATOR);
        data.append(getName()).append(Constants.DATAFILE_FIELD_SEPARATOR);
        data.append(getType().toString()).append(Constants.DATAFILE_FIELD_SEPARATOR);
        data.append(getGenres()).append(Constants.DATAFILE_FIELD_SEPARATOR);
        data.append(getPersons()).append("\n");
        return data.toString();
    }

    public String getXmlHttpAddress() throws Exception {

        if (type == Type.FOLDER) {
            return getNameHttpAddress() +  URLEncoder.encode(name + xml, "utf-8").replace("+", "%20");
        }

        return getNameHttpAddress() + "/" + xml.replace(" ", "%20");
    }

    public String getNameHttpAddress() throws Exception {
        return "http://" + jukebox.getIpAddress() + jukebox.getSubdir() + URLEncoder.encode(getFolder(), "utf-8").replace("+", "%20").replace("%2F", "/");
    }

    public void showImage(ImageView imageView, int width, int height, float transparency) throws Exception {
        String url = getNameHttpAddress() + "/" + getJukebox().getFanart();
        Logger.log("showing image from URL:" + url);
        imageView.setTag(width + "x" + height);
        imageView.setAlpha(transparency);
        ImageShowerTask task = new ImageShowerTask(imageView);
        task.execute(url);
    }

    @Override
    public int compareTo(Object o) {

        Movie comparedMovie = (Movie) o;
        return this.getSortingTitle().compareTo(comparedMovie.getSortingTitle());
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getGenres() {
        return genres.replace("\n", "");
    }

    public String getPersons() {
        return persons;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getSortingTitle() {
        return sortingTitle;
    }

    public String getName() {
        return name;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public void setPersons(String persons) {
        this.persons = persons;
    }

    public Type getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSortingTitle(String sortingTitle) {
        this.sortingTitle = sortingTitle;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "folder='" + folder + '\'' +
                ", sortingTitle='" + sortingTitle + '\'' +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", thumbnail=" + thumbnail +
                ", genres='" + genres + '\'' +
                ", persons='" + persons + '\'' +
                '}';
    }

}
