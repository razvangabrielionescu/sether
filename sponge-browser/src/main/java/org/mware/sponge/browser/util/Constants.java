package org.mware.sponge.browser.util;

/**
 * Created by Dan on 10/2/2017.
 */
public class Constants {
    public static final String USAGE = "Usage: java -Djava.rmi.server.hostname=localhost -Dprocess.java.home=<JAVAFX_JDK_HOME> -jar sponge-browser.jar <listen_port> <parent_host>";
    public static final String RMI_HOST = "localhost";
    public static final String RMI_PORTS_MESSAGE = "READY-ON-PORTS";
    public static final int CALLBACK_SERVER_PORT = 6790;
    public static final int MAX_FIND_PORT_RETRIES = 5;
}
