package org.mware.sponge.webui.browser.server.request;

import org.mware.sponge.webui.browser.Protocol;

/**
 * Created by Dan on 10/3/2017.
 */
public class CallbackRequest {
    private String command;
    private String payload;
    private String sessionId;

    /**
     * @param command
     * @param payload
     */
    public CallbackRequest(String command, String payload) {
        this.command = command;
        this.payload = payload;
        this.extractSessionId();
    }

    /**
     * @return
     */
    public String getCommand() {
        return command;
    }

    /**
     * @return
     */
    public String getPayload() {
        return payload;
    }


    /**
     * @return
     */
    public String getSessionId() {
        return sessionId;
    }

    private void extractSessionId() {
        final int _idx = this.payload.indexOf(Protocol.PARAMETER_DELIMITER);
        this.sessionId = this.payload.substring(0, _idx);
        this.payload = this.payload.substring(_idx + 1);
    }

    @Override
    public String toString() {
        return "ServerRequest{" +
                "command='" + command + '\'' +
                ", payload='" + payload + '\'' +
                '}';
    }
}