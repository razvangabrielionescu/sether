package org.mware.sponge.converter;

import com.norconex.collector.core.CollectorConfigLoader;
import com.norconex.collector.core.crawler.ICrawlerConfig;
import com.norconex.collector.http.HttpCollectorConfig;
import com.norconex.collector.http.crawler.HttpCrawlerConfig;
import com.norconex.collector.http.fetch.IHttpDocumentFetcher;
import org.mware.sponge.crawl.fetcher.FacebookDocumentFetcher;
import org.mware.sponge.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by Dan on 8/29/2017.
 */
public class NorconnexSocial extends Norconnex {
    private static final Logger log = LoggerFactory.getLogger(NorconnexSocial.class);

    private static NorconnexSocial INSTANCE;

    private NorconnexSocial() {}

    /**
     * @return
     */
    public static synchronized NorconnexSocial getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NorconnexSocial();
        }

        return INSTANCE;
    }

    /**
     * @param inputFile
     * @param projectName
     * @param norconnexBase
     * @param proxySettings
     * @param genericSettings
     * @param crawlers
     * @param socialCredentials
     * @return
     */
    public String createNorconnexSocialProjectFile(File inputFile, String projectName,
                                                   String norconnexBase, ProxySettings proxySettings, GenericSettings genericSettings,
                                                   List<String> crawlers, SocialUtil.SocialCredentials socialCredentials) {
        HttpCollectorConfig config = null;
        try {
            config = (HttpCollectorConfig) new CollectorConfigLoader(HttpCollectorConfig.class)
                                                                        .loadCollectorConfig(inputFile, null);

            config.setId(projectName);
            config.setLogsDir(norconnexBase + File.separator + Constants.NORCONNEX_LOGS_SUFFIX);
            config.setProgressDir(norconnexBase + File.separator + Constants.NORCONNEX_PROGRESS_SUFFIX);
            HttpCrawlerConfig crawlerConfig = null;
            for (ICrawlerConfig icrawlerConfig : config.getCrawlerConfigs()) {
                crawlerConfig = (HttpCrawlerConfig)icrawlerConfig;
                if (!crawlerConfig.getId().startsWith("[" + projectName + "] ")) {
                    crawlerConfig.setId("[" + projectName + "] " + crawlerConfig.getId());
                }
                //Add generic settings
                SettingsUtil.getInstance().addGenericSettingsToProject(genericSettings, crawlerConfig);
                //Add proxy settings
                ProxyUtil.getInstance().addProxySettingsToProject(proxySettings, crawlerConfig);
                //Add social credentials
                IHttpDocumentFetcher documentFetcher = crawlerConfig.getDocumentFetcher();
                if (documentFetcher instanceof FacebookDocumentFetcher) {
                    //TBD for other social fetchers
                    FacebookDocumentFetcher fbDocumentFetcher = (FacebookDocumentFetcher)documentFetcher;
                    fbDocumentFetcher.setAppId(socialCredentials.getAppId());
                    fbDocumentFetcher.setAppSecret(socialCredentials.getAppSecret());
                    crawlerConfig.setDocumentFetcher(fbDocumentFetcher);
                }

                crawlers.add(crawlerConfig.getId());
            }
        } catch(IOException e) {
            log.error("There was an error reading Norconex (Social) file with message: "+e.getMessage());
        }

        this.ensureInputPath(norconnexBase);
        String projectFilePath = norconnexBase + File.separator + Constants.NORCONNEX_INPUT_SUFFIX +
            File.separator + projectName + ".xml";
        try {
            config.saveToXML(new FileWriter(projectFilePath));
        } catch (IOException e) {
            log.error("Norconnex (Social) project file couldn't be created. WebUiProject ID: " + projectName
                +" | message: "+ e.getMessage());
        }

        return projectFilePath;
    }
}
