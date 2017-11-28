package org.mware.sponge.browser.process;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;

public class SocketLock implements Serializable {
    private final AtomicBoolean expired = new AtomicBoolean();

    /**
     * @return
     */
    public SocketLock validated() {
        if (expired.get()) {
            throw new IllegalStateException("Operation attempted, but browser already quit.");
        }

        return this;
    }

    /**
     * @return
     */
    public AtomicBoolean getExpired() {
        return expired;
    }
}
