package org.mware.sponge;

import org.mware.sponge.converter.NorconnexConverter;
import org.mware.sponge.converter.NorconnexSocial;
import org.mware.sponge.exception.SystemConfigurationNotFoundException;
import org.mware.sponge.repository.SystemConfigurationRepository;
import org.mware.sponge.service.ProjectService;
import org.mware.sponge.util.*;
import org.mware.sponge.webui.domain.WebUiProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileFilter;
import java.util.*;

@Service
@Transactional
public class ProjectManager {
    private static final Logger log = LoggerFactory.getLogger(ProjectManager.class);

    @Autowired
    private SystemConfigurationRepository systemConfigurationRepository;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectController projectController;

    @Autowired
    private NorconnexConverter norconnexConverter;

    @Autowired
    private SimpMessagingTemplate template;

    private String socialMonitoredFolder;
    private String norconnexBase;
    private boolean runCollectorLocal;

    public ProjectManager() {
    }

    @PostConstruct
    public void afterCreate() {
        init();
        startProjectMonitor();
    }

    private void init() {
        try {
            norconnexBase = systemConfigurationRepository.findOneByConfigKey(
                Constants.CONFIG_KEY_NORCONNEX_BASE_DIR).map(config -> {
                return config.getConfigValue();
            }).orElseThrow(() -> new SystemConfigurationNotFoundException(
                "System Configuration: " +  Constants.CONFIG_KEY_NORCONNEX_BASE_DIR +
                    " was not found in the database"));

            runCollectorLocal = systemConfigurationRepository.findOneByConfigKey(
                Constants.CONFIG_KEY_RUN_COLLECTOR_LOCAL).map(config -> {
                return Boolean.valueOf(config.getConfigValue());
            }).orElseThrow(() -> new SystemConfigurationNotFoundException(
                "System Configuration: " +  Constants.CONFIG_KEY_RUN_COLLECTOR_LOCAL +
                    " was not found in the database"));
        } catch (SystemConfigurationNotFoundException e) {
            log.error("SEVERE: Core module [ProjectManager] not started with message: "+e.getMessage());
        }
        log.info("Norconex base directory: " + norconnexBase);
        log.info("Run collectors local: " + runCollectorLocal);
    }

    private void startProjectMonitor() {
        projectController.setNorconnexBase(this.norconnexBase);
        projectController.setRunCollectorLocal(this.runCollectorLocal);
        projectController.startProjectMonitor();
    }

    public void syncWebUiproject(String userName, WebUiProject project) {
        String resultedFile = this.norconnexConverter.createNorconnexProjectFile(
                project,
                this.norconnexBase,
                ProxyUtil.getInstance().getProxySettings(systemConfigurationRepository),
                SettingsUtil.getInstance().getGenericSettings(systemConfigurationRepository));

        if (new File(resultedFile).exists()) {
            log.debug("WebUiProject: "+project.name+" was created at path: "+
                    resultedFile+". Syncing database info");
            try {
                projectService.synchronizeWebUiProject(project, userName);
            } catch(Throwable t) {
                log.error("Unexpected error syncing db project: "+project.name+" with message: "+t.getMessage());
            }
        } else {
            log.error("WebUiProject: "+project.name+" wasn't created!");
        }

        template.convertAndSend("/topic/project", "refresh");
    }

    /**
     *
     */
    public void handleSocialProjectsUpdated() {
        List<File> socialProjects = identifyProjects(this.socialMonitoredFolder);

        socialProjects.forEach((projectFolder) -> {
            if (projectFolder == null || !projectFolder.exists()) {
                return;
            }

            int idx = projectFolder.getName().indexOf("_");
            if (idx < 0) {
                log.info("Corrupt social project folder. No u_n format,");
                return;
            }
            String userName = projectFolder.getName().substring(0, idx);
            String projectName = projectFolder.getName().substring(idx + 1);
            List<String> crawlers = new ArrayList<String>();
            String resultedFile = NorconnexSocial.getInstance().createNorconnexSocialProjectFile(
                new File(projectFolder, projectFolder.getName() + ".xml"),
                projectName,
                this.norconnexBase,
                ProxyUtil.getInstance().getProxySettings(systemConfigurationRepository),
                SettingsUtil.getInstance().getGenericSettings(systemConfigurationRepository),
                crawlers,
                SocialUtil.getSocialCredentials(systemConfigurationRepository)
            );

            if (new File(resultedFile).exists()) {
                log.info("WebUiProject (Social): "+projectName+" was created at path: "+
                    resultedFile+". Syncing database info");
                try {
                    projectService.synchronizeSocialProject(crawlers, projectName, userName);
                } catch(Throwable t) {
                    log.error("Unexpected error syncing db project (Social): "+projectName+" with message: "+t.getMessage());
                }
            } else {
                log.error("WebUiProject (Social): "+projectName+" wasn't created!");
            }
        });

        template.convertAndSend("/topic/project", "refresh");
    }

    private List<File> identifyProjects(String folder) {
        return Arrays.asList(
            new File(folder).listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isDirectory();
                }
        }));
    }

    /**
     * @return
     */
    public String getSocialMonitoredFolder() {
        return socialMonitoredFolder;
    }

    /**
     * @param socialMonitoredFolder
     */
    public void setSocialMonitoredFolder(String socialMonitoredFolder) {
        this.socialMonitoredFolder = socialMonitoredFolder;
        projectController.setSocialFolder(this.socialMonitoredFolder);
    }
}
