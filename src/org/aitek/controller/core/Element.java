package org.aitek.controller.core;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 9/1/13
 * Time: 10:35 PM
 */
public class Element {

    public enum Type {
        MOVIE_FOLDER, MOVIE_FILE, MUSIC_FOLDER, MUSIC_FILE, PLAYLIST, FOLDER, FILELINK;

    }

    private String baseUrl;
    private String folder;
    private String xml;
    private Type type;

    public Element(Type type, String baseUrl, String folder, String xml) {
        this.type = type;
        this.baseUrl = baseUrl;
        this.folder = folder;
        this.xml = xml;
    }

    public Type getType() {
        return type;
    }

    public String getFolder() {
        return folder;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getXml() {
        return xml;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public String toString() {
        return "Element{" +
                "type=" + type +
                ", baseUrl='" + baseUrl + '\'' +
                ", xml='" + xml + '\'' +
                '}';
    }
}
