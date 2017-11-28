package org.mware.sponge.webui.browser.server.action;

import org.mware.sponge.webui.WebUiCommandHandler;
import org.mware.sponge.webui.browser.server.request.CallbackRequest;
import org.mware.sponge.webui.browser.server.response.CallbackResponse;

/**
 * Created by Dan on 10/3/2017.
 */
public interface CallbackAction {

    /**
     * @param request
     * @return
     */
    public CallbackResponse doExecute(CallbackRequest request);

    /**
     * @param webUiCommandHandler
     */
    public void setWebUiCommandHandler(WebUiCommandHandler webUiCommandHandler);
}
