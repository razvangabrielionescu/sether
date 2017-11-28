package org.mware.sponge.listener;

import java.io.File;

/**
 * Created by Dan on 7/11/2017.
 */
public interface FileListener {
    /**
     * @param file
     */
    void fileChanged (File file);
}
