package org.mware.sponge.webui.browser;

import liquibase.util.StringUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * Created by Dan on 10/3/2017.
 */
public class SpongeBrowserClient {
    private static final Logger log = LoggerFactory.getLogger(SpongeBrowserClient.class);

    private static SpongeBrowserClient INSTANCE;

    private SpongeBrowserClient() {}

    /**
     * @return
     */
    public static synchronized SpongeBrowserClient getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SpongeBrowserClient();
        }

        return INSTANCE;
    }

    /**
     * @param server
     * @param sessionId
     * @param viewPortWidth
     * @param viewPortHeight
     * @param userAgent
     * @param url
     * @param loadId
     * @param combinedJs
     * @return
     */
    public String sendLoadPageCommand(BrowserServer server, String sessionId, String viewPortWidth, String viewPortHeight,
                                    String userAgent, String url, String loadId, String combinedJs) {
        if (server == null) {
            log.error("[LoadPage] Sponge browser client misconfiguration. Nothing happened");
            return null;
        }
        log.debug("[LoadPage] Server: " + server);

        String _command = Protocol.SERVER_COMMAND_PREFIX_LOAD_PAGE + Protocol.COMMAND_DELIMITER +
                                    sessionId + Protocol.PARAMETER_DELIMITER +
                                    viewPortWidth + Protocol.PARAMETER_DELIMITER +
                                    viewPortHeight + Protocol.PARAMETER_DELIMITER +
                                    userAgent + Protocol.PARAMETER_DELIMITER +
                                    url + Protocol.PARAMETER_DELIMITER +
                                    loadId + Protocol.PARAMETER_DELIMITER +
                                    combinedJs;
        String _response = null;
        try {
            _response = sendCommand(server, _command);
        } catch (IOException e) {
            log.error("Sending LoadPage command to server failed with message: "+e.getMessage());
        }

        if (!StringUtils.isEmpty(_response)) {
            return _response.substring(Protocol.SUCCESS_RESPONSE.length() + 1);
        }

        return null;
    }

    /**
     * @param server
     * @param sessionId
     * @param script
     */
    public void sendExecuteJavaScriptCommand(BrowserServer server, String sessionId, String script) {
        if (server == null) {
            log.error("[ExecuteJavaScript] Sponge browser client misconfiguration. Nothing happened");
            return;
        }
        log.debug("[ExecuteJavaScript] Server: " + server);

        String _command = Protocol.SERVER_COMMAND_PREFIX_EXEC_JS + Protocol.COMMAND_DELIMITER +
                sessionId + Protocol.PARAMETER_DELIMITER + script;
        try {
            sendCommand(server, _command);
        } catch (IOException e) {
            log.error("Sending ExecuteJavaScript command to server failed with message: "+e.getMessage());
        }
    }

    /**
     * @param server
     * @param sessionId
     * @return
     */
    public String sendGetRenderedHtmlCommand(BrowserServer server, String sessionId) {
        if (server == null) {
            log.error("[GetHtml] Sponge browser client misconfiguration. Nothing happened");
            return null;
        }
        log.debug("[GetHtml] Server: " + server);

        String _command = Protocol.SERVER_COMMAND_PREFIX_GET_RENDERED_HTML + Protocol.COMMAND_DELIMITER
                + sessionId + Protocol.PARAMETER_DELIMITER;

        String _response = null;

        try {
            _response = sendCommand(server, _command);
        } catch (IOException e) {
            log.error("Sending GetRenderedHtml command to server failed with message: "+e.getMessage());
        }

        if (!StringUtils.isEmpty(_response)) {
            return _response.substring(Protocol.SUCCESS_RESPONSE.length() + 1);
        } else {
            return null;
        }
    }

    /**
     * @param server
     * @param sessionId
     * @return
     */
    public String sendGetOriginalHtmlCommand(BrowserServer server, String sessionId) {
        if (server == null) {
            log.error("[GetHtml] Sponge browser client misconfiguration. Nothing happened");
            return null;
        }
        log.debug("[GetHtml] Server: " + server);

        String _command = Protocol.SERVER_COMMAND_PREFIX_GET_ORIGINAL_HTML + Protocol.COMMAND_DELIMITER
                + sessionId + Protocol.PARAMETER_DELIMITER;

        String _response = null;

        try {
            _response = sendCommand(server, _command);
        } catch (IOException e) {
            log.error("Sending GetOriginalHtml command to server failed with message: "+e.getMessage());
        }

        if (!StringUtils.isEmpty(_response)) {
            return _response.substring(Protocol.SUCCESS_RESPONSE.length() + 1);
        } else {
            return null;
        }
    }

    /**
     * @param server
     * @param sessionId
     * @return
     */
    public String sendGetLoadedUrlCommand(BrowserServer server, String sessionId) {
        if (server == null) {
            log.error("[GetHtml] Sponge browser client misconfiguration. Nothing happened");
            return null;
        }
        log.debug("[GetHtml] Server: " + server);

        String _command = Protocol.SERVER_COMMAND_PREFIX_GET_LOADED_URL + Protocol.COMMAND_DELIMITER
                + sessionId + Protocol.PARAMETER_DELIMITER;

        String _response = null;

        try {
            _response = sendCommand(server, _command);
        } catch (IOException e) {
            log.error("Sending GetLoadedUrl command to server failed with message: "+e.getMessage());
        }

        if (!StringUtils.isEmpty(_response)) {
            return _response.substring(Protocol.SUCCESS_RESPONSE.length() + 1);
        } else {
            return null;
        }
    }

    /**
     * @param server
     * @param sessionId
     */
    public void sendCloseSessionCommand(BrowserServer server, String sessionId) {
        if (server == null) {
            log.error("[CloseSession] Sponge browser client misconfiguration. Nothing happened");
            return;
        }
        log.debug("[CloseSession] Server: " + server);

        String _command = Protocol.SERVER_COMMAND_PREFIX_CLOSE + Protocol.COMMAND_DELIMITER
                                                        + sessionId + Protocol.PARAMETER_DELIMITER;
        try {
            sendCommand(server, _command);
        } catch (IOException e) {
            log.error("Sending CloseSession command to server failed with message: "+e.getMessage());
        }
    }

    private String sendCommand(BrowserServer server, String command) throws IOException {
        log.debug("Sending command: " + command + " to browser server: " + server.toString());

        Socket clientSocket = new Socket(server.getHost(), server.getPort());
        BufferedWriter outToServer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));
        InputStream inFromServer = clientSocket.getInputStream();
        outToServer.write(command + '\n');
        outToServer.write(Protocol.END_MESSAGE + '\n');
        outToServer.flush();

        String serverResponse = IOUtils.toString(inFromServer, Charset.forName("UTF-8"));
        log.debug("Browser server response: "+serverResponse);

        outToServer.close();
        inFromServer.close();
        clientSocket.close();

        return serverResponse;
    }
}
