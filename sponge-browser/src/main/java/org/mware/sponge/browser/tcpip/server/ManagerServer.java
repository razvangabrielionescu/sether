package org.mware.sponge.browser.tcpip.server;

import org.mware.sponge.browser.ProcessController;
import org.mware.sponge.browser.SpongeBrowserDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ServerSocket;

/**
 * Created by Dan on 10/2/2017.
 */
public class ManagerServer {
    private static final Logger log = LoggerFactory.getLogger(ManagerServer.class);

    private int port;
    private ProcessController processController;

    public void startServer() throws Exception {
        ServerSocket socket = new ServerSocket(this.port);
        new Thread(new ManagerRunnable(processController, socket)).start();
        log.info("Sponge browser manager server started on port: " + this.port);
    }

    /**
     * @return
     */
    public int getPort() {
        return port;
    }

    /**
     * @param port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @return
     */
    public ProcessController getProcessController() {
        return processController;
    }

    /**
     * @param processController
     */
    public void setProcessController(ProcessController processController) {
        this.processController = processController;
    }
}
