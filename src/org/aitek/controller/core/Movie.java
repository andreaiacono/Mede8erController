package org.aitek.controller.core;

import android.graphics.Bitmap;
import android.widget.ImageView;

import org.aitek.controller.loaders.ImageShowerTask;
import org.aitek.controller.utils.Constants;
import org.aitek.controller.utils.Logger;
import org.aitek.controller.utils.MiscUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
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
    private Date date;
    private Comparator<Movie> comparator = byTitle;


    public Movie(Jukebox jukebox, String folder, String xml, Type type, String name, Date date) {
        super(jukebox, folder, xml);
        this.folder = folder;
        this.type = type;
        this.name = name;
        this.date = date;
    }

    public static Movie createFromJson(JSONObject jsonObject, Jukebox jukebox, Type type) throws Exception {
        String folder = jsonObject.optString("folder");
        String xml = jsonObject.optString("xml");
        if (xml.equals("")) {
            xml = new String(folder) + ".xml";
        }

        JSONArray video = jsonObject.optJSONArray("video");
        String name = null;
        for (int j = 0; j < video.length(); j++) {
            JSONObject item = video.getJSONObject(j);
            name = item.optString("name");
        }
        return new Movie(jukebox, folder, xml, type, name, new Date(0));
    }

    public static Movie createFromDataFile(String line, Map<String, Jukebox> jukeboxes) {

        String[] fields = line.split(Constants.DATAFILE_FIELD_SEPARATOR_REGEX);
        if (fields.length < 8) {
            return null;
        }
        Jukebox jukebox = jukeboxes.get(fields[0]);
        String sortingTitle = fields[1];
        String title = fields[2];
        String folder = fields[3];
        String name = fields[4];

        Type type = Type.valueOf(fields[5]);
        String genres = fields[6];
        String persons = fields[7];
        DateFormat format = new SimpleDateFormat("d/M/yy", Locale.ENGLISH);
        Date date = null;
        if (fields.length > 8) {
            try {
                date = format.parse(fields[8]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        Movie movie = new Movie(jukebox, folder, jukebox.getThumb(), type, name, date);
        movie.setTitle(title);
        movie.setSortingTitle(sortingTitle);
        movie.setGenres(genres);
        movie.setPersons(persons);
        movie.setDate(date);
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
        data.append(getPersons()).append(Constants.DATAFILE_FIELD_SEPARATOR);
        data.append(MiscUtils.getFormattedDate(getDate())).append("\n");
        return data.toString();
    }

    public String getXmlHttpAddress() throws Exception {

        if (type == Type.FOLDER) {
            return getNameHttpAddress() + URLEncoder.encode(name + xml, "utf-8").replace("+", "%20");
        }

        return getNameHttpAddress() + "/" + xml.replace(" ", "%20");
    }

    public String getNameHttpAddress() throws Exception {
        return "http://" + jukebox.getIpAddress() + jukebox.getSubdir() + URLEncoder.encode(getFolder(), "utf-8").replace("+", "%20").replace("%2F", "/");
    }

    public void showImage(ImageView imageView, int width, int height, float transparency) throws Exception {
        String url = getNameHttpAddress() + "/" + getJukebox().getFanart();
        imageView.setTag(width + "x" + height);
        imageView.setAlpha(transparency);
        ImageShowerTask task = new ImageShowerTask(imageView);
        task.execute(url);
    }

    @Override
    public int compareTo(Object o) {
        Movie comparedMovie = (Movie) o;
        return comparator.compare(this, comparedMovie);
    }

//    private static Comparator<Movie> byTitle = (movie1, movie2) -> movie1.getSortingTitle().compareTo(movie2.getSortingTitle());
//    private static Comparator<Movie> byDate = (movie1, movie2) -> movie1.getDate().compareTo(movie2.getDate());

    private static Comparator<Movie> byTitle = new Comparator<Movie>() {
        @Override
        public int compare(Movie movie1, Movie movie2) {
            return movie1.getSortingTitle().compareTo(movie2.getSortingTitle());
        }
    };
    private static Comparator<Movie> byDate = new Comparator<Movie>() {
        @Override
        public int compare(Movie movie1, Movie movie2) {
            return movie1.getDate().compareTo(movie2.getDate());
        }
    };

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

    public String getSortingTitle() {
        return sortingTitle;
    }

    public String getName() {
        return name;
    }

    public Date getDate() {
        return date;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public void setPersons(String persons) {
        this.persons = persons;
    }

    public void setDate(Date date) {
        this.date = date;
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
