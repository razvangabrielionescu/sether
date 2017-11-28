package org.mware.sponge;

import com.norconex.collector.core.CollectorConfigLoader;
import com.norconex.collector.core.ICollector;
import com.norconex.collector.core.ICollectorConfig;
import com.norconex.collector.core.crawler.ICrawlerConfig;
import com.norconex.collector.core.filter.IReferenceFilter;
import com.norconex.collector.core.filter.impl.ExtensionReferenceFilter;
import com.norconex.collector.fs.FilesystemCollector;
import com.norconex.collector.fs.FilesystemCollectorConfig;
import com.norconex.collector.fs.crawler.FilesystemCrawlerConfig;
import com.norconex.collector.http.HttpCollector;
import com.norconex.collector.http.HttpCollectorConfig;
import com.norconex.collector.http.crawler.HttpCrawlerConfig;
import com.norconex.jef4.status.JobSuiteStatusSnapshot;
import org.mware.sponge.agent.SpongeAgentClient;
import org.mware.sponge.committer.CommitterFactory;
import org.mware.sponge.domain.CommiterFieldMapping;
import org.mware.sponge.domain.Job;
import org.mware.sponge.domain.Project;
import org.mware.sponge.domain.Setting;
import org.mware.sponge.exception.UnimplementedCommitterException;
import org.mware.sponge.monitor.ProjectMonitor;
import org.mware.sponge.repository.*;
import org.mware.sponge.service.dto.ProjectDTO;
import org.mware.sponge.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by Dan on 7/14/2017.
 */
