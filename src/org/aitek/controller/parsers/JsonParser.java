package org.aitek.controller.parsers;

import org.aitek.controller.core.Element;
import org.aitek.controller.core.Jukebox;
import org.aitek.controller.core.Movie;
import org.aitek.controller.utils.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 8/30/13
 * Time: 4:25 PM
 */
public class JsonParser {

    public static List<Jukebox> getJukeboxes(String json) throws JSONException {

        int counter = 0;
        JSONArray jboxes = new JSONObject(json).optJSONArray("jukeboxes");
        int length = jboxes.length();
        List<Jukebox> jukeboxes = new ArrayList<>();

        for (int i = 0; i < length; i++) {
            jukeboxes.add(getJukebox(jboxes.getJSONObject(i)));
        }

        return jukeboxes;
    }

    public static Element getElement(JSONObject jsonObject) {

        Element.Type type;
        String name;
        String xml;

        if (jsonObject.optString("folder") != null) {
            type = Element.Type.MOVIE_FOLDER;
            name = jsonObject.optString("folder");
            xml = jsonObject.optString("xml");

            JSONArray video = jsonObject.optJSONArray("video");
            Logger.log("inserting " + name);
            return new Movie(name, name, null, "", "", "");
        }

        return null;
    }

    public static Jukebox getJukebox(JSONObject jsonObject) {

        String id = jsonObject.optString("id");
        String name = jsonObject.optString("name");
        Jukebox.Type type = Jukebox.Type.valueOf(jsonObject.optString("type").toUpperCase());
        Jukebox.Media media = Jukebox.Media.valueOf(jsonObject.optString("media").toUpperCase());

        return new Jukebox(id, name, type, media);
    }

    public static int getElementLength(JSONObject jsonContent) throws Exception {
        JSONArray files = jsonContent.optJSONArray("files");
        if (files != null) {
            Logger.log("Files length= " + files.length());
            return files.length();
        }
        Logger.log("Files length= returning 0");
        return 0;
    }
}
