package org.mware.sponge.exception;

/**
 * Created by Dan on 7/11/2017.
 */
public class SystemConfigurationNotFoundException extends Exception{
    public SystemConfigurationNotFoundException(String msg) {
        super(msg);
    }

    public SystemConfigurationNotFoundException(String msg, Throwable t) {
        super(msg, t);
    }
}
