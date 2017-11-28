package org.mware.sponge.browser.tcpip.server.action;

import org.mware.sponge.browser.ProcessController;
import org.mware.sponge.browser.SpongeBrowserDriver;
import org.mware.sponge.browser.exception.ProtocolCommandNotImplementedException;
import org.mware.sponge.browser.tcpip.server.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Dan on 10/2/2017.
 */
public class ActionFactory {
    private static final Logger log = LoggerFactory.getLogger(ActionFactory.class);

    /**
     * @param command
     * @param processController
     * @return
     * @throws ProtocolCommandNotImplementedException
     */
    public static Action buildAction(String command, ProcessController processController)
                throws ProtocolCommandNotImplementedException {
        if (!Protocol.actionMap.containsKey(command)) {
            throw new ProtocolCommandNotImplementedException("Action for command: "+command+" is not implemented");
        }

        String actionClassName = Protocol.actionMap.get(command);
        Action action = null;
        try {
            Class actionClass = Class.forName(actionClassName);
            try {
                action = (Action)actionClass.newInstance();
                action.setProcessController(processController);
            } catch (InstantiationException e) {
                log.error("Class could not be instantiated: "+actionClassName+" with message: "+e.getMessage());
            } catch (IllegalAccessException e) {
                log.error("Class illegal access: "+actionClassName+" with message: "+e.getMessage());
            }
        } catch (ClassNotFoundException e) {
            log.error("Class not found: "+actionClassName);
        }

        return action;
    }
}
