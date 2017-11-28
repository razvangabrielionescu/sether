package org.mware.sponge.agent;

import org.mware.sponge.agent.server.CommandServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class SpongeAgent {
    private static final Logger log = LoggerFactory.getLogger(SpongeAgent.class);

    private CollectorManager collectorManager;

    /**
     * @param args
     */
    public static void main(String[] args) {
	    if (!parseArgs(args)) {
            System.exit(0);
        }

        new SpongeAgent().initialize(Integer.parseInt(args[0]), args[1]);
    }

    private void initialize(int port, String projectDirectory) {
        this.collectorManager = new CollectorManager(projectDirectory);
        this.startServer(port);
    }

    private void startServer(int port) {
        CommandServer _server = new CommandServer();
        _server.setCollectorManager(this.collectorManager);
        _server.setPort(port);

        try {
            _server.startServer();
        } catch (Exception e) {
            log.error("Error starting sponge agent server with message: "+e.getMessage());
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

        if (!new File(args[1]).exists()){
            log.error("Project directory does not exist");
            return false;
        }

        if (!new File(args[1], Constants.NORCONNEX_INPUT_SUFFIX).exists()){
            log.error("Project directory is not valid");
            return false;
        }

        return true;
    }
}