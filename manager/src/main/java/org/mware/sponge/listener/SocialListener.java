package org.mware.sponge.listener;

import org.mware.sponge.ProjectManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by Dan on 8/29/2017.
 */
public class SocialListener implements FileListener {
    private static final Logger log = LoggerFactory.getLogger(SocialListener.class);

    private ProjectManager projectManager;

    /**
     * @param projectManager
     */
    public SocialListener(ProjectManager projectManager) {
        this.projectManager = projectManager;
    }

    @Override
    public void fileChanged(File file) {
        log.info("Social projects directory changed.");
        try {
            projectManager.handleSocialProjectsUpdated();
        } catch(Throwable t) {
            log.warn("Something wrong happened in File Monitoring module [Social].");
            t.printStackTrace();
        }
    }

    /**
     * @return
     */
    public ProjectManager getProjectManager() {
        return projectManager;
    }

    /**
     * @param projectManager
     */
    public void setProjectManager(ProjectManager projectManager) {
        this.projectManager = projectManager;
    }
}
