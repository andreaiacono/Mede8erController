package org.aitek.controller.core;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 9/1/13
 * Time: 10:35 PM
 */
public class Element {

    public enum Type {
        MOVIE_FOLDER, FILE, MUSIC_FOLDER, MUSIC_FILE, PLAYLIST, FOLDER, FILELINK;

    }

    private String folder;
    private String xml;

    protected Jukebox jukebox;

    public Element(Jukebox jukebox, String folder, String xml) {
        this.jukebox = jukebox;
        this.folder = folder;
        this.xml = xml;
    }

    public String getXml() {
        return xml;
    }

//    public void setBaseUrl(String baseUrl) {
//        this.baseUrl = baseUrl;
//    }

    public Jukebox getJukebox() {
        return jukebox;
    }

}
