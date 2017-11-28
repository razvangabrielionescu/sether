package org.mware.sponge.browser;

import org.mware.sponge.browser.util.DriverStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dan on 10/2/2017.
 */
public class ProcessController {
    private static final Logger log = LoggerFactory.getLogger(ProcessController.class);

    private final Map<String, SpongeBrowserDriver> drivers =
            Collections.synchronizedMap(new HashMap<String, SpongeBrowserDriver>());

    private String parentHost;

    /**
     * @param parentHost
     */
    public ProcessController(String parentHost) {
        this.parentHost = parentHost;
    }

    /**
     * @param sessionId
     * @param status
     * @return
     */
    public SpongeBrowserDriver getAssignedDriver(String sessionId, DriverStatus status) {
        if (!drivers.containsKey(sessionId)) {
            log.debug("Creating a new process driver");

            SpongeBrowserDriver _driver = new SpongeBrowserDriver(this.parentHost);
            drivers.put(sessionId, _driver);
            status.setCreated(true);

            return _driver;
        }
        status.setCreated(false);

        return drivers.get(sessionId);
    }

    /**
     * @param sessionId
     */
    public void killProcess(String sessionId) {
        if (!drivers.containsKey(sessionId)) {
            log.debug("Trying to kill an non-existent process. Nothing happened");
            return;
        }

        drivers.get(sessionId).kill();
        drivers.remove(sessionId);
    }

    /**
     * @return
     */
    public Map<String, SpongeBrowserDriver> getDrivers() {
        return drivers;
    }

    /**
     * @return
     */
    public String getParentHost() {
        return parentHost;
    }

    /**
     * @param parentHost
     */
    public void setParentHost(String parentHost) {
        this.parentHost = parentHost;
    }
}
