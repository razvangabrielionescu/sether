package org.mware.sponge.webui.browser.server;

import org.mware.sponge.webui.WebUiCommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ServerSocket;

/**
 * Created by Dan on 10/3/2017.
 */
public class SpongeBrowserCallbackServer {
    private static final Logger log = LoggerFactory.getLogger(SpongeBrowserCallbackServer.class);

    private int port;
    private WebUiCommandHandler webUiCommandHandler;

    public SpongeBrowserCallbackServer() {}

    public void startServer() throws Exception {
        ServerSocket socket = new ServerSocket(this.port);
        new Thread(new SpongeBrowserCallbackRunnable(webUiCommandHandler, socket)).start();
        log.info("Sponge browser callback server started on port: " + this.port);
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
    public WebUiCommandHandler getWebUiCommandHandler() {
        return webUiCommandHandler;
    }

    /**
     * @param webUiCommandHandler
     */
    public void setWebUiCommandHandler(WebUiCommandHandler webUiCommandHandler) {
        this.webUiCommandHandler = webUiCommandHandler;
    }
}