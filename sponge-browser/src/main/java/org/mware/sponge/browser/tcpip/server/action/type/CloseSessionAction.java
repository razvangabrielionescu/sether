package org.mware.sponge.browser.tcpip.server.action.type;

import org.mware.sponge.browser.ProcessController;
import org.mware.sponge.browser.SpongeBrowserDriver;
import org.mware.sponge.browser.tcpip.server.Protocol;
import org.mware.sponge.browser.tcpip.server.action.Action;
import org.mware.sponge.browser.tcpip.server.request.ServerRequest;
import org.mware.sponge.browser.tcpip.server.response.ServerResponse;
import org.mware.sponge.browser.util.DriverStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Dan on 10/2/2017.
 */
public class CloseSessionAction implements Action {
    //Message format: CLOSE<\u001e><SESSIONID>
    private static final Logger log = LoggerFactory.getLogger(CloseSessionAction.class);

    private ProcessController processController;

    /**
     * @param request
     * @return
     */
    @Override
    public ServerResponse doExecute(ServerRequest request) {
        log.debug("Executing close session action for session: "+request.getSessionId());

        processController.killProcess(request.getSessionId());

        return new ServerResponse(Protocol.SUCCESS_RESPONSE);
    }

    /**
     * @param processController
     */
    public void setProcessController(ProcessController processController) {
        this.processController = processController;
    }
}
