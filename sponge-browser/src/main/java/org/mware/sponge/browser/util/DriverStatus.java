package org.mware.sponge.browser.util;

/**
 * Created by Dan on 10/5/2017.
 */
public class DriverStatus {
    private boolean created;

    /**
     *
     */
    public DriverStatus() {
    }

    /**
     * @param created
     */
    public DriverStatus(boolean created) {
        this.created = created;
    }

    /**
     * @return
     */
    public boolean isCreated() {
        return created;
    }

    /**
     * @param created
     */
    public void setCreated(boolean created) {
        this.created = created;
    }
}
