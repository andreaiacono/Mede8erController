package org.aitek.controller.utils;

import org.aitek.controller.mede8er.Status;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 9/17/13
 * Time: 11:43 AM
 */
public class StatusException extends Exception {

    private String message;
    private int status;

    public StatusException(int status) {
        super();
        switch(status) {
            case Status.DOWN:
                message= "down";
                break;
            case Status.UP:
                message= "up";
                break;
            case Status.CONNECTED:
                message= "connected";
                break;
            case Status.NO_JUKEBOX:
                message= "no jukebox";
                break;
            case Status.FULLY_OPERATIONAL:
                message= "fully operational";
                break;
        }
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return "The Mede8er is in status: " + message;
    }
}
