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

public class GetLoadedUrlAction implements Action {
    //Message format: LOADEDURL<\u001e><SESSIONID>
    private static final Logger log = LoggerFactory.getLogger(GetRenderedHtmlAction.class);

    private ProcessController processController;

    @Override
    public ServerResponse doExecute(ServerRequest request) {
        log.debug("Executing get loaded url action");
        final DriverStatus status = new DriverStatus();
        final SpongeBrowserDriver _driver = processController.getAssignedDriver(request.getSessionId(), status);

        String url = _driver.getLoadedUrl();

        return new ServerResponse(Protocol.SUCCESS_RESPONSE + Protocol.COMMAND_DELIMITER
                + url);
    }

    /**
     * @param processController
     */
    @Override
    public void setProcessController(ProcessController processController) {
        this.processController = processController;
    }
}