@Component
public class ProjectController {
    private static final Logger log = LoggerFactory.getLogger(ProjectController.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public static final Map<String, ICollector> collectorsMap =
                            Collections.synchronizedMap(new HashMap<String, ICollector>());
    private static final Map<Long, ScheduledFuture<?>> jobs =
                            Collections.synchronizedMap(new HashMap<Long, ScheduledFuture<?>>());

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private CommiterFieldMappingRepository commiterFieldMappingRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private JobRepository jobRepository;

    private String norconnexBase;
    private boolean runCollectorLocal;

    private String socialFolder;
    private ProjectMonitor monitor;
    private ScheduledExecutorService scheduler;

    /**
     *
     */
    public void startProjectMonitor() {
        File[] targetFiles = new File[1];
        targetFiles[0] = new File(this.norconnexBase + File.separator
                                                + Constants.NORCONNEX_PROGRESS_LATEST_SUFFIX);
        monitor = new ProjectMonitor(targetFiles, new IMonitorAction() {
            @Override
            public void doAction() {
                template.convertAndSend("/topic/project", "refresh");
            }
        });
        monitor.startMonitoring();

        this.initScheduler();
    }

    /**
     *
     */
    @PreDestroy
    private void stopProjectMonitor() {
        if (monitor != null) {
            monitor.startMonitoring();
        }
    }

    private void initScheduler() {
        scheduler = Executors.newScheduledThreadPool(Constants.SCHEDULER_POOL_SIZE);

        //Remove all existing jobs after system reboot
        projectRepository.findAll().forEach(project -> {
            project.setJob(null);
            projectRepository.save(project);
        });
        jobRepository.deleteAll();
    }

    private void prepareSettings(ProjectDTO project) {
        log.info("Preparing settings for project: "+project.getName());
        Setting _setting = project.getSetting();
        if (_setting == null) {
            log.warn("There was a problem getting the project settings for project: "+project.getName());
            return;
        }

        try {
            File configFile =
                new File(this.norconnexBase + File.separator
                    + Constants.NORCONNEX_INPUT_SUFFIX + File.separator + project.getName() + ".xml");

            ICollectorConfig config = null;
            if (project.getTool().equals(Constants.TOOL_FILESYSTEM)) {
                config = this.configureFileSystemCollectorSettings(_setting, configFile);
            } else {
                config = this.configureHttpCollectorSettings(_setting, configFile);
            }

            if (config != null) {
                config.saveToXML(new FileWriter(configFile));
            }
        } catch (IOException e) {
            log.error("There was a problem preparing settings for project: "+project.getName()+". The project will run with default settings.");
            e.printStackTrace();
        }
    }

    private ICollectorConfig configureHttpCollectorSettings(Setting setting, File configFile) throws IOException{
        HttpCollectorConfig config = (HttpCollectorConfig)new CollectorConfigLoader(HttpCollectorConfig.class)
            .loadCollectorConfig(configFile, null);
        HttpCrawlerConfig crawlerConfig = null;
        for (ICrawlerConfig crawler : config.getCrawlerConfigs()) {
            crawlerConfig = (HttpCrawlerConfig)crawler;

            crawlerConfig.setMaxDepth(setting.getMaxDepth());
            crawlerConfig.setNumThreads(setting.getNumThreads());
            crawlerConfig.getURLCrawlScopeStrategy().setStayOnDomain(setting.isStayOnDomain());
            crawlerConfig.getURLCrawlScopeStrategy().setStayOnPort(setting.isStayOnPort());
            crawlerConfig.getURLCrawlScopeStrategy().setStayOnProtocol(setting.isStayOnProtocol());
            IReferenceFilter[] referenceFilters = crawlerConfig.getReferenceFilters();
            if (referenceFilters != null) {
                for (int i = 0; i < referenceFilters.length; i++) {
                    if (referenceFilters[i] instanceof ExtensionReferenceFilter) {
                        ((ExtensionReferenceFilter) referenceFilters[i])
                            .setExtensions(setting.getFilterExtensions());
                    }
                }
            }
        }

        return config;
    }

    private ICollectorConfig configureFileSystemCollectorSettings(Setting setting, File configFile) throws IOException{
        FilesystemCollectorConfig config = (FilesystemCollectorConfig)new CollectorConfigLoader(FilesystemCollectorConfig.class)
            .loadCollectorConfig(configFile, null);
        FilesystemCrawlerConfig fscrawlerConfig = null;
        for (ICrawlerConfig crawler : config.getCrawlerConfigs()) {
            fscrawlerConfig = (FilesystemCrawlerConfig)crawler;

            fscrawlerConfig.setNumThreads(setting.getNumThreads());
            String[] pathsFiles = new String[1];
            pathsFiles[0] = "";
            fscrawlerConfig.setPathsFiles(pathsFiles);
        }

        return config;
    }

    private void prepareCommitter(ProjectDTO project) {
        log.info("Preparing committer for project: "+project.getName());

        addFieldMappingsToProject(project);
        try {
            File configFile =
                new File(this.norconnexBase + File.separator
                    + Constants.NORCONNEX_INPUT_SUFFIX + File.separator + project.getName() + ".xml");

            ICollectorConfig config = null;
            if (project.getTool().equals(Constants.TOOL_FILESYSTEM)) {
                config = this.configureFileSystemCollectorCommitter(project, configFile);
            } else {
                config = this.configureHttpCollectorCommitter(project, configFile);
            }

            if (config != null) {
                config.saveToXML(new FileWriter(configFile));
            }
        } catch (IOException e) {
            log.error("There was a problem preparing committer for project: "+project.getName()+". The project will run without committer.");
            e.printStackTrace();
        }
    }

    private ICollectorConfig configureHttpCollectorCommitter(ProjectDTO project, File configFile) throws IOException{
        HttpCollectorConfig config = (HttpCollectorConfig)new CollectorConfigLoader(HttpCollectorConfig.class)
            .loadCollectorConfig(configFile, null);
        HttpCrawlerConfig crawlerConfig = null;
        for (ICrawlerConfig crawler : config.getCrawlerConfigs()) {
            crawlerConfig = (HttpCrawlerConfig)crawler;

            //Add committer
            try {
                crawlerConfig.setCommitter(
                    CommitterFactory.buildCommitter(project));
            } catch (UnimplementedCommitterException e) {
                log.error("Committer factory failed with message: "+e.getMessage());
                e.printStackTrace();
            }
        }

        return config;
    }

    private ICollectorConfig configureFileSystemCollectorCommitter(ProjectDTO project, File configFile) throws IOException{
        FilesystemCollectorConfig config = (FilesystemCollectorConfig)new CollectorConfigLoader(FilesystemCollectorConfig.class)
            .loadCollectorConfig(configFile, null);
        FilesystemCrawlerConfig fscrawlerConfig = null;
        for (ICrawlerConfig crawler : config.getCrawlerConfigs()) {
            fscrawlerConfig = (FilesystemCrawlerConfig)crawler;

            //Add committer
            try {
                fscrawlerConfig.setCommitter(
                    CommitterFactory.buildCommitter(project));
                String[] pathsFiles = new String[1];
                pathsFiles[0] = "";
                fscrawlerConfig.setPathsFiles(pathsFiles);
            } catch (UnimplementedCommitterException e) {
                log.error("Committer factory failed with message: "+e.getMessage());
                e.printStackTrace();
            }
        }

        return config;
    }

    private void addFieldMappingsToProject(ProjectDTO project) {
        log.info("Adding field mappings to project DTO");
        List<CommiterFieldMapping> commiterFieldMappings =
            commiterFieldMappingRepository.findAllByCommiterConfigId(project.getCommiterConfig().getId());
        log.info("Found: "+commiterFieldMappings.size()+" mappings");

        project.getCommiterConfig().setCommiterFieldMappings(new HashSet<CommiterFieldMapping>(commiterFieldMappings));
    }

    /**
     * @param project
     */
    public void startProjectScheduled(ProjectDTO project) {
        if (jobs.containsKey(project.getId())) {
            log.warn("The project: "+project.getName()+" was already scheduled. Operation canceled");
            return;
        }

        ScheduledFuture<?> future =
            scheduler.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    startProject(project);
                }
            }, 0, project.getSchedulePeriod(),
                project.getScheduleUnit().equals("HOUR") ? TimeUnit.HOURS :
                    project.getScheduleUnit().equals("DAY") ? TimeUnit.DAYS : TimeUnit.MINUTES);

        jobs.put(project.getId(), future);

        // Add job child
        Job job = new Job();
        job.setPeriod(project.getSchedulePeriod());
        job.setPeriodUnit(project.getScheduleUnit());
        job.setStartTime(LocalDate.now());

        jobRepository.save(job);
        Project projectDomain = projectRepository.findOne(project.getId());
        projectDomain.setJob(job);
        projectRepository.save(projectDomain);
    }

    /**
     * @param project
     */
    public void removeProjectSchedule(ProjectDTO project) {
        if (!jobs.containsKey(project.getId())) {
            log.warn("The project: "+project.getName()+" is not scheduled. Operation canceled");
            return;
        }

        ScheduledFuture<?> future = jobs.get(project.getId());
        future.cancel(false);
        jobs.remove(project.getId());

        // Remove job child
        Project projectDomain = projectRepository.findOne(project.getId());
        projectDomain.setJob(null);
        projectRepository.save(projectDomain);
    }

    /**
     * @param project
     */
    public void startProject(ProjectDTO project) {
        this.prepareSettings(project);
        this.prepareCommitter(project);

        if (!this.runCollectorLocal) {
            this.startProjectRemote(project);
            return;
        }

        log.info("Start project: "+project.getName()+" locally");
//        if (!collectorsMap.containsKey(project.getName())) {
            File collectorFile =
                new File(this.norconnexBase + File.separator
                    + Constants.NORCONNEX_INPUT_SUFFIX + File.separator + project.getName() + ".xml");
            log.info("Starting project with configuration file: "+collectorFile.getAbsolutePath());
            try {
                final ICollector _collector = buildCollector(project, collectorFile);
                collectorsMap.put(project.getName(), _collector);
                new Thread(new Runnable() {
                    public void run() {
                        _collector.start(true);
                    }
                }).start();
            } catch (IOException e) {
                log.error("Error while loading collector config file with message: "+e.getMessage());
            }
//        } else {
//            log.warn("Trying to start an already running project.");
//        }
    }

    private ICollector buildCollector(ProjectDTO project, File collectorFile)
                                                                        throws IOException{
        ICollector _collector = null;
        if (!project.getTool().equals(Constants.TOOL_FILESYSTEM)) {
            HttpCollectorConfig config = (HttpCollectorConfig)new CollectorConfigLoader(HttpCollectorConfig.class)
                .loadCollectorConfig(collectorFile, null);
            _collector = new HttpCollector(config);
        } else {
            FilesystemCollectorConfig config = (FilesystemCollectorConfig)new CollectorConfigLoader(FilesystemCollectorConfig.class)
                .loadCollectorConfig(collectorFile, null);
            _collector = new FilesystemCollector(config);
        }

        return _collector;
    }

    /**
     * @param project
     */
    public void stopProject(ProjectDTO project) {
        if (!this.runCollectorLocal) {
            this.stopProjectRemote(project);
            return;
        }

        log.debug("Stop project: "+project.getName()+" locally");
        if (collectorsMap.containsKey(project.getName())) {
            try {
                collectorsMap.get(project.getName()).stop();
            } catch(Exception e) {
                log.trace("There was a problem stopping project "+project.getName());
            } finally {
                collectorsMap.remove(project.getName());
            }
        } else {
            if (project.getStatus() == null ||
                !project.getStatus().getStatus().equals(Constants.NORCONNEX_COMPLETED_STATUS)) {
                log.warn("WebUiProject: "+project.getName()+" can't be stopped because is not running.");
            }
        }
    }

    private void startProjectRemote(ProjectDTO project) {
        log.info("Start project: "+project.getName()+" remote");
        SpongeAgentClient.getInstance().sendStartCommand(project.getAgent(), project.getName());
    }

    private void stopProjectRemote(ProjectDTO project) {
        log.debug("Stop project: "+project.getName()+" remote");
        SpongeAgentClient.getInstance().sendStopCommand(project.getAgent(), project.getName());
    }

    /**
     * @param tool
     * @param projectName
     * @param userName
     */
    public void deleteProject(String tool, String projectName, String userName) {
//        Delete norconnex project
        deleteNorconnexProject(projectName);

//        Delete webui/social project
        if (tool.equals(Constants.TOOL_WEBUI)) {

            // delete webui project
            // Cascade

        } else if (tool.equals(Constants.TOOL_SOCIAL)) {
            File socialProject =
                new File(this.socialFolder + File.separator + userName + "_" + projectName);
            if (socialProject.exists()) {
                deleteDir(socialProject);
            }
        }
    }

    private void deleteNorconnexProject(String projectName) {
        String path = this.norconnexBase + File.separator +
                Constants.NORCONNEX_INPUT_SUFFIX + File.separator + projectName + ".xml";

        log.info("Deleting norconex file: " + path);
        File norconnexProject = new File(path);
        if (norconnexProject.exists()) {
            norconnexProject.delete();
        }
    }

    private boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));

                if (!success) {
                    return false;
                }
            }
        }
        // The directory is now empty so delete it
        return dir.delete();
    }


    /**
     * @return
     */
    public Map<String, ProjectStatus> getProjectStatusMap() {
        final Map<String, ProjectStatus> map = new HashMap<String, ProjectStatus>();

        Collection<JobSuiteStatusSnapshot> statusCollection = monitor.getJobSuitesStatuses();
        statusCollection.forEach((status) -> {
            status.getJobStatusList().forEach((jobStatus) -> {
                ProjectStatus projectStatus = new ProjectStatus();
                projectStatus.setStatus(jobStatus.getState().toString());
                if (jobStatus.getLastActivity() != null) {
                    projectStatus.setLastActivity(DATE_FORMAT.format(jobStatus.getLastActivity()));
                } else {
                    projectStatus.setLastActivity("");
                }
                projectStatus.setNote(jobStatus.getNote());
                projectStatus.setProgress(jobStatus.getProgress()+"");

                map.put(jobStatus.getJobId(), projectStatus);
            });
        });

        return map;
    }

    /**
     *
     */
    public void getMonitorSnaphsot() {}


    /**
     * @return
     */
    public String getNorconnexBase() {
        return norconnexBase;
    }

    /**
     * @param norconnexBase
     */
    public void setNorconnexBase(String norconnexBase) {
        this.norconnexBase = norconnexBase;
    }

    /**
     * @return
     */
    public boolean isRunCollectorLocal() {
        return runCollectorLocal;
    }

    /**
     * @param runCollectorLocal
     */
    public void setRunCollectorLocal(boolean runCollectorLocal) {
        this.runCollectorLocal = runCollectorLocal;
    }


    /**
     * @return
     */
    public String getSocialFolder() {
        return socialFolder;
    }

    /**
     * @param socialFolder
     */
    public void setSocialFolder(String socialFolder) {
        log.info("Social project set in [ProjectController] to: "+socialFolder);
        this.socialFolder = socialFolder;
    }

    /**
     *
     */
    public interface IMonitorAction{
        /**
         *
         */
        public void doAction();
    }

    /**
     *
     */
    public class ProjectStatus {
        private String status;
        private String note;
        private String progress;
        private String lastActivity;

        /**
         * @return
         */
        public String getStatus() {
            return status;
        }

        /**
         * @param status
         */
        public void setStatus(String status) {
            this.status = status;
        }

        /**
         * @return
         */
        public String getNote() {
            return note;
        }

        /**
         * @param note
         */
        public void setNote(String note) {
            this.note = note;
        }

        /**
         * @return
         */
        public String getProgress() {
            return progress;
        }

        /**
         * @param progress
         */
        public void setProgress(String progress) {
            this.progress = progress;
        }

        /**
         * @return
         */
        public String getLastActivity() {
            return lastActivity;
        }

        /**
         * @param lastActivity
         */
        public void setLastActivity(String lastActivity) {
            this.lastActivity = lastActivity;
        }
    }
}
