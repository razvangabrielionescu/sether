package org.mware.sponge.browser.tcpip.server;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.mware.sponge.browser.ProcessController;
import org.mware.sponge.browser.exception.BadServerRequest;
import org.mware.sponge.browser.exception.ProtocolCommandNotImplementedException;
import org.mware.sponge.browser.tcpip.server.action.Action;
import org.mware.sponge.browser.tcpip.server.action.ActionFactory;
import org.mware.sponge.browser.tcpip.server.request.ServerRequest;
import org.mware.sponge.browser.tcpip.server.response.ServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Created by Dan on 10/20/2017.
 */
public class ManagerThread extends Thread {
    private static final Logger log = LoggerFactory.getLogger(ManagerThread.class);

    private Socket connectionSocket;
    private ProcessController processController;

    /**
     * @param connectionSocket
     * @param processController
     */
    public ManagerThread(Socket connectionSocket, ProcessController processController) {
        this.connectionSocket = connectionSocket;
        this.processController = processController;
    }

    /**
     *
     */
    @Override
    public void run() {
        String clientResponse = "";
        BufferedReader inFromClient = null;
        InputStream clientResponseStream = null;
        OutputStream outToClient = null;
        try {
            inFromClient = new BufferedReader(
                    new InputStreamReader(connectionSocket.getInputStream(), "UTF-8"));
            outToClient = connectionSocket.getOutputStream();

            StringBuffer clientCommandBuffer = new StringBuffer();
            String line = inFromClient.readLine();
            while (!line.trim().equals(Protocol.END_MESSAGE)) {
                clientCommandBuffer.append(line);
                clientCommandBuffer.append('\n');
                line = inFromClient.readLine();
            }
            final String clientCommand = clientCommandBuffer.toString();
            log.trace("[SpongeBrowserManagerServer] Received command: " + clientCommand);
            try {
                ServerRequest serverRequest = buildServerRequest(clientCommand);
                Action action = ActionFactory.buildAction(serverRequest.getCommand(), processController);
                ServerResponse serverResponse = action.doExecute(serverRequest);
                clientResponse = serverResponse.getMessage();
            } catch (BadServerRequest bsr) {
                log.error("Error building server request with message: " + bsr.getMessage());
                clientResponse = bsr.getMessage();
            } catch (ProtocolCommandNotImplementedException e) {
                log.error("Error building action with message: " + e.getMessage());
                clientResponse = e.getMessage();
            }
        } catch (IOException e) {
            log.error("I/O error in manager thread with message: " + e.getMessage());
        } finally {
            try {
                if (outToClient != null) {
                    clientResponseStream =
                            new ByteArrayInputStream(clientResponse.getBytes(StandardCharsets.UTF_8.name()));
                    IOUtils.copyLarge(clientResponseStream, outToClient);
                    clientResponseStream.close();
                    outToClient.flush();
                    outToClient.close();
                }
                if (inFromClient != null) {
                    inFromClient.close();
                }
                if (this.connectionSocket != null) {
                    IOUtils.closeQuietly(connectionSocket);
                }
            } catch (IOException e) {
                log.trace("Error closing streams: " + e.getMessage());
            }
        }
    }

    private ServerRequest buildServerRequest(String request)
            throws BadServerRequest {
        if (StringUtils.isEmpty(request)) {
            throw new BadServerRequest(
                    "Command is empty");
        }
        if (request.indexOf(Protocol.COMMAND_DELIMITER) < 0) {
            throw new BadServerRequest(
                    "Command does not contain the protocol command delimiter: "+Protocol.COMMAND_DELIMITER);
        }

        int _idx = request.indexOf(Protocol.COMMAND_DELIMITER);
        final String command = request.substring(0, _idx);
        if (StringUtils.isEmpty(command)) {
            throw new BadServerRequest(
                    "Command contains empty action directive");
        }
        final String payload = request.substring(_idx + 1);
        if (StringUtils.isEmpty(payload)) {
            throw new BadServerRequest(
                    "Command contains empty payload");
        }

        return new ServerRequest(command, payload);
    }
}