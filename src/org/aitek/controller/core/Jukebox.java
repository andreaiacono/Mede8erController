package org.aitek.controller.core;

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
    String jsonContent;


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

    public String getJsonContent() {
        return jsonContent;
    }

    public void setJsonContent(String jsonContent) {
        this.jsonContent = jsonContent;
    }

    public int getLength() {
        if (jsonContent == null) {
            return -1;
        }

        // TODO parse json
        return 10;
    }

    public String getElement(int counter) {

    // TODO parse json
        return null;

    }


}
