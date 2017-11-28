package org.mware.sponge.browser.tcpip.server;

import org.mware.sponge.browser.ProcessController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Dan on 10/2/2017.
 */
public class ManagerRunnable implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(ManagerRunnable.class);

    private ServerSocket socket;
    private ProcessController processController;

    /**
     * @param socket
     */
    public ManagerRunnable(ProcessController processController, ServerSocket socket) {
        super();
        this.processController = processController;
        this.socket = socket;
    }

    /**
     *
     */
    public void run() {
        while(true) {
            try {
                Socket connectionSocket = socket.accept();
                connectionSocket.setSoTimeout(60 * 1000); //Close after 1min to avoid socket leakage
                new ManagerThread(connectionSocket, processController).start();
            } catch(Exception e) {
                log.error("Generic error in server with message: "+e.getMessage());
            }
        }
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
