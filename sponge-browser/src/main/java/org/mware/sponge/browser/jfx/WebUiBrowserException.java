package org.mware.sponge.browser.jfx;

public class WebUiBrowserException extends RuntimeException {
    public WebUiBrowserException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public WebUiBrowserException(String message) {
        super(message);
    }
}
