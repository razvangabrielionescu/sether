package org.mware.sponge.converter;

import com.norconex.collector.core.filter.IReferenceFilter;
import com.norconex.collector.core.filter.impl.ExtensionReferenceFilter;
import com.norconex.collector.core.filter.impl.RegexReferenceFilter;
import com.norconex.collector.http.HttpCollectorConfig;
import com.norconex.collector.http.client.impl.GenericHttpClientFactory;
import com.norconex.collector.http.crawler.HttpCrawlerConfig;
import com.norconex.collector.http.url.impl.GenericCanonicalLinkDetector;
import com.norconex.commons.lang.file.ContentType;
import com.norconex.importer.ImporterConfig;
import com.norconex.importer.handler.filter.OnMatch;
import org.mware.sponge.crawl.tagger.BigConnectDOMTagger;
import org.mware.sponge.util.*;
import org.mware.sponge.webui.domain.WebUiAnnotation;
import org.mware.sponge.webui.domain.WebUiProject;
import org.mware.sponge.webui.domain.WebUiSpider;
import org.mware.sponge.webui.domain.WebUiStartUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Dan on 7/12/2017.
 */
@Service
@Transactional
public class NorconnexConverter extends Norconnex {
    private static final Logger log = LoggerFactory.getLogger(NorconnexConverter.class);

