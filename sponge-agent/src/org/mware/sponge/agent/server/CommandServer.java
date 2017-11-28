package org.mware.sponge.agent.server;

import org.mware.sponge.agent.CollectorManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ServerSocket;

/**
 * Created by Dan on 7/23/2017.
 */
public class CommandServer {
    private static final Logger log = LoggerFactory.getLogger(CommandServer.class);

    private int port;
    private CollectorManager collectorManager;

    public void startServer() throws Exception {
        ServerSocket socket = new ServerSocket(this.port);
        new Thread(new CommandRunnable(this.collectorManager, socket)).start();
        log.info("Sponge agent server started on port: " + this.port);
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
    public CollectorManager getCollectorManager() {
        return collectorManager;
    }

    /**
     * @param collectorManager
     */
    public void setCollectorManager(CollectorManager collectorManager) {
        this.collectorManager = collectorManager;
    }
}