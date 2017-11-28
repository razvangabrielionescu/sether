package org.mware.sponge.webui.browser.server.action.type;

import org.mware.sponge.webui.WebUiCommandHandler;
import org.mware.sponge.webui.browser.Protocol;
import org.mware.sponge.webui.browser.server.action.CallbackAction;
import org.mware.sponge.webui.browser.server.request.CallbackRequest;
import org.mware.sponge.webui.browser.server.response.CallbackResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by Dan on 10/4/2017.
 */
public class WebsocketAction implements CallbackAction {
    //Message format: WEBSOCKET<\u001e><SESSIONID><\u001f><MESSAGE>
    private static final Logger log = LoggerFactory.getLogger(WebsocketAction.class);

    private WebUiCommandHandler webUiCommandHandler;

    /**
     * @param request
     * @return
     */
    @Override
    public synchronized CallbackResponse doExecute(CallbackRequest request) {
        log.trace("Executing websocket action");

        try {
            webUiCommandHandler.sendWebsocketMessage(request.getSessionId(), request.getPayload());
        } catch (IOException e) {
            log.debug("Sending message to socket failed with message: " + e.getMessage());
        } catch (NullPointerException npe) {
            log.warn("Sending message to socket failed with message: " + npe.getMessage());
        }

        return new CallbackResponse(Protocol.SUCCESS_RESPONSE);
    }

    /**
     * @param webUiCommandHandler
     */
    @Override
    public void setWebUiCommandHandler(WebUiCommandHandler webUiCommandHandler) {
        this.webUiCommandHandler = webUiCommandHandler;
    }
}
