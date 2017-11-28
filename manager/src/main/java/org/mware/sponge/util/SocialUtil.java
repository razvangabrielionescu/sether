package org.mware.sponge.util;

import org.mware.sponge.exception.SystemConfigurationNotFoundException;
import org.mware.sponge.repository.SystemConfigurationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Dan on 8/30/2017.
 */
public class SocialUtil {
    private static final Logger log = LoggerFactory.getLogger(SocialUtil.class);

    /**
     * @param systemConfigurationRepository
     * @return
     */
    public static SocialCredentials getSocialCredentials(SystemConfigurationRepository systemConfigurationRepository) {
        log.debug("Retrieving social credentials");
        SocialCredentials socialCredentials = new SocialCredentials();

        try {
            String appId = systemConfigurationRepository.findOneByConfigKey(
                Constants.CONFIG_KEY_SOCIAL_APP_ID).map(config -> {
                return config.getConfigValue();
            }).orElseThrow(() -> new SystemConfigurationNotFoundException(
                "System Configuration: " + Constants.CONFIG_KEY_SOCIAL_APP_ID +
                    " was not found in the database"));
            socialCredentials.setAppId(appId);

            String appSecret = systemConfigurationRepository.findOneByConfigKey(
                Constants.CONFIG_KEY_SOCIAL_APP_SECRET).map(config -> {
                return config.getConfigValue();
            }).orElseThrow(() -> new SystemConfigurationNotFoundException(
                "System Configuration: " + Constants.CONFIG_KEY_SOCIAL_APP_SECRET +
                    " was not found in the database"));
            socialCredentials.setAppSecret(appSecret);
        } catch(SystemConfigurationNotFoundException e) {
            log.error("There was a problem retrieving social credentials with message: "+e.getMessage());
        }

        return socialCredentials;
    }

    public static class SocialCredentials {
        private String appId;
        private String appSecret;

        /**
         *
         */
        public SocialCredentials() {
            this.appId = "";
            this.appSecret = "";
        }

        /**
         * @param appId
         * @param appSecret
         */
        public SocialCredentials(String appId, String appSecret) {
            this.appId = appId;
            this.appSecret = appSecret;
        }

        /**
         * @return
         */
        public String getAppId() {
            return appId;
        }

        /**
         * @param appId
         */
        public void setAppId(String appId) {
            this.appId = appId;
        }

        /**
         * @return
         */
        public String getAppSecret() {
            return appSecret;
        }

        /**
         * @param appSecret
         */
        public void setAppSecret(String appSecret) {
            this.appSecret = appSecret;
        }
    }
}
