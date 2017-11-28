package org.mware.sponge.webui.browser.server;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.mware.sponge.exception.BadCallbackRequest;
import org.mware.sponge.exception.ProtocolCommandNotImplementedException;
import org.mware.sponge.webui.WebUiCommandHandler;
import org.mware.sponge.webui.browser.Protocol;
import org.mware.sponge.webui.browser.server.action.CallbackAction;
import org.mware.sponge.webui.browser.server.action.CallbackActionFactory;
import org.mware.sponge.webui.browser.server.request.CallbackRequest;
import org.mware.sponge.webui.browser.server.response.CallbackResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by Dan on 10/20/2017.
 */
public class CallbackThread extends Thread {
    private static final Logger log = LoggerFactory.getLogger(CallbackThread.class);

    private Socket connectionSocket;
    private WebUiCommandHandler webUiCommandHandler;

    /**
     * @param connectionSocket
     * @param webUiCommandHandler
     */
    public CallbackThread(Socket connectionSocket, WebUiCommandHandler webUiCommandHandler) {
        this.connectionSocket = connectionSocket;
        this.webUiCommandHandler = webUiCommandHandler;
    }

    /**
     *
     */
    @Override
    public void run() {
        String clientResponse = "";
        BufferedReader inFromClient = null;
        DataOutputStream outToClient = null;

        try {
            inFromClient = new BufferedReader(
                    new InputStreamReader(connectionSocket.getInputStream(), "UTF-8"));
            outToClient = new DataOutputStream(connectionSocket.getOutputStream());

            StringBuffer clientCommandBuffer = new StringBuffer();
            String line = inFromClient.readLine();
            while (!line.trim().equals(Protocol.END_MESSAGE)) {
                clientCommandBuffer.append(line);
                clientCommandBuffer.append('\n');
                line = inFromClient.readLine();
            }
            final String clientCommand = clientCommandBuffer.toString();
            log.trace("[SpongeBrowserCallbackServer] Received command: " + clientCommand);

            try {
                CallbackRequest callbackRequest = buildCallbackRequest(clientCommand);
                CallbackAction action =
                        CallbackActionFactory.buildAction(callbackRequest.getCommand(), webUiCommandHandler);
                CallbackResponse callbackResponse = action.doExecute(callbackRequest);
                clientResponse = callbackResponse.getMessage();
            } catch (BadCallbackRequest bcr) {
                log.error("Error building callback request with message: " + bcr.getMessage());
                clientResponse = bcr.getMessage();
            } catch (ProtocolCommandNotImplementedException e) {
                log.error("Error building callback action with message: " + e.getMessage());
                clientResponse = e.getMessage();
            }
        } catch(IOException e) {
            log.error("I/O error in callback thread with message: " + e.getMessage());
        } finally {
            try {
                if (outToClient != null) {
                    outToClient.writeBytes(clientResponse);
                    outToClient.flush();
                    outToClient.close();
                }
                if (inFromClient != null) {
                    inFromClient.close();
                }
                if (this.connectionSocket != null) {
                    IOUtils.closeQuietly(connectionSocket);
                }
            } catch(IOException e) {
                log.trace("Error closing streams " + e.getMessage());
            }
        }
    }

    private CallbackRequest buildCallbackRequest(String request)
            throws BadCallbackRequest {
        if (StringUtils.isEmpty(request)) {
            throw new BadCallbackRequest(
                    "Callback command is empty");
        }
        if (request.indexOf(Protocol.COMMAND_DELIMITER) < 0) {
            throw new BadCallbackRequest(
                    "Callback command does not contain the protocol command delimiter: "+Protocol.COMMAND_DELIMITER);
        }

        int _idx = request.indexOf(Protocol.COMMAND_DELIMITER);
        final String command = request.substring(0, _idx);
        if (StringUtils.isEmpty(command)) {
            throw new BadCallbackRequest(
                    "Callback command contains empty action directive");
        }

        final String payload = request.substring(_idx + 1);
        if (StringUtils.isEmpty(payload)) {
            throw new BadCallbackRequest(
                    "Callback command contains empty payload");
        }

        return new CallbackRequest(command, payload);
    }
}
