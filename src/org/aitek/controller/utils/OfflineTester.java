package org.aitek.controller.utils;

import org.aitek.controller.core.Element;
import org.aitek.controller.core.Jukebox;
import org.aitek.controller.parsers.JsonParser;
import org.json.JSONObject;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 9/12/13
 * Time: 3:26 PM
 */
public class OfflineTester {

    public static final String ROOT_DIR = "file:///home/andrea/Dropbox/workspace/Mede8erController/";

    public static void main(String[] args) throws Exception {
        List<Jukebox> jukeboxes = JsonParser.getJukeboxes(IoUtils.readFile(ROOT_DIR + "jukeboxes.json"));
        int elementCounter = 0;

        for (Jukebox jukebox : jukeboxes) {
            System.out.println(jukebox);
            String json = IoUtils.readFile(ROOT_DIR + "jukebox.json");
            jukebox.setJsonContent(new JSONObject(json));

            int jukeboxLength = jukebox.getLength();
            for (int j = 0; j < jukeboxLength; j++) {
                Element element = JsonParser.getElement(jukebox.getElement(elementCounter));
                System.out.print(element);
                int percent = 10 + ((int) (90 * ((double) elementCounter / jukeboxLength)));
                System.out.print("\t percent=" + percent + " parsed=" + elementCounter + " total=" + jukeboxLength);
            }
        }
    }
}
