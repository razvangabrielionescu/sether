package org.mware.sponge.browser;

import org.apache.commons.lang.StringUtils;
import org.mware.sponge.browser.tcpip.server.ManagerServer;
import org.mware.sponge.browser.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Dan on 10/2/2017.
 */
public class SpongeBrowserManager {
    private static final Logger log = LoggerFactory.getLogger(SpongeBrowserManager.class);

    private ProcessController processController;

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (!parseArgs(args)) {
            System.exit(0);
        }

        new SpongeBrowserManager().initialize(Integer.parseInt(args[0]), args[1]);
    }

    private void initialize(int port, String parentHost) {
        this.processController = new ProcessController(parentHost);
        this.startServer(port);
    }

    private void startServer(int port) {
        ManagerServer _server = new ManagerServer();
        _server.setProcessController(this.processController);
        _server.setPort(port);

        try {
            _server.startServer();
        } catch (Exception e) {
            log.error("Error starting sponge browser manager server with message: "+e.getMessage());
        }
    }

    private static boolean parseArgs(String[] args) {
        if (args.length < 2) {
            log.error(Constants.USAGE);
            return false;
        }

        try {
            Integer.parseInt(args[0]);
        } catch (Exception e) {
            log.error("<listen_port> must be a number");
            return false;
        }

        if (StringUtils.isEmpty(args[1])) {
            log.error("<parent_host> must not be empty");
            return false;
        }

        return true;
    }
}
