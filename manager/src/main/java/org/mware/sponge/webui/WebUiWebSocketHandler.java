package org.mware.sponge.webui;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Service
public class WebUiWebSocketHandler extends TextWebSocketHandler {
    private static final Logger log = LoggerFactory.getLogger(WebUiWebSocketHandler.class);

    @Autowired
    private WebUiCommandHandler commandHandler;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JSONObject json = new JSONObject(message.getPayload());
        String command = json.getString("_command");

        if("load".equals(command)) {
            commandHandler.loadPage(session, json);
        } else if("interact".equals(command)) {
            commandHandler.interactPage(session, json);
        } else if("save_html".equals(command)) {
            commandHandler.saveHtml(session, json);
        } else if("extract_items".equals(command)) {
            commandHandler.extractItems(session, json);
        }
    }
}
