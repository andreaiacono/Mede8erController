package org.aitek.movies.net;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 8/26/13
 * Time: 12:27 PM
 */
public class Response {

    private Value value;
    private String content;

    public enum Value {
        OK, ERR_UNKNOWN_TAG, ERR_NO_MEDIA, ERR_OPEN_DIR, ERR_FAIL, ERR_PLAYING;
    }

    public Response(Value value, String content) {
        this.value = value;

        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public Value getValue() {
        return value;
    }

}
