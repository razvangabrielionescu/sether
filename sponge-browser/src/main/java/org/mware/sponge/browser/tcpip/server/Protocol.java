package org.mware.sponge.browser.tcpip.server;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dan on 10/2/2017.
 */
public class Protocol {
    //Request Message format: <COMMAND><\u001e><SESSIONID><\u001f><REST_OF_THE_PAYLOAD>
    //Response Message format: SUCCESS<\u001e><NUM_ACTIVE_PROCESSES>
    public static final char COMMAND_DELIMITER = '\u001e';
    public static final char PARAMETER_DELIMITER = '\u001f';
    public static final String END_MESSAGE = "PROTOCOL_END";
    public static final String SUCCESS_RESPONSE = "SUCCESS";

    //Receives
    public static Map<String, String> actionMap = new HashMap<String, String>(){{
        put("LOAD_PAGE", "org.mware.sponge.browser.tcpip.server.action.type.LoadPageAction");
        put("EXEC_JS", "org.mware.sponge.browser.tcpip.server.action.type.ExecuteJavaScriptAction");
        put("CLOSE", "org.mware.sponge.browser.tcpip.server.action.type.CloseSessionAction");
        put("ORIGINALHTML", "org.mware.sponge.browser.tcpip.server.action.type.GetOriginalHtmlAction");
        put("RENDEREDHTML", "org.mware.sponge.browser.tcpip.server.action.type.GetRenderedHtmlAction");
        put("LOADEDURL", "org.mware.sponge.browser.tcpip.server.action.type.GetLoadedUrlAction");
    }};

    //Sends
    public static final String CALLBACK_COMMAND_PREFIX_WEBSOCKET = "WEBSOCKET";
    public static final String CALLBACK_COMMAND_PREFIX_LOADFINISHED = "LOADFINISHED";
}
