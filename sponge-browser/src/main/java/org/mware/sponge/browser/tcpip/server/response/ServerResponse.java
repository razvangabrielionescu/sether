package org.mware.sponge.browser.tcpip.server.response;

/**
 * Created by Dan on 10/2/2017.
 */
public class ServerResponse {
    private String message;

    /**
     * @param message
     */
    public ServerResponse(String message) {
        this.message = message;
    }

    /**
     * @return
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ServerResponse{" +
                "message='" + message + '\'' +
                '}';
    }
}
