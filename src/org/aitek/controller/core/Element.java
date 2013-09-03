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
    private String basePath;
    private String thumbnailName;
    private String imageName;

    public Element(Type type, String path) {
        this.type = type;
        this.basePath = path;
        thumbnailName = "folder.jpg";
        imageName = "about.jpg";
    }

    public Type getType() {
        return type;
    }

    public String getBasePath() {
        return basePath;
    }

    public String getThumbnailName() {
        return thumbnailName;
    }

    public String getImageName() {
        return imageName;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }
}
