package org.mware.sponge.agent;

import com.norconex.collector.core.CollectorConfigLoader;
import com.norconex.collector.core.ICollector;
import com.norconex.collector.fs.FilesystemCollector;
import com.norconex.collector.fs.FilesystemCollectorConfig;
import com.norconex.collector.http.HttpCollector;
import com.norconex.collector.http.HttpCollectorConfig;
import com.norconex.commons.lang.config.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dan on 7/23/2017.
 */
public class CollectorManager {
    private static final Logger log = LoggerFactory.getLogger(CollectorManager.class);

    private String projectDirectory;
    private File sourceFolder;

    private Map<String, ICollector> collectorsMap;

    /**
     * @param projectDirectory
     */
    public CollectorManager(String projectDirectory) {
        this.projectDirectory = projectDirectory;
        this.prepare();
    }

    private void prepare() {
        this.sourceFolder = new File(this.projectDirectory, Constants.NORCONNEX_INPUT_SUFFIX);
        this.collectorsMap = new HashMap<String, ICollector>();
    }

    /**
     * @param projectName
     * @throws IOException
     */
    public void startProject(String projectName) throws IOException {
        if (this.collectorsMap.containsKey(projectName)) {
            log.warn("Project "+projectName+" is already running.");
            return;
        }

        final ICollector _collector = buildCollector(projectName);
        this.collectorsMap.put(projectName, _collector);
        log.info("Project "+projectName+" was started.");
        new Thread(new Runnable() {
            public void run() {
                _collector.start(true);
            }
        }).start();
    }

    private ICollector buildCollector(String projectName) throws IOException {
        ICollector collector = null;
        try {
            HttpCollectorConfig config = (HttpCollectorConfig) new CollectorConfigLoader(HttpCollectorConfig.class)
                    .loadCollectorConfig(new File(this.sourceFolder, projectName + ".xml"), null);
            collector = new HttpCollector(config);
        } catch(Exception e) {
            log.info("File System project");
            FilesystemCollectorConfig config = (FilesystemCollectorConfig) new CollectorConfigLoader(FilesystemCollectorConfig.class)
                    .loadCollectorConfig(new File(this.sourceFolder, projectName + ".xml"), null);
            collector = new FilesystemCollector(config);
        }

        return collector;
    }

    /**
     * @param projectName
     */
    public void stopProject(String projectName) {
        if (!this.collectorsMap.containsKey(projectName)) {
            log.trace("Project "+projectName+" is not running so it can not be stopped.");
            return;
        }

        try {
            this.collectorsMap.get(projectName).stop();
        } catch(Exception e) {
            log.trace("There was a problem stopping project "+projectName);
        } finally {
            log.info("Project "+projectName+" was removed.");
            this.collectorsMap.remove(projectName);
        }
    }

    /**
     * @return
     */
    public String getProjectDirectory() {
        return projectDirectory;
    }

    /**
     * @param projectDirectory
     */
    public void setProjectDirectory(String projectDirectory) {
        this.projectDirectory = projectDirectory;
    }
}