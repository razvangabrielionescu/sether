package org.mware.sponge.agent;

import liquibase.util.StringUtils;
import org.mware.sponge.domain.Agent;
import org.mware.sponge.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by Dan on 7/23/2017.
 */
public class SpongeAgentClient {
    private static final Logger log = LoggerFactory.getLogger(SpongeAgentClient.class);

    private static SpongeAgentClient INSTANCE;

    private SpongeAgentClient() {};

    /**
     * @return
     */
    public static synchronized SpongeAgentClient getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SpongeAgentClient();
        }

        return INSTANCE;
    }

    /**
     * @param agent
     * @param projectName
     */
    public void sendStartCommand(Agent agent, String projectName) {
        if (agent == null || StringUtils.isEmpty(projectName)) {
            log.error("[START] Sponge agent client misconfiguration. Nothing happened");
            return;
        }
        log.debug("[START] Agent: "+agent);

        String _command = Constants.COMMAND_START_PREFIX + " " + projectName;
        try {
            sendCommand(agent, _command);
        } catch (IOException e) {
            log.error("Sending START command to agent failed with message: "+e.getMessage());
        }
    }

    /**
     * @param agent
     * @param projectName
     */
    public void sendStopCommand(Agent agent, String projectName) {
        if (agent == null || StringUtils.isEmpty(projectName)) {
            log.error("[STOP] Sponge agent client misconfiguration. Nothing happened");
            return;
        }
        log.debug("[STOP] Agent: "+agent);

        String _command = Constants.COMMAND_STOP_PREFIX + " " + projectName;
        try {
            sendCommand(agent, _command);
        } catch (IOException e) {
            log.trace("Sending STOP command to agent failed with message: "+e.getMessage());
        }
    }

    private void sendCommand(Agent agent, String command) throws IOException {
        log.debug("Sending command: "+command+" to agent: "+agent.getName());

        Socket clientSocket = new Socket(agent.getHost(), agent.getPort().intValue());
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        outToServer.writeBytes(command + '\n');
        String serverResponse = inFromServer.readLine();

        log.debug("Agent response: "+serverResponse);
        outToServer.flush();
        outToServer.close();
        inFromServer.close();
        clientSocket.close();
    }
}
