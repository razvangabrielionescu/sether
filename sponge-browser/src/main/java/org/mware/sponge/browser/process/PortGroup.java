package org.mware.sponge.browser.process;

import java.io.Serializable;

public class PortGroup implements Serializable {
    private final long child;
    private final long parent;
    private final long parentAlt;

    /**
     * @param child
     * @param parent
     * @param parentAlt
     */
    public PortGroup(long child, long parent, long parentAlt) {
        this.child = child;
        this.parent = parent;
        this.parentAlt = parentAlt;
    }

    /**
     * @return
     */
    public long getChild() {
        return child;
    }

    /**
     * @return
     */
    public long getParent() {
        return parent;
    }

    /**
     * @return
     */
    public long getParentAlt() {
        return parentAlt;
    }
}

