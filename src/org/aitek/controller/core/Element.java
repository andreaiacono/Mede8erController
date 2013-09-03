package org.aitek.controller.core;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 9/1/13
 * Time: 10:35 PM
 */
public class Element {

    public enum Type {
        MOVIE_FOLDER, MOVIE_FILE, MUSIC_FOLDER, MUSIC_FILE, PLAYLIST;

    }
    private Type type;

    private String path;

    public Element(Type type, String path) {
        this.type = type;
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public Type getType() {
        return type;
    }

    public void setPath(String path) {
        this.path = path;
    }


}
