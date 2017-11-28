package org.mware.sponge.browser.tcpip.client;

import org.mware.sponge.browser.tcpip.server.Protocol;
import org.mware.sponge.browser.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

/**
 * Created by Dan on 10/3/2017.
 */
public class CallbackClient {
    private static final Logger log = LoggerFactory.getLogger(CallbackClient.class);

    private static CallbackClient INSTANCE;
    private String parentHost;

    private CallbackClient(String parentHost) { this.parentHost = parentHost; }

    /**
     * @param parentHost
     * @return
     */
    public static synchronized CallbackClient getInstance(String parentHost) {
        if (INSTANCE == null) {
            INSTANCE = new CallbackClient(parentHost);
        }

        return INSTANCE;
    }

    /**
     * @param sessionId
     * @param message
     */
    public void sendWebsocketMessage(String sessionId, String message) {
        log.debug("Sending websocket message: "+message+" for sessionId: "+sessionId);

        String _command = Protocol.CALLBACK_COMMAND_PREFIX_WEBSOCKET + Protocol.COMMAND_DELIMITER
                                + sessionId + Protocol.PARAMETER_DELIMITER + message;
        try {
            sendCommand(_command);
        } catch (IOException e) {
            log.trace("Sending websocket message failed with message: "+e.getMessage());
        }
    }

    public void sendLoadFinished(String sessionId, String url, String loadId) {
        log.debug("Sending load finished message for url: "+url+" and sessionId: "+sessionId);

        String _command = Protocol.CALLBACK_COMMAND_PREFIX_LOADFINISHED + Protocol.COMMAND_DELIMITER
                + sessionId + Protocol.PARAMETER_DELIMITER + url + Protocol.PARAMETER_DELIMITER + loadId;
        try {
            sendCommand(_command);
        } catch (IOException e) {
            log.trace("Sending load finished message failed with message: "+e.getMessage());
        }
    }

    private void sendCommand(String command) throws IOException {
        log.debug("Sending callback command: "+command);

        Socket clientSocket = new Socket(this.parentHost, Constants.CALLBACK_SERVER_PORT);
        BufferedWriter outToServer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        outToServer.write(command + '\n');
        outToServer.write(Protocol.END_MESSAGE + '\n');
        outToServer.flush();

        String serverResponse = inFromServer.readLine();

        log.debug("Callback server response: "+serverResponse);
        outToServer.close();
        inFromServer.close();
        clientSocket.close();
    }
}
