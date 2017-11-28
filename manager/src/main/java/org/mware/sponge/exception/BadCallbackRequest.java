package org.mware.sponge.exception;

/**
 * Created by Dan on 10/3/2017.
 */
public class BadCallbackRequest extends Exception {
    public BadCallbackRequest(String msg) {
        super(msg);
    }

    public BadCallbackRequest(String msg, Throwable t) {
        super(msg, t);
    }
}
