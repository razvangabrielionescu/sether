package org.mware.sponge.service;

import com.norconex.collector.core.CollectorConfigLoader;
import com.norconex.collector.core.crawler.ICrawlerConfig;
import com.norconex.collector.http.HttpCollectorConfig;
import com.norconex.collector.http.crawler.HttpCrawlerConfig;
import com.norconex.importer.handler.tagger.impl.KeepOnlyTagger;
import liquibase.util.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mware.sponge.converter.NorconnexFileSystem;
import org.mware.sponge.crawl.committer.BigConnectCommitter;
import org.mware.sponge.crawl.tagger.BigConnectDOMTagger;
import org.mware.sponge.crawl.tagger.SimpleContentTagger;
import org.mware.sponge.domain.CommiterConfig;
import org.mware.sponge.domain.Project;
import org.mware.sponge.domain.Setting;
import org.mware.sponge.domain.Spider;
import org.mware.sponge.repository.CommiterConfigRepository;
import org.mware.sponge.repository.ProjectRepository;
import org.mware.sponge.repository.SettingRepository;
import org.mware.sponge.repository.SpiderRepository;
import org.mware.sponge.service.dto.ProjectDTO;
import org.mware.sponge.service.dto.SpiderDTO;
import org.mware.sponge.service.dto.util.OntologyProperty;
import org.mware.sponge.util.Constants;
import org.mware.sponge.webui.domain.WebUiProject;
import org.mware.sponge.webui.domain.WebUiSpider;
import org.mware.sponge.webui.repository.WebUiProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.*;

/**
 * Created by Dan on 7/14/2017.
 */
@Service
@Transactional
public class ProjectService {
    private final Logger log = LoggerFactory.getLogger(ProjectService.class);

    private final ProjectRepository projectRepository;
    private final SpiderRepository spiderRepository;
    private final CommiterConfigRepository commiterConfigRepository;
    private final SettingRepository settingRepository;
    private final WebUiProjectRepository webUiProjectRepository;

    /**
     * @param projectRepository
     * @param spiderRepository
     * @param commiterConfigRepository
     * @param settingRepository
     * @param webUiProjectRepository
     */
    public ProjectService(ProjectRepository projectRepository, SpiderRepository spiderRepository,
                          CommiterConfigRepository commiterConfigRepository, SettingRepository settingRepository,
                          WebUiProjectRepository webUiProjectRepository) {
        this.projectRepository = projectRepository;
        this.spiderRepository = spiderRepository;
        this.commiterConfigRepository = commiterConfigRepository;
        this.settingRepository = settingRepository;
        this.webUiProjectRepository = webUiProjectRepository;
    }

    /**
     * @param userName
     * @return
     */
    public List<ProjectDTO> findAllProjectsForUser(String userName) {
        List<Project> projects = projectRepository.findAllByUserName(userName);
        List<ProjectDTO> projectDTOs = new ArrayList<ProjectDTO>();
        projects.forEach(project -> {
            projectDTOs.add(new ProjectDTO(project));
        });

        return projectDTOs;
    }

    /**
     * @param project
     * @return
     */
    public Project saveProject(ProjectDTO project, String userName) {
        Project projectToSave = convertProjectDTOtoProject(project, userName);
        return projectRepository.save(projectToSave);
    }

