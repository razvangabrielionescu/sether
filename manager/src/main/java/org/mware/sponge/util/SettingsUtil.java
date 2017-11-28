package org.mware.sponge.util;

import com.norconex.collector.http.crawler.HttpCrawlerConfig;
import liquibase.util.StringUtils;
import org.mware.sponge.exception.SystemConfigurationNotFoundException;
import org.mware.sponge.repository.SystemConfigurationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Dan on 8/28/2017.
 */
public class SettingsUtil {
    private static final Logger log = LoggerFactory.getLogger(SettingsUtil.class);
    private static SettingsUtil INSTANCE = null;

    private SettingsUtil() {}

    /**
     * @return
     */
    public static synchronized SettingsUtil getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SettingsUtil();
        }

        return INSTANCE;
    }

    /**
     * @param systemConfigurationRepository
     * @return
     */
    public GenericSettings getGenericSettings(SystemConfigurationRepository systemConfigurationRepository) {
        log.debug("Retrieving generic settings");

        GenericSettings _settings = new GenericSettings();

        try {
            _settings.setUserAgent(systemConfigurationRepository.findOneByConfigKey(
                Constants.CONFIG_KEY_NORCONEX_USER_AGENT).map(config -> {
                return config.getConfigValue();
            }).orElseThrow(() -> new SystemConfigurationNotFoundException(
                "System Configuration: " +  Constants.CONFIG_KEY_NORCONEX_USER_AGENT +
                    " was not found in the database")));

        } catch (SystemConfigurationNotFoundException e) {
            log.error("Generic settings could not be loaded with message: "+e.getMessage());
        }

        return _settings;
    }

    /**
     * @param genericSettings
     * @param crawlerConfig
     */
    public void addGenericSettingsToProject(GenericSettings genericSettings, HttpCrawlerConfig crawlerConfig) {
        if (!StringUtils.isEmpty(genericSettings.getUserAgent())) {
            crawlerConfig.setUserAgent(genericSettings.getUserAgent());
        }
    }
}
