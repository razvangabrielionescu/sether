package org.mware.sponge.exception;

/**
 * Created by Dan on 7/24/2017.
 */
public class UnimplementedCommitterException extends Exception {
    public UnimplementedCommitterException(String msg) {
        super(msg);
    }

    public UnimplementedCommitterException(String msg, Throwable t) {
        super(msg, t);
    }
}
