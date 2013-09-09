package org.aitek.controller.core;

import org.aitek.controller.parsers.JsonParser;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 8/30/13
 * Time: 4:18 PM
 */
public class Jukebox {

    public enum Type {
        MOVIE, MUSIC, SHARE, SERIES, UNKNOWN;

    }
    public enum Media {
        USB,HDD,NFS,SMB;

    }
    String id;

    String name;
    Type type;
    Media media;
    JSONObject jsonContent;


    public Jukebox(String id, String name, Type type, Media media) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.media = media;
    }

    public String getId() {
        return id;
    }


    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public Media getMedia() {
        return media;
    }

    public JSONObject getJsonContent() {
        return jsonContent;
    }

    public void setJsonContent(JSONObject jsonContent) {
        this.jsonContent = jsonContent;
    }

    public int getLength() throws Exception {
        if (jsonContent == null) {
            return 0;
        }

        return JsonParser.getElementLength(jsonContent);
    }

    public JSONObject getElement(int counter) throws Exception {

        JSONArray files = jsonContent.optJSONArray("files");
        if (files != null) {
            return (JSONObject) files.get(counter);
        }

        return new JSONObject();
    }


}
