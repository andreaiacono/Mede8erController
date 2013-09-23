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

    public static List<Jukebox> getJukeboxes(String json) throws JSONException {

        int counter = 0;
        Logger.log("JSON=" + json);
        JSONArray jboxes = new JSONObject(json).optJSONArray("jukeboxes");
        int length = jboxes.length();
        List<Jukebox> jukeboxes = new ArrayList<>();

        for (int i = 0; i < length; i++) {
            jukeboxes.add(getJukebox(jboxes.getJSONObject(i)));
        }

        return jukeboxes;
    }

    public static Movie getMovie(Context context, JSONObject jsonObject, String baseUrl, String fanArt, String thumb, String jukeboxId, String dir) throws Exception {

        Element.Type type;
        String folder;
        String xml;

        if (jsonObject.optString("folder") != null) {
            type = Element.Type.MOVIE_FOLDER;
            folder = jsonObject.optString("folder");
            xml = jsonObject.optString("xml");
            if (xml.equals("")) {
                xml = folder.substring(1) + ".xml";
            }

            JSONArray video = jsonObject.optJSONArray("video");
            for (int j = 0; j < video.length(); j++) {
                JSONObject item = video.getJSONObject(j);
                Element.Type elementType = Element.Type.valueOf(item.optString("type").toUpperCase());
                String innerFolder;
                switch (elementType) {
                    case FOLDER:
                        innerFolder = item.optString("name");
                        try {
                            InputStream xmlInputStream = (InputStream) new URL(baseUrl + "/" + URLEncoder.encode(folder.substring(1), "utf-8").replace("+", "%20") + "/" + URLEncoder.encode(xml, "utf-8").replace("+", "%20")).getContent();
                            Movie movie = XmlParser.parseMovie(xmlInputStream, jukeboxId, context);
                            movie.setImageName(fanArt);
                            movie.setBaseUrl(baseUrl);
                            movie.setFolder(folder + innerFolder);
                            movie.setDir(dir + jukeboxId);
                            return movie;
                        }
                        catch (FileNotFoundException e) {
                            Logger.log("Error: " + e.getMessage());
                        }
                }

            }
//            Logger.log("inserting " + name);
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
//            Logger.log("Files length= " + files.length());
            return files.length();
        }
        Logger.log("Files length= returning 0");
        return 0;
    }
}
