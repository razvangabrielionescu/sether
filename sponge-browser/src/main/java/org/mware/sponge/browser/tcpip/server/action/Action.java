package org.mware.sponge.browser.tcpip.server.action;

import org.mware.sponge.browser.ProcessController;
import org.mware.sponge.browser.SpongeBrowserDriver;
import org.mware.sponge.browser.tcpip.server.request.ServerRequest;
import org.mware.sponge.browser.tcpip.server.response.ServerResponse;

/**
 * Created by Dan on 10/2/2017.
 */
public interface Action {

    /**
     * @param request
     * @return
     */
    public ServerResponse doExecute(ServerRequest request);

    /**
     * @param processController
     */
    public void setProcessController(ProcessController processController);
}
