package org.mware.sponge.browser.tcpip.server.action.type;

import org.apache.commons.lang.StringUtils;
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
public class LoadPageAction implements Action {
    //Message format: LOAD_PAGE<\u001e><SESSIONID><\u001f><VIEWPORTWIDTH><\u001f><VIEWPORTHEIGHT><\u001f><USERAGENT><\u001f><URL><\u001f><LOADID><\u001f><COMBINEDJS>
    private static final Logger log = LoggerFactory.getLogger(LoadPageAction.class);

    private ProcessController processController;

    /**
     * @param request
     * @return
     */
    public ServerResponse doExecute(ServerRequest request) {
        log.debug("Executing load page action");
        final DriverStatus status = new DriverStatus();
        final SpongeBrowserDriver _driver = processController.getAssignedDriver(request.getSessionId(), status);

        //Extract payload data
        final String[] payloadSplit = StringUtils.split(request.getPayload(), Protocol.PARAMETER_DELIMITER);
        if(payloadSplit.length != 6)
            throw new IllegalArgumentException("Received wrong number of arguments for LoadPageAction");

        final Integer viewPortWidth = Integer.parseInt(payloadSplit[0]);
        final Integer viewPortHeight = Integer.parseInt(payloadSplit[1]);
        final String userAgent = payloadSplit[2];
        final String url = payloadSplit[3];
        final String loadId = payloadSplit[4];
        final String combinedJs = payloadSplit[5];

        new Thread(new Runnable() {
            @Override
            public void run() {
                _driver.loadPage(request.getSessionId(), viewPortWidth, viewPortHeight, userAgent, url, loadId, combinedJs);
            }
        }).start();


        return new ServerResponse(Protocol.SUCCESS_RESPONSE + Protocol.COMMAND_DELIMITER +
                (status.isCreated() ? _driver.incrementAndGetNumberOfActiveJobs() : _driver.getNumberOfActiveJobs()));
    }

    /**
     * @param processController
     */
    public void setProcessController(ProcessController processController) {
        this.processController = processController;
    }
}
