package org.mware.sponge.browser.util;

import java.io.File;

import org.apache.commons.io.FileUtils;

public class FileRemover extends Thread {
    private final File toDelete;

    /**
     * @param toDelete
     */
    public FileRemover(File toDelete) {
        this.toDelete = toDelete;
    }

    /**
     *
     */
    @Override
    public void run() {
        FileUtils.deleteQuietly(toDelete);
    }
}