    /**
     * @param norconnexBase
     * @param userName
     * @param projectName
     * @return
     */
    public List<String> findAllSourceFields(final String norconnexBase, String userName, String projectName) {
        List<String> sourceFields = new ArrayList<String>();
        List<Project> projects = projectRepository.findAllByUserName(userName);
        List<Project> toSearch = new ArrayList<Project>();
        if (projectName.equals("ALL")) {
            toSearch.addAll(projects);
        } else {
            for (Project project : projects) {
                if (project.getName().equals(projectName)) {
                    toSearch.add(project);
                    break;
                }
            }
        }
        toSearch.forEach((project) -> {
            String configPath = norconnexBase + File.separator
                + Constants.NORCONNEX_INPUT_SUFFIX + File.separator + project.getName() + ".xml";
            log.debug("Parsing file for source fields: "+configPath);

            if (!project.getTool().equals(Constants.TOOL_FILESYSTEM)) {
                try {
                    File configFile =
                        new File(configPath);
                    HttpCollectorConfig config = (HttpCollectorConfig)new CollectorConfigLoader(HttpCollectorConfig.class)
                        .loadCollectorConfig(configFile, null);
                    if (config != null) {
                        HttpCrawlerConfig crawlerConfig = null;
                        BigConnectDOMTagger tagger = null;
                        KeepOnlyTagger socialTagger = null;
                        if (config.getCrawlerConfigs() != null) {
                            for (ICrawlerConfig crawler : config.getCrawlerConfigs()) {
                                crawlerConfig = (HttpCrawlerConfig) crawler;

                                if (project.getTool().equals(Constants.TOOL_WEBUI)) {
                                    tagger = (BigConnectDOMTagger)crawlerConfig.getImporterConfig().getPreParseHandlers()[0];
                                    for (BigConnectDOMTagger.DOMExtractDetails detail : tagger.getDOMExtractDetailsList()) {
                                        if (!sourceFields.contains(detail.getToField())) {
                                            sourceFields.add(detail.getToField());
                                        }
                                    }
                                } else {
                                    socialTagger = (KeepOnlyTagger) crawlerConfig.getImporterConfig().getPreParseHandlers()[0];
                                    for (String field : socialTagger.getFields()) {
                                        if (!sourceFields.contains(field)) {
                                            sourceFields.add(field);
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        log.error("Fields were not found because norconnex config file couldn't be loaded. " +
                                                                    "WebUiProject: "+project.getName());
                    }
                } catch (IOException e) {
                    log.error("There was an error while extracting fields from norconnex config file. " +
                                                                    "WebUiProject: "+project.getName());
                    e.printStackTrace();
                }
            } else {
                //FileSystem Tool
                if (!sourceFields.contains(SimpleContentTagger.CONTENT_DESTINATION_FIELD)) {
                    sourceFields.add(SimpleContentTagger.CONTENT_DESTINATION_FIELD);
                }
            }
        });

        return sourceFields;
    }

    /**
     * @param committerConfigId
     * @return
     * @throws IOException
     */
    public List<OntologyProperty> findBigConnectOntologyFieldsForCommitterConfig(Long committerConfigId) throws IOException {
        List<OntologyProperty> result = new ArrayList<OntologyProperty>();
        CommiterConfig config = this.commiterConfigRepository.findOne(committerConfigId);
        log.info("Getting BigConnect connection from committer config: "+config.getName());
        if (!config.getCommiter().getName().equals(Constants.COMMITTER_BIGCONNECT_NAME)) {
            log.error("Attempted to get ontology for configuration which is not based on "+Constants.COMMITTER_BIGCONNECT_NAME+" committer");
            return result;
        }

        log.info("Connecting to BigConnect ontology api with: "+config.getBcUrl()+" "+config.getBcUsername()+" "+config.getBcPassword());
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(config.getBcUrl()+"/login");
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("username", config.getBcUsername()));
        urlParameters.add(new BasicNameValuePair("password", config.getBcPassword()));
        post.setEntity(new UrlEncodedFormEntity(urlParameters));

        HttpResponse response = client.execute(post);
        log.info("Response Code from BigConnect login: "
                                + response.getStatusLine().getStatusCode());
        if (response.getStatusLine().getStatusCode() == 200) {

            HttpGet get = new HttpGet(config.getBcUrl() + "/user/me");
            final String _cookie = response.getFirstHeader("set-cookie").getValue();
            get.addHeader("Cookie", _cookie);
            get.addHeader("Accept", "application/json");
            HttpResponse userResponse = client.execute(get);

            log.info("Response Code from BigConnect user request: "
                + userResponse.getStatusLine().getStatusCode());
            if (userResponse.getStatusLine().getStatusCode() == 200) {
                final String json = EntityUtils.toString(userResponse.getEntity());
                JSONObject user = new JSONObject(json);
                final String csrfToken = user.getString("csrfToken");
                final String workspaceId = user.getString("currentWorkspaceId");

                get = new HttpGet(config.getBcUrl()+"/ontology");
                get.addHeader("Cookie", response.getFirstHeader("set-cookie").getValue());
                get.addHeader("Accept", "application/json");
                //get.addHeader(BigConnectCommitter.CSRF_TOKEN_HEADER,csrfToken);
                get.addHeader(BigConnectCommitter.WORKSPACE_ID_HEADER,workspaceId);

                HttpResponse ontResponse = client.execute(get);

                log.info("Response Code from BigConnect ontology: "
                    + ontResponse.getStatusLine().getStatusCode());
                if (ontResponse.getStatusLine().getStatusCode() == 200) {
                    final String json1 = EntityUtils.toString(ontResponse.getEntity());
                    JSONObject ontology = new JSONObject(json1);
                    JSONArray properties = ontology.getJSONArray("properties");
                    JSONObject property = null;
                    for (int i=0; i<properties.length(); i++) {
                        property = properties.getJSONObject(i);
                        if (property.getBoolean("userVisible")) {
                            result.add(new OntologyProperty(property.getString("title"), property.getString("displayName")));
                        }
                    }
                } else {
                    log.error("BigConnect ontology fetch failed");
                    return result;
                }
            } else {
                log.error("BigConnect user info fetch failed");
            }

        } else {
            log.error("BigConnect login failed");
            return result;
        }

        return result;
    }

    public void synchronizeWebUiProject(WebUiProject webUiProject, String userName) {
        Optional<Project> optionalProject = projectRepository.findOneByNameAndUserName(webUiProject.name, userName);
        Project project = null;
        if (optionalProject.isPresent()) {
            project = optionalProject.get();
        }
        Setting setting = null;
        if (project == null || !project.getTool().equals(Constants.TOOL_WEBUI)) {
            project = new Project();
            project.setTool(Constants.TOOL_WEBUI);
            project.setName(webUiProject.name);
            project.setUserName(userName);
            project.setWebUiProject(webUiProject);
            webUiProject.spongeProject = project;
            //Add default settings
            setting = createDefaultSettings();
        }
        project.getSpiders().forEach((oldSpider) -> {
            spiderRepository.delete(oldSpider);
        });
        project.setSpiders(null);
        projectRepository.save(project);

        for (WebUiSpider webUiSpider : webUiProject.spiders) {
            Spider spider = new Spider();
            spider.setName("["+webUiProject.name+"] "+webUiSpider.name);
            spider.setProject(project);
            spiderRepository.save(spider);
        }

        saveSettings(setting, project);
    }

    /**
     * @param crawlers
     * @param socialProjectName
     * @param userName
     */
    public void synchronizeSocialProject(List<String> crawlers, String socialProjectName, String userName) {
        Optional<Project> optionalProject = projectRepository.findOneByNameAndUserName(socialProjectName, userName);
        Project project = null;
        if (optionalProject.isPresent()) {
            project = optionalProject.get();
        }
        Setting setting = null;
        if (project == null || !project.getTool().equals(Constants.TOOL_SOCIAL)) {
            project = new Project();
            project.setTool(Constants.TOOL_SOCIAL);
            project.setName(socialProjectName);
            project.setUserName(userName);

            //Add default settings
            setting = createDefaultSettings();
        }
        project.getSpiders().forEach((oldSpider) -> {
            spiderRepository.delete(oldSpider);
        });
        project.setSpiders(null);
        projectRepository.save(project);

        Spider spider = null;
        for (String crawler : crawlers) {
            spider = new Spider();
            spider.setName(crawler);
            spider.setProject(project);
            spiderRepository.save(spider);
        }

        saveSettings(setting, project);
    }

    /**
     * @param projectDTO
     * @param userName
     * @return
     */
    public Project createFileSystemProject(ProjectDTO projectDTO, String userName, String norconnexBase) {
        Project project = new Project();
        project.setName(projectDTO.getName());
        project.setTool(projectDTO.getTool());
        project.setUserName(userName);
        Project result = projectRepository.save(project);
        //Add default settings
        Setting setting = createDefaultSettings();
        saveSettings(setting, project);
        //Add one spider (File System Crawler)
        Spider spider = new Spider();
        spider.setName("["+projectDTO.getName()+"] "+Constants.NORCONNEX_DEFAULT_FILE_SYSTEM_CRAWLER_ID);
        spider.setProject(project);
        spiderRepository.save(spider);

        NorconnexFileSystem.getInstance().createNorconnexFileSystemProjectFile(projectDTO, norconnexBase);

        return result;
    }

    private Setting createDefaultSettings() {
        Setting setting = new Setting();
        setting.setFilterExtensions(Constants.NORCONNEX_CRAWLER_DEFAULT_EXTENSIONS_REJECT);
        setting.setMaxDepth(Constants.NORCONNEX_CRAWLER_DEFAULT_MAX_DEPTH);
        setting.setNumThreads(Constants.NORCONNEX_CRAWLER_DEFAULT_THREAD_NUM);
        setting.setStayOnDomain(Constants.NORCONNEX_CRAWL_SCOPE_STRATEGY_STAY_ON_DOMAIN);
        setting.setStayOnPort(Constants.NORCONNEX_CRAWL_SCOPE_STRATEGY_STAY_ON_PORT);
        setting.setStayOnProtocol(Constants.NORCONNEX_CRAWL_SCOPE_STRATEGY_STAY_ON_PROTOCOL);

        return setting;
    }

    private void saveSettings(Setting setting, Project project) {
        if (setting != null) {
            setting.setProject(project);
            Setting _savedSetting = settingRepository.save(setting);
            project.setSetting(_savedSetting);
            projectRepository.save(project);
        }
    }

    private Project convertProjectDTOtoProject(ProjectDTO projectDTO, String userName) {
        Project project = new Project();
        project.setId(projectDTO.getId());
        project.setName(projectDTO.getName());
        project.setUserName(userName);
        project.setTool(projectDTO.getTool());
        project.setSetting(projectDTO.getSetting());
        project.setJob(projectDTO.getJob());
        project.setAgent(projectDTO.getAgent());
        project.setCommiterConfig(projectDTO.getCommiterConfig());
        project.setDescription(projectDTO.getDescription());
        project.setWebUiProject(findWebUiProject(projectDTO.getWebUiProjectId()));
        project.setSpiders(convertSpiderDTOSetToSpiderSet(projectDTO.getSpiders(), project));

        return project;
    }

    private Set<Spider> convertSpiderDTOSetToSpiderSet(Set<SpiderDTO> spiderDTOs, Project project) {
        Set<Spider> spiders = new HashSet<Spider>();
        Spider spider = null;
        for (SpiderDTO spiderDTO : spiderDTOs) {
            spider = new Spider();
            spider.setId(spiderDTO.getId());
            spider.setName(spiderDTO.getName());
            spider.setProject(project);

            spiders.add(spider);
        }

        return spiders;
    }

    private WebUiProject findWebUiProject(String webUiProjectId) {
        if (StringUtils.isEmpty(webUiProjectId)) {
            return null;
        }

        return webUiProjectRepository.findOne(Long.parseLong(webUiProjectId));
    }
}