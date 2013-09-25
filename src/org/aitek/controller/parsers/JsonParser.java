package org.aitek.controller.parsers;

import android.content.Context;
import org.aitek.controller.core.Element;
import org.aitek.controller.core.Jukebox;
import org.aitek.controller.core.Movie;
import org.aitek.controller.loaders.ImageSaverTask;
import org.aitek.controller.utils.Constants;
import org.aitek.controller.utils.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 8/30/13
 * Time: 4:25 PM
 */
public class JsonParser {

    public static List<Jukebox> getJukeboxes(String json, String ipAddress) throws JSONException {

        JSONArray jukeboxesJson = new JSONObject(json).optJSONArray("jukeboxes");
        int length = jukeboxesJson.length();
        List<Jukebox> jukeboxes = new ArrayList<>();

        for (int i = 0; i < length; i++) {
            jukeboxes.add(getJukebox(jukeboxesJson.getJSONObject(i), ipAddress));
        }

        return jukeboxes;
    }

    public static Movie getMovie(Jukebox jukebox, int elementCounter) throws Exception {

        JSONObject jsonObject = jukebox.getElement(elementCounter);
        if (jsonObject.optString("folder") != null) {

            JSONArray video = jsonObject.optJSONArray("video");
            for (int j = 0; j < video.length(); j++) {
                JSONObject item = video.getJSONObject(j);
                Element.Type elementType = Element.Type.valueOf(item.optString("type").toUpperCase());
                switch (elementType) {
                    case FOLDER:
                        try {

                            // first creates the movie from mede8er data (in JSON)
                            Movie movie = Movie.createFromJson(jsonObject, jukebox);

                            // then completes its data with movie info from XML
                            InputStream xmlInputStream = (InputStream) new URL(movie.getNameHttpAddress() + URLEncoder.encode(movie.getName() + movie.getXml(), "utf-8").replace("+", "%20")).getContent();
                            XmlParser.parseMovie(xmlInputStream, movie);

                            return movie;
                        }
                        catch (FileNotFoundException e) {
                            Logger.log("Error: " + e.getMessage());
                        }
                }

            }
        }

        return null;
    }

    public static Jukebox getJukebox(JSONObject jsonObject, String ipAddress) {

        String id = jsonObject.optString("id");
        String name = jsonObject.optString("name");
        Jukebox.Type type = Jukebox.Type.valueOf(jsonObject.optString("type").toUpperCase());
        Jukebox.Media media = Jukebox.Media.valueOf(jsonObject.optString("media").toUpperCase());

        return new Jukebox(id, name, ipAddress, type, media);
    }

    public static int getElementLength(JSONObject jsonContent) throws Exception {
        JSONArray files = jsonContent.optJSONArray("files");
        if (files != null) {
//            Logger.log("Files length= " + files.length());
            return files.length();
        }
        Logger.log("Files length= returning 0");
        return 0;
    }
}
