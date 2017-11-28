package org.mware.sponge.browser.exception;

/**
 * Created by Dan on 10/2/2017.
 */
public class BadServerRequest extends Exception {
    public BadServerRequest(String msg) {
        super(msg);
    }

    public BadServerRequest(String msg, Throwable t) {
        super(msg, t);
    }
}
