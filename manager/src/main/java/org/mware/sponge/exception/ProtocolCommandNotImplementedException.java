package org.mware.sponge.exception;

/**
 * Created by Dan on 10/3/2017.
 */
public class ProtocolCommandNotImplementedException extends Exception {
    public ProtocolCommandNotImplementedException(String msg) {
        super(msg);
    }

    public ProtocolCommandNotImplementedException(String msg, Throwable t) {
        super(msg, t);
    }
}
