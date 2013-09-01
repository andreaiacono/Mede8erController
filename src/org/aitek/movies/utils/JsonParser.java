package org.aitek.movies.utils;

import org.aitek.movies.core.Element;
import org.aitek.movies.core.Jukebox;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 8/30/13
 * Time: 4:25 PM
 */
public class JsonParser {

    public static Jukebox[] getJukeboxes(String json) throws JSONException {

        int counter = 0;
        JSONArray jboxes = new JSONObject(json).optJSONArray("jukeboxes");
        int length = jboxes.length();
        Jukebox[] jukeboxes = new Jukebox[length];

        for (int i = 0; i < length; i++) {
            JSONObject jb = jboxes.getJSONObject(i);

            String id = jb.optString("id");
            String name = jb.optString("name");
            Jukebox.Type type = Jukebox.Type.valueOf(jb.optString("type"));
            Jukebox.Media media = Jukebox.Media.valueOf(jb.optString("media"));

            jukeboxes[counter++] = new Jukebox(id, name, type, media);
        }

        return jukeboxes;
    }

    public static Element getElement(String json) throws Exception {

      /*  int counter = 0;
        JSONArray files = new JSONObject(json).optJSONArray("files");
        int length = files.length();
        Jukebox[] jukeboxes = new Jukebox[length];

        for (int i = 0; i < length; i++) {
            JSONObject jb = jboxes.getJSONObject(i);

            String id = jb.optString("id");
            String name = jb.optString("name");
            Jukebox.Type type = Jukebox.Type.valueOf(jb.optString("type"));
            Jukebox.Media media = Jukebox.Media.valueOf(jb.optString("media"));

            jukeboxes[counter++] = new Jukebox(id, name, type, media);
        }
*/

        //
       // if (movie) return Movie();

        return null;
    }
}
