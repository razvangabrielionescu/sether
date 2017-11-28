package org.mware.sponge.converter;

import org.mware.sponge.util.Constants;

import java.io.File;

/**
 * Created by Dan on 8/30/2017.
 */
public class Norconnex {

    /**
     * @param norconnexBase
     */
    protected void ensureInputPath(String norconnexBase) {
        String inputPath = norconnexBase + File.separator + Constants.NORCONNEX_INPUT_SUFFIX;
        File inputDirectory = new File(inputPath);
        if (!inputDirectory.exists() || !inputDirectory.isDirectory()) {
            inputDirectory.mkdirs();
        }
    }
}
