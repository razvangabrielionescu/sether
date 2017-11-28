package org.mware.sponge.listener;

import org.mware.sponge.ProjectManager;
import org.mware.sponge.exception.SystemConfigurationNotFoundException;
import org.mware.sponge.repository.SystemConfigurationRepository;
import org.mware.sponge.util.Constants;
import org.mware.sponge.webui.WebUiCommandHandler;
import org.mware.sponge.webui.browser.server.SpongeBrowserCallbackServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.File;

@Component
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {
    private static final Logger log = LoggerFactory.getLogger(ApplicationStartup.class);

    @Autowired
    private SystemConfigurationRepository systemConfigurationRepository;

    @Autowired
    private WebUiCommandHandler webUiCommandHandler;

    @Autowired
    private ProjectManager projectManager;

    private FileMonitor _socialMonitor;

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        log.info("Sponge application started. Preparing file monitor.");

        try {
            String socialMonitoredFolder = systemConfigurationRepository.findOneByConfigKey(
                Constants.CONFIG_KEY_SOCIAL_PROJECT_DIR).map(config -> {
                return config.getConfigValue();
            }).orElseThrow(() -> new SystemConfigurationNotFoundException(
                "System Configuration: " +  Constants.CONFIG_KEY_SOCIAL_PROJECT_DIR +
                    " was not found in the database"));
            log.info("Social projects directory: "+socialMonitoredFolder);

            Long socialPollPace = systemConfigurationRepository.findOneByConfigKey(
                Constants.CONFIG_KEY_SOCIAL_POLLING_PACE).map(config -> {
                return Long.parseLong(config.getConfigValue());
            }).orElseThrow(() -> new SystemConfigurationNotFoundException(
                "System Configuration: " +  Constants.CONFIG_KEY_SOCIAL_POLLING_PACE +
                    " was not found in the database"));
            log.info("Social projects polling pace: "+socialPollPace);

            createListeners(socialMonitoredFolder, socialPollPace);
            startBrowserCallbackServer();
        } catch (SystemConfigurationNotFoundException e) {
            log.error("SEVERE: Core module [FileMonitor] not started with message: "+e.getMessage());
        }
    }

    private void createListeners(String socialMonitoredFolder, long socialPollPace) {
        this.createSocialListener(socialMonitoredFolder, socialPollPace);
    }

    private void startBrowserCallbackServer() {
        SpongeBrowserCallbackServer _server = new SpongeBrowserCallbackServer();
        _server.setWebUiCommandHandler(webUiCommandHandler);
        _server.setPort(Constants.BROWSER_CALLBACK_SERVER_PORT);

        try {
            _server.startServer();
        } catch (Exception e) {
            log.error("Error starting sponge browser callback server with message: "+e.getMessage());
        }
    }

    private void createSocialListener(String monitoredFolder, long pollPace) {
        _socialMonitor = new FileMonitor(pollPace);
        projectManager.setSocialMonitoredFolder(monitoredFolder);

        File f = new File(monitoredFolder);
        if (f.exists()) {
            _socialMonitor.addFile(f);
            _socialMonitor.addListener(new SocialListener(projectManager));
        } else {
            log.error("SEVERE: Core module not started because social directory does not exist!");
        }
    }

    @PreDestroy
    private void stopFileMonitor() {
        if (this._socialMonitor != null) {
            _socialMonitor.stop();
        }
    }
}
