package org.mware.sponge.util;

import com.norconex.collector.http.client.impl.GenericHttpClientFactory;
import com.norconex.collector.http.crawler.HttpCrawlerConfig;
import org.apache.commons.lang.StringUtils;
import org.mware.sponge.exception.SystemConfigurationNotFoundException;
import org.mware.sponge.repository.SystemConfigurationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Dan on 8/28/2017.
 */
public class ProxyUtil {
    private static final Logger log = LoggerFactory.getLogger(ProxyUtil.class);
    private static ProxyUtil INSTANCE = null;

    private ProxyUtil() {}

    /**
     * @return
     */
    public static synchronized ProxyUtil getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ProxyUtil();
        }

        return INSTANCE;
    }

    /**
     * @param systemConfigurationRepository
     * @return
     */
    public ProxySettings getProxySettings(SystemConfigurationRepository systemConfigurationRepository) {
        log.debug("Retrieving proxy settings");

        ProxySettings _settings = new ProxySettings();

        try {
            _settings.setUsesProxy(systemConfigurationRepository.findOneByConfigKey(
                Constants.CONFIG_KEY_USES_PROXY).map(config -> {
                return StringUtils.isEmpty(config.getConfigValue()) ? false : Boolean.valueOf(config.getConfigValue());
            }).orElseThrow(() -> new SystemConfigurationNotFoundException(
                "System Configuration: " +  Constants.CONFIG_KEY_USES_PROXY +
                    " was not found in the database")));

            _settings.setProxyHost(systemConfigurationRepository.findOneByConfigKey(
                Constants.CONFIG_KEY_PROXY_HOST).map(config -> {
                return config.getConfigValue();
            }).orElseThrow(() -> new SystemConfigurationNotFoundException(
                "System Configuration: " +  Constants.CONFIG_KEY_PROXY_HOST +
                    " was not found in the database")));

            _settings.setProxyPort(systemConfigurationRepository.findOneByConfigKey(
                Constants.CONFIG_KEY_PROXY_PORT).map(config -> {
                return StringUtils.isEmpty(config.getConfigValue()) ? -1 : Integer.parseInt(config.getConfigValue());
            }).orElseThrow(() -> new SystemConfigurationNotFoundException(
                "System Configuration: " +  Constants.CONFIG_KEY_PROXY_PORT +
                    " was not found in the database")));

            _settings.setUsesAuth(systemConfigurationRepository.findOneByConfigKey(
                Constants.CONFIG_KEY_PROXY_USES_AUTH).map(config -> {
                return StringUtils.isEmpty(config.getConfigValue()) ? false : Boolean.valueOf(config.getConfigValue());
            }).orElseThrow(() -> new SystemConfigurationNotFoundException(
                "System Configuration: " +  Constants.CONFIG_KEY_PROXY_USES_AUTH +
                    " was not found in the database")));

            _settings.setUsername(systemConfigurationRepository.findOneByConfigKey(
                Constants.CONFIG_KEY_PROXY_USERNAME).map(config -> {
                return config.getConfigValue();
            }).orElseThrow(() -> new SystemConfigurationNotFoundException(
                "System Configuration: " +  Constants.CONFIG_KEY_PROXY_USERNAME +
                    " was not found in the database")));

            _settings.setPassword(systemConfigurationRepository.findOneByConfigKey(
                Constants.CONFIG_KEY_PROXY_PASSWORD).map(config -> {
                return config.getConfigValue();
            }).orElseThrow(() -> new SystemConfigurationNotFoundException(
                "System Configuration: " +  Constants.CONFIG_KEY_PROXY_PASSWORD +
                    " was not found in the database")));
        } catch (SystemConfigurationNotFoundException e) {
            log.error("Proxy settings could not be loaded with message: "+e.getMessage());
        }

        return _settings;
    }

    /**
     * @param proxySettings
     * @param crawlerConfig
     */
    public void addProxySettingsToProject(ProxySettings proxySettings, HttpCrawlerConfig crawlerConfig) {
        if (proxySettings.isUsesProxy()) {
            GenericHttpClientFactory clientFactory =
                (GenericHttpClientFactory) crawlerConfig.getHttpClientFactory();
            clientFactory.setProxyHost(proxySettings.getProxyHost());
            clientFactory.setProxyPort(proxySettings.getProxyPort());
            if (proxySettings.isUsesAuth()) {
                clientFactory.setProxyUsername(proxySettings.getUsername());
                clientFactory.setProxyPassword(proxySettings.getPassword());
            }
            crawlerConfig.setHttpClientFactory(clientFactory);
        }
    }
}
