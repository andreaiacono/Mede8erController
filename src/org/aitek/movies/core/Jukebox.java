package org.aitek.movies.core;

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
    Media meddia;

    public Jukebox(String id, String name, Type type, Media meddia) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.meddia = meddia;
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

    public Media getMeddia() {
        return meddia;
    }


}
