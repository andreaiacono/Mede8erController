package org.aitek.controller.mede8er.net;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 8/26/13
 * Time: 12:27 PM
 */
public class Response {
    private Code code;

    private String content;
    public enum Code {
        OK, FAIL, ERR_UNKNOWN_TAG, ERR_NO_MEDIA, ERR_OPEN_DIR, ERR_FAIL, ERR_PLAYING, ERR_RC_UNKNOWN, ERR_NOT_PLAYING, STATUS, EMPTY;

    }
    public Response(Code code, String content) {
        this.code = code;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public Code getCode() {
        return code;
    }

    public boolean isEmpty() {
        return code.toString().toUpperCase().equals(Code.EMPTY);
    }

    @Override
    public String toString() {
        return "Response{" +
                "code=" + code +
                ", content='" + content + '\'' +
                '}';
    }

}
