package org.mware.sponge.agent.server;

import org.apache.commons.lang.StringUtils;
import org.mware.sponge.agent.CollectorManager;
import org.mware.sponge.agent.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Dan on 7/23/2017.
 */
public class CommandRunnable implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(CommandRunnable.class);

    private ServerSocket socket;
    private CollectorManager collectorManager;

    /**
     * @param collectorManager
     * @param socket
     */
    public CommandRunnable(CollectorManager collectorManager, ServerSocket socket) {
        super();
        this.socket = socket;
        this.collectorManager = collectorManager;
    }

    /**
     * @param socket
     */
    public CommandRunnable(ServerSocket socket) {
        super();
        this.socket = socket;
    }

    public void run() {
        String clientResponse;
        while(true) {
            try {
                Socket connectionSocket = socket.accept();
                BufferedReader inFromClient =
                        new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                final String clientCommand = inFromClient.readLine();
                log.trace("[SpongeAgentServer] Received command: " + clientCommand);

                if (!StringUtils.isEmpty(clientCommand)) {
                    if (clientCommand.startsWith(Constants.COMMAND_START_PREFIX)) {
                        String projectName = clientCommand.substring(Constants.COMMAND_START_PREFIX.length() + 1).trim();
                        if (StringUtils.isEmpty(projectName)) {
                            log.warn("Invalid START command. Project name is missing");
                        } else {
                            collectorManager.startProject(projectName);
                        }
                    } else if (clientCommand.startsWith(Constants.COMMAND_STOP_PREFIX)) {
                        String projectName = clientCommand.substring(Constants.COMMAND_STOP_PREFIX.length() + 1).trim();
                        if (StringUtils.isEmpty(projectName)) {
                            log.warn("Invalid STOP command. Project name is missing");
                        } else {
                            try {
                                collectorManager.stopProject(projectName);
                            } catch (com.norconex.collector.core.CollectorException ex) {
                                // We are not interested in this
                                log.trace("Common stop exception");
                            }
                        }
                    } else {
                        log.warn("Unrecognized command from Sponge");
                    }
                }

                clientResponse = Constants.DEFAULT_CLIENT_RESPONSE;
                outToClient.writeBytes(clientResponse);
                outToClient.flush();
                outToClient.close();
                inFromClient.close();
            } catch(Exception e) {
                log.error("Generic error in server with message: "+e.getMessage());
                e.printStackTrace();
            }
        }
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