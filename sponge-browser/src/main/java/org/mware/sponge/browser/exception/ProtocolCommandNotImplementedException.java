package org.mware.sponge.browser.exception;

/**
 * Created by Dan on 10/2/2017.
 */
public class ProtocolCommandNotImplementedException extends Exception {
    public ProtocolCommandNotImplementedException(String msg) {
        super(msg);
    }

    public ProtocolCommandNotImplementedException(String msg, Throwable t) {
        super(msg, t);
    }
}
