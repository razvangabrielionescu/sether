package org.mware.sponge.webui.browser.server.action.type;

import org.apache.commons.lang.StringUtils;
import org.mware.sponge.webui.WebUiCommandHandler;
import org.mware.sponge.webui.browser.Protocol;
import org.mware.sponge.webui.browser.server.action.CallbackAction;
import org.mware.sponge.webui.browser.server.request.CallbackRequest;
import org.mware.sponge.webui.browser.server.response.CallbackResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class LoadFinishedAction implements CallbackAction {
    //Message format: LOADFINISHED<\u001e><SESSIONID><\u001f><URL><\u001f><LOAD_ID>
    private static final Logger log = LoggerFactory.getLogger(LoadFinishedAction.class);

    private WebUiCommandHandler webUiCommandHandler;

    /**
     * @param request
     * @return
     */
    @Override
    public synchronized CallbackResponse doExecute(CallbackRequest request) {
        log.trace("Executing LoadFinished action");

        final String[] payloadSplit = StringUtils.split(request.getPayload(), Protocol.PARAMETER_DELIMITER);
        if (payloadSplit.length != 2) {
            throw new IllegalArgumentException("Received wrong number of arguments for LoadPageAction");
        }

        final String url = payloadSplit[0];
        final String loadId = payloadSplit[1];

        try {
            webUiCommandHandler.sendLoadFinished(request.getSessionId(), url, loadId);
        } catch (IOException e) {
            log.debug("Sending message to socket failed with message: " + e.getMessage());
        } catch (NullPointerException npe) {
            log.warn("Sending message to socket failed with message: " + npe.getMessage());
        }

        return new CallbackResponse(Protocol.SUCCESS_RESPONSE);
    }

    @Override
    public void setWebUiCommandHandler(WebUiCommandHandler webUiCommandHandler) {
        this.webUiCommandHandler = webUiCommandHandler;
    }
}