    /**
     * @param webUiProject
     * @param norconnexBase
     * @param proxySettings
     * @param genericSettings
     * @return
     */
    public String createNorconnexProjectFile(
            WebUiProject webUiProject,
            String norconnexBase,
            ProxySettings proxySettings,
            GenericSettings genericSettings) {
        final HttpCollectorConfig config = new HttpCollectorConfig();
        config.setId(webUiProject.name);
        config.setLogsDir(norconnexBase + File.separator + Constants.NORCONNEX_LOGS_SUFFIX);
        config.setProgressDir(norconnexBase + File.separator + Constants.NORCONNEX_PROGRESS_SUFFIX);

        //Crawler configuration
        List<HttpCrawlerConfig> crawlerConfigs = new ArrayList<HttpCrawlerConfig>();
        webUiProject.spiders.forEach(spider -> {
            final HttpCrawlerConfig crawlerConfig = new HttpCrawlerConfig();

            crawlerConfig.setId("["+webUiProject.name+"] "+spider.name);
            crawlerConfig.setMaxDepth(Constants.NORCONNEX_CRAWLER_DEFAULT_MAX_DEPTH);
            crawlerConfig.setNumThreads(Constants.NORCONNEX_CRAWLER_DEFAULT_THREAD_NUM);
            crawlerConfig.setIgnoreSitemap(true);

            //Reference filters
            List<IReferenceFilter> filters = new ArrayList<>();

            // Excluding by extension
            ExtensionReferenceFilter extensionFilter = new ExtensionReferenceFilter();
            extensionFilter.setOnMatch(OnMatch.EXCLUDE);
            extensionFilter.setExtensions(Constants.NORCONNEX_CRAWLER_DEFAULT_EXTENSIONS_REJECT);
            filters.add(extensionFilter);

            // Adding Portia include patterns
            filters.addAll(spider.followPatterns.stream()
                .map(followPattern -> {
                   return new RegexReferenceFilter(followPattern, OnMatch.INCLUDE);
                }).collect(Collectors.toList()));

            //Adding Portia exclude patterns
            filters.addAll(spider.excludePatterns.stream()
                .map(followPattern -> {
                    return new RegexReferenceFilter(followPattern, OnMatch.EXCLUDE);
                }).collect(Collectors.toList()));

            crawlerConfig.setReferenceFilters(filters.toArray(new IReferenceFilter[]{}));

            //Crawler start URLs
            String[] startURLs = new String[spider.startUrls.size()];
            int _idx = 0;
            for (WebUiStartUrl _url : spider.startUrls) {
                startURLs[_idx++] = _url.url;
            }
            crawlerConfig.setStartURLs(startURLs);
            crawlerConfig.getURLCrawlScopeStrategy().setStayOnDomain(Constants.NORCONNEX_CRAWL_SCOPE_STRATEGY_STAY_ON_DOMAIN);

            //Authentication
            if (spider.performLogin) {
                GenericHttpClientFactory clientFactory =
                        (GenericHttpClientFactory)crawlerConfig.getHttpClientFactory();
                clientFactory.setAuthURL(spider.loginUrl);
                clientFactory.setAuthUsername(spider.loginUser);
                clientFactory.setAuthPassword(spider.loginPassword);
                clientFactory.setAuthMethod(spider.authMethod);
                clientFactory.setAuthUsernameField(spider.loginUserField);
                clientFactory.setAuthPasswordField(spider.loginPasswordField);

                crawlerConfig.setHttpClientFactory(clientFactory);
            }

            //Add generic settings
            SettingsUtil.getInstance().addGenericSettingsToProject(genericSettings, crawlerConfig);

            //Add proxy settings
            ProxyUtil.getInstance().addProxySettingsToProject(proxySettings, crawlerConfig);

            //TODO - Determine a business way to set canonical link detector (Now this is bypassed)
            GenericCanonicalLinkDetector gcld = new GenericCanonicalLinkDetector();
            gcld.setContentTypes(ContentType.GIF);
            crawlerConfig.setCanonicalLinkDetector(gcld);

            //Crawler importer
            final ImporterConfig importerConfig = new ImporterConfig();
            final BigConnectDOMTagger bcTagger = new BigConnectDOMTagger();
            //Configure tagger with Portia selectors
            List<Selector> selectors = buildSelectorList(spider);
            selectors.forEach((selector) -> {
                BigConnectDOMTagger.DOMExtractDetails eDetail = new BigConnectDOMTagger.DOMExtractDetails();
                eDetail.setSelector(selector.getSelector());
                eDetail.setToField(selector.getField());
                bcTagger.addDOMExtractDetails(eDetail);
            });

            importerConfig.setPreParseHandlers(bcTagger);
            crawlerConfig.setImporterConfig(importerConfig);

            crawlerConfigs.add(crawlerConfig);
        });

        int _idx = 0;
        HttpCrawlerConfig[] arrayCrawlerConfig = new HttpCrawlerConfig[crawlerConfigs.size()];
        for (HttpCrawlerConfig conf : crawlerConfigs) {
            arrayCrawlerConfig[_idx++] = conf;
        }
        config.setCrawlerConfigs(arrayCrawlerConfig);

        this.ensureInputPath(norconnexBase);
        String projectFilePath = norconnexBase + File.separator + Constants.NORCONNEX_INPUT_SUFFIX +
                                                                        File.separator + webUiProject.name + ".xml";
        try {
            config.saveToXML(new FileWriter(projectFilePath));
        } catch (IOException e) {
            log.error("Norconnex project file couldn't be created. WebUiProject name: "+webUiProject.name
                                                                +" | message: "+ e.getMessage());
        }

        return projectFilePath;
    }

    private List<Selector> buildSelectorList(WebUiSpider spider) {
        final List<Selector> selectors = new ArrayList<Selector>();

        spider.samples.forEach(sample -> {
            sample.items.forEach(item -> {
                item.annotations.forEach(a -> {
                    if(a instanceof WebUiAnnotation) {
                        WebUiAnnotation annotation = (WebUiAnnotation) a;
                        if(annotation.acceptSelectors.size() > 0) {
                            selectors.add(new Selector(annotation.field.name, annotation.selector+"|"+annotation.acceptSelectors.get(0)));
                        }
                    }
                });
            });
        });

        return selectors;
    }

    private class Selector {
        private String field;
        private String selector;

        /**
         * @param field
         * @param selector
         */
        public Selector(String field, String selector) {
            this.field = field;
            this.selector = selector;
        }

        /**
         * @return
         */
        public String getField() {
            return field;
        }

        /**
         * @param field
         */
        public void setField(String field) {
            this.field = field;
        }

        /**
         * @return
         */
        public String getSelector() {
            return selector;
        }

        /**
         * @param selector
         */
        public void setSelector(String selector) {
            this.selector = selector;
        }
    }
}
