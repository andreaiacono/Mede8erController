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
    private Type type;
    private String basePath;
    private String xml;

    public Element(Type type, String path, String xml) {
        this.type = type;
        this.basePath = path;
        this.xml = xml;
    }

    public Type getType() {
        return type;
    }

    public String getBasePath() {
        return basePath;
    }

    public String getXml() {
        return xml;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    @Override
    public String toString() {
        return "Element{" +
                "type=" + type +
                ", basePath='" + basePath + '\'' +
                ", xml='" + xml + '\'' +
                '}';
    }
}
