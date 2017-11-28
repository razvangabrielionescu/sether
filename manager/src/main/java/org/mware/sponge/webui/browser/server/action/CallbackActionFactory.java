package org.mware.sponge.webui.browser.server.action;

import org.mware.sponge.exception.ProtocolCommandNotImplementedException;
import org.mware.sponge.webui.WebUiCommandHandler;
import org.mware.sponge.webui.browser.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Dan on 10/3/2017.
 */
public class CallbackActionFactory {
    private static final Logger log = LoggerFactory.getLogger(CallbackActionFactory.class);

    /**
     * @param command
     * @param webUiCommandHandler
     * @return
     * @throws ProtocolCommandNotImplementedException
     */
    public static synchronized CallbackAction buildAction(String command, WebUiCommandHandler webUiCommandHandler)
            throws ProtocolCommandNotImplementedException {
        if (!Protocol.actionMap.containsKey(command)) {
            throw new ProtocolCommandNotImplementedException("Callback Action for command: "+command+" is not implemented");
        }

        String actionClassName = Protocol.actionMap.get(command);
        CallbackAction action = null;
        try {
            Class actionClass = Class.forName(actionClassName);
            try {
                action = (CallbackAction)actionClass.newInstance();
                action.setWebUiCommandHandler(webUiCommandHandler);
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
