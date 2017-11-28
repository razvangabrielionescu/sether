package org.mware.sponge.monitor;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.filefilter.SuffixFileFilter;

import com.norconex.commons.lang.Sleeper;
import com.norconex.jef4.status.JobSuiteStatusSnapshot;
import org.mware.sponge.ProjectController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProjectMonitor implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(ProjectMonitor.class);

    public static final long DEFAULT_SCAN_INTERVAL = 5 * 1000;

    private final Monitor monitor;

    /**
     * @param targetFiles
     * @param _action
     */
    public ProjectMonitor(File[] targetFiles, ProjectController.IMonitorAction _action) {
        this(targetFiles);
        if (this.monitor != null) {
            this.monitor.setAction(_action);
        }
    }

    /**
     * @param targetFiles
     */
    public ProjectMonitor(File[] targetFiles) {
        this(targetFiles, DEFAULT_SCAN_INTERVAL);
    }

    /**
     * @param targetFiles
     * @param scanInterval
     */
    public ProjectMonitor(File[] targetFiles, long scanInterval) {
        super();
        this.monitor = new Monitor(targetFiles, scanInterval);
    }

    public Collection<JobSuiteStatusSnapshot> getJobSuitesStatuses() {
        return monitor.getStatuses().values();
    }

    public JobSuiteStatusSnapshot getJobSuiteStatuses(String suiteId) {
        for (JobSuiteStatusSnapshot snapshot : getJobSuitesStatuses()) {
            if (snapshot.getRoot().getJobId().equals(suiteId)) {
                return snapshot;
            }
        }
        return null;
    }

    public void startMonitoring() {
        new Thread(monitor).start();
    }

    public void stopMonitoring() {
        monitor.stopMe();
    }

    private static class Monitor implements Runnable, Serializable {
        private static final long serialVersionUID = 1L;

        private static final Map<File, JobSuiteStatusSnapshot> STATUSES =
            Collections.synchronizedMap(new HashMap<File, JobSuiteStatusSnapshot>());

        private File[] targetFiles;
        private boolean running;
        private boolean stopme;
        private final long interval;
        private ProjectController.IMonitorAction action;

        public Monitor(File[] targetFiles, long interval) {
            super();
            this.targetFiles = targetFiles;
            this.interval = interval;
        }

        private Map<File, JobSuiteStatusSnapshot> getStatuses() {
            return STATUSES;
        }

        public void run() {
            if (running) {
                throw new IllegalStateException(
                    "ProjectMonitor already running.");
            }
            running = true;
            stopme = false;
            while (!stopme) {
                if (targetFiles != null) {
                    syncIndexFiles();
                    if (this.action != null) {
                        action.doAction();
                    }
                }
                Sleeper.sleepMillis(interval);
            }
            running = false;
        }

        public void stopMe() {
            stopme = true;
        }

        private void syncIndexFiles() {
            Set<File> files = new HashSet<File>();
            for (File file : targetFiles) {
                if (!file.exists()) {
                    continue;
                }

                if (file.isFile()) {
                    files.add(file);
                } else if (file.isDirectory()) {
                    files.addAll(Arrays.asList(file.listFiles(
                        (FilenameFilter) new SuffixFileFilter(".index"))));
                }
            }
            // remove suite statuses those that no longer have an index file.
            STATUSES.keySet().retainAll(files);

            // add/replace suite statuses
            for (File file : files) {
                try {
                    STATUSES.put(file,
                        JobSuiteStatusSnapshot.newSnapshot(file));
                } catch (IOException e) {
                    log.error("Cannot sync suite statuses for index file: "
                        + file, e);
                }
            }
        }

        /**
         * @return
         */
        public ProjectController.IMonitorAction getAction() {
            return action;
        }

        /**
         * @param action
         */
        public void setAction(ProjectController.IMonitorAction action) {
            this.action = action;
        }
    }
}
