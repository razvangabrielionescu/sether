package org.mware.sponge.webui.browser.server.response;

/**
 * Created by Dan on 10/3/2017.
 */
public class CallbackResponse {
    private String message;

    /**
     * @param message
     */
    public CallbackResponse(String message) {
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
        return "CallbackResponse{" +
                "message='" + message + '\'' +
                '}';
    }
}
