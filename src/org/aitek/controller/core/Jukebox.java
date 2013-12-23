package org.aitek.controller.core;

import org.aitek.controller.parsers.JsonParser;
import org.aitek.controller.utils.Constants;
import org.aitek.controller.utils.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
        USB, HDD, NFS, SMB;

    }

    private String id;
    private String name;
    private String ipAddress;
    private Type type;
    private Media media;
    private JSONObject jsonContent;
    private String fanart;
    private String thumb;
    private String absolutePath;
    private String subdir;

    public Jukebox(String id, String name, String ipAddress, Type type, Media media) {
        this.id = id;
        this.name = name;
        this.ipAddress = ipAddress;
        this.type = type;
        this.media = media;
    }

    public static Jukebox createFromDataFile(String line, String ipAddress) {

        String[] fields = line.split("\\" + Constants.DATAFILE_FIELD_SEPARATOR);
        if (fields.length < 8) {
            return null;
        }

        String id = fields[0];
        String name = fields[1];
        Type type = Type.MOVIE;
        if (fields[2].indexOf("|") < 0) {
            try {
                type = Type.valueOf(fields[2]);
            }
            catch (Exception ex) {
                Logger.log("type: [" + fields[2] + "]");
            }
        }
        Media media = Media.NFS;
        if (fields[3].length() == 3) {
            try {
                media = Media.valueOf(fields[3]);
            }
            catch (Exception ex) {
                Logger.log("media: [" + fields[3] + "] on " + fields[1]);
            }
        };
        String fanart = fields[4];
        String thumb = fields[5];
        String absolutePath = fields[6];
        String subDir = fields[7];

        Jukebox jukebox = new Jukebox(id, name, ipAddress, type, media);
        jukebox.setFanart(fanart);
        jukebox.setThumb(thumb);
        jukebox.setAbsolutePath(absolutePath);
        jukebox.setSubdir(subDir);
        return jukebox;
    }

    public static Map<String, Jukebox> getJukeboxMap(BufferedReader bufferedReader, String ipAddress) throws IOException {

        Map<String, Jukebox> jukeboxes = new HashMap();
        while (true) {
            String line = bufferedReader.readLine();
            if (line.equals("") || line == null) {
                return jukeboxes;
            }
            Jukebox jukebox = createFromDataFile(line, ipAddress);
            Logger.log("Created jukebox: " + jukebox + " jukebox == null " + (jukebox == null) + " - Line: [" + line + "]");
            if (jukebox != null) {
                jukeboxes.put(jukebox.getId(), jukebox);
            }
        }
    }

    public String toDataFormat() {
        return new StringBuilder(id).append(Constants.DATAFILE_FIELD_SEPARATOR)
                .append(name).append(Constants.DATAFILE_FIELD_SEPARATOR)
                .append(type).append(Constants.DATAFILE_FIELD_SEPARATOR)
                .append(media).append(Constants.DATAFILE_FIELD_SEPARATOR)
                .append(fanart).append(Constants.DATAFILE_FIELD_SEPARATOR)
                .append(thumb).append(Constants.DATAFILE_FIELD_SEPARATOR)
                .append(absolutePath).append(Constants.DATAFILE_FIELD_SEPARATOR)
                .append(subdir)
                .append("\n")
                .toString();
    }

    public String getIpAddress() {
        return ipAddress;
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

    public String getFanart() {
        return fanart;
    }

    public void setFanart(String fanart) {
        this.fanart = fanart;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public String getSubdir() {
        return subdir;
    }

    public void setSubdir(String subdir) {
        this.subdir = subdir;
    }

    @Override
    public String toString() {
        return "Jukebox{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", type=" + type +
                ", media=" + media +
                ", jsonContent=" + jsonContent +
                ", fanart='" + fanart + '\'' +
                ", thumb='" + thumb + '\'' +
                ", absolutePath='" + absolutePath + '\'' +
                ", subdir='" + subdir + '\'' +
                '}';
    }
}
