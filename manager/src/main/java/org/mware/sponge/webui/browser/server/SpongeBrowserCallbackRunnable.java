package org.mware.sponge.webui.browser.server;

import org.mware.sponge.webui.WebUiCommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Dan on 10/3/2017.
 */
public class SpongeBrowserCallbackRunnable implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(SpongeBrowserCallbackRunnable.class);

    private WebUiCommandHandler webUiCommandHandler;
    private ServerSocket socket;

    public SpongeBrowserCallbackRunnable(WebUiCommandHandler webUiCommandHandler, ServerSocket socket) {
        super();
        this.webUiCommandHandler = webUiCommandHandler;
        this.socket = socket;
    }

    public void run() {
        while(true) {
            try {
                Socket connectionSocket = socket.accept();
                connectionSocket.setSoTimeout(60 * 1000); //Close after 1min to avoid socket leakage
                new CallbackThread(connectionSocket, this.webUiCommandHandler).start();
            } catch(Exception e) {
                log.error("Generic error in server with message: " + e.getMessage());
            }
        }
    }

    /**
     * @return
     */
    public WebUiCommandHandler getWebUiCommandHandler() {
        return webUiCommandHandler;
    }

    /**
     * @param webUiCommandHandler
     */
    public void setWebUiCommandHandler(WebUiCommandHandler webUiCommandHandler) {
        this.webUiCommandHandler = webUiCommandHandler;
    }

    /**
     * @return
     */
    public ServerSocket getSocket() {
        return socket;
    }

    /**
     * @param socket
     */
    public void setSocket(ServerSocket socket) {
        this.socket = socket;
    }
}