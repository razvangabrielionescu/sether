package org.mware.sponge.webui.browser;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dan on 10/3/2017.
 */
public class Protocol {
    //Message format: <COMMAND>|<SESSIONID>:::<REST_OF_THE_PAYLOAD>
    public static final char COMMAND_DELIMITER = '\u001e';
    public static final char PARAMETER_DELIMITER = '\u001f';
    public static final String END_MESSAGE = "PROTOCOL_END";
    public static final String SUCCESS_RESPONSE = "SUCCESS";

    //Receives
    public static Map<String, String> actionMap = new HashMap<String, String>(){{
        put("WEBSOCKET", "org.mware.sponge.webui.browser.server.action.type.WebsocketAction");
        put("LOADFINISHED", "org.mware.sponge.webui.browser.server.action.type.LoadFinishedAction");
    }};

    //Sends
    public static final String SERVER_COMMAND_PREFIX_LOAD_PAGE = "LOAD_PAGE";
    public static final String SERVER_COMMAND_PREFIX_EXEC_JS = "EXEC_JS";
    public static final String SERVER_COMMAND_PREFIX_CLOSE = "CLOSE";
    public static final String SERVER_COMMAND_PREFIX_GET_RENDERED_HTML = "RENDEREDHTML";
    public static final String SERVER_COMMAND_PREFIX_GET_ORIGINAL_HTML = "ORIGINALHTML";
    public static final String SERVER_COMMAND_PREFIX_GET_LOADED_URL = "LOADEDURL";
}
