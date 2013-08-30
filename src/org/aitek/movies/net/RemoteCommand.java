package org.aitek.movies.net;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 8/29/13
 * Time: 11:32 AM
 */
public enum RemoteCommand {

    PLAY("play"), PAUSE("pause"), STOP("stop"), NEXT_TRACK("next"), PREVIOUS_TRACK("prev"),
    INFO("info"), VOL_UP("vol+"), VOL_DOWN("vol-"), AUDIO("audio"), SUBTITLE("subtitle"),
    GO_TO("goto"), FAST_FORWARD("ffw"), FAST_REVERSE("frw");

    private String remoteCommand;

    private RemoteCommand(String remoteCommand) {

        this.remoteCommand = remoteCommand;
    }

    public String getRemoteCommand() {
        return remoteCommand;
    }
}
