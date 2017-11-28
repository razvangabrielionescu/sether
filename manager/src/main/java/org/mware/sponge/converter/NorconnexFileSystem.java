package org.mware.sponge.converter;

import com.norconex.collector.fs.FilesystemCollectorConfig;
import com.norconex.collector.fs.crawler.FilesystemCrawlerConfig;
import com.norconex.importer.ImporterConfig;
import org.mware.sponge.crawl.tagger.SimpleContentTagger;
import org.mware.sponge.service.dto.ProjectDTO;
import org.mware.sponge.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Dan on 8/28/2017.
 */
public class NorconnexFileSystem extends Norconnex {
    private static final Logger log = LoggerFactory.getLogger(NorconnexFileSystem.class);

    private static NorconnexFileSystem INSTANCE;

    private NorconnexFileSystem() {}

    /**
     * @return
     */
    public static synchronized NorconnexFileSystem getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NorconnexFileSystem();
        }

        return INSTANCE;
    }

    /**
     * @param project
     */
    public void createNorconnexFileSystemProjectFile(ProjectDTO project, String norconnexBase) {
        FilesystemCollectorConfig config = new FilesystemCollectorConfig();
        config.setId(project.getName());
        config.setLogsDir(norconnexBase + File.separator + Constants.NORCONNEX_LOGS_SUFFIX);
        config.setProgressDir(norconnexBase + File.separator + Constants.NORCONNEX_PROGRESS_SUFFIX);

        FilesystemCrawlerConfig crawlerConfig = new FilesystemCrawlerConfig();
        crawlerConfig.setId("["+project.getName()+"] "+Constants.NORCONNEX_DEFAULT_FILE_SYSTEM_CRAWLER_ID);
        crawlerConfig.setNumThreads(Constants.NORCONNEX_CRAWLER_DEFAULT_THREAD_NUM);
        crawlerConfig.setStartPaths(project.getStartUrl().split(","));
        String[] pathsFiles = new String[1];
        pathsFiles[0] = "";
        crawlerConfig.setPathsFiles(pathsFiles);

        //Crawler importer
        final ImporterConfig importerConfig = new ImporterConfig();
        final SimpleContentTagger cTagger = new SimpleContentTagger();
        importerConfig.setPreParseHandlers(cTagger);
        crawlerConfig.setImporterConfig(importerConfig);

        config.setCrawlerConfigs(crawlerConfig);

        this.ensureInputPath(norconnexBase);
        String projectFilePath = norconnexBase + File.separator + Constants.NORCONNEX_INPUT_SUFFIX +
                                            File.separator + project.getName() + ".xml";

        try {
            config.saveToXML(new FileWriter(projectFilePath));
        } catch (IOException e) {
            log.error("Norconnex project file (FileSystem) couldn't be created. WebUiProject ID: "+project.getName()
                +" | message: "+ e.getMessage());
        }
    }
}
