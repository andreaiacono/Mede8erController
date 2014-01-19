package org.aitek.controller.utils;

import org.aitek.controller.mede8er.Mede8erStatus;

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
            case Mede8erStatus.DOWN:
                message= "down";
                break;
            case Mede8erStatus.UP:
                message= "up";
                break;
            case Mede8erStatus.CONNECTED:
                message= "connected";
                break;
            case Mede8erStatus.NO_JUKEBOX:
                message= "no jukebox";
                break;
            case Mede8erStatus.FULLY_OPERATIONAL:
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
