package org.aitek.controller.utils;

import org.aitek.controller.core.Element;
import org.aitek.controller.core.Jukebox;
import org.aitek.controller.parsers.JsonParser;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 9/12/13
 * Time: 3:26 PM
 */
public class OfflineTester {

    public static final String ROOT_DIR = "/home/andrea/Dropbox/workspace/Mede8erController/";
    private static String mede8erIp = "192.168.1.5";

    public static void main(String[] args) throws Exception {
        List<Jukebox> jukeboxes = JsonParser.getJukeboxes(IoUtils.readFile(ROOT_DIR + "jukeboxes.json"));
        int elementCounter = 0;

        for (Jukebox jukebox : jukeboxes) {
            System.out.println(jukebox);
            String json = IoUtils.readFile(ROOT_DIR + "jukebox.json");
            jukebox.setJsonContent(new JSONObject(json));

            int jukeboxLength = jukebox.getLength();
            for (int j = 0; j < jukeboxLength; j++) {
                String url = "http://" + mede8erIp;
                JSONObject meta = new JSONObject(json).optJSONObject("meta");
                url = url + meta.optString("subdir");
                Element element = JsonParser.getElement(null, jukebox.getElement(elementCounter++), url, meta.optString("fanart"), meta.optString("thumb"));
                System.out.print(element);
                int percent = 10 + ((int) (90 * ((double) elementCounter / jukeboxLength)));
                System.out.println("\t percent=" + percent + " parsed=" + elementCounter + " total=" + jukeboxLength);
            }
        }
    }
}
