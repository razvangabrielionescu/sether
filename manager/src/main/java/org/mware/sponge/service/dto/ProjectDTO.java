package org.mware.sponge.service.dto;

import org.mware.sponge.domain.*;
import org.mware.sponge.service.dto.util.IJob;
import org.mware.sponge.service.dto.util.StatusDTO;

import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Dan on 7/14/2017.
 */
public class ProjectDTO implements IJob {
    private Long id;
    private String name;
    private String description;
    private String tool;
    private Set<SpiderDTO> spiders;
    private CommiterConfig commiterConfig;
    private Agent agent;
    private Job job;
    private Setting setting;
    private String webUiProjectId;

    //    Transient fields
    private StatusDTO status;
    private boolean show;
    private int schedulePeriod;
    private String scheduleUnit;
    private String tableName;
    private String startUrl;

    /**
     *
     */
    public ProjectDTO() {
    }

    /**
     * @param project
     */
    public ProjectDTO(Project project) {
        this(project.getId(),
                project.getName(),
                project.getDescription(),
                project.getTool(),
                project.getWebUiProject() != null ? project.getWebUiProject().id.toString() : null,
                new TreeSet<SpiderDTO>(),
                project.getCommiterConfig(),
                project.getAgent(),
                project.getJob(),
                new StatusDTO(),
                (project.getSpiders() != null && !project.getSpiders().isEmpty()) ? true : false, //Expand projects with spiders by default
                0, "", project.getSetting(), "", "");

        project.getSpiders().forEach((spider) -> {
            this.getSpiders().add(new SpiderDTO(spider));
        });
    }

    /**
     * @param id
     * @param name
     * @param description
     * @param tool
     * @param spiders
     * @param commiterConfig
     * @param agent
     * @param job
     * @param status
     * @param show
     * @param schedulePeriod
     * @param scheduleUnit
     * @param setting
     * @param tableName
     * @param startUrl
     */
    public ProjectDTO(Long id, String name, String description, String tool,
                      String webUiProjectId, Set<SpiderDTO> spiders, CommiterConfig commiterConfig, Agent agent, Job job,
                      StatusDTO status, boolean show, int schedulePeriod, String scheduleUnit, Setting setting,
                      String tableName, String startUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.tool = tool;
        this.spiders = spiders;
        this.commiterConfig = commiterConfig;
        this.agent = agent;
        this.job = job;
        this.status = status;
        this.show = show;
        this.schedulePeriod = schedulePeriod;
        this.scheduleUnit = scheduleUnit;
        this.setting = setting;
        this.tableName = tableName;
        this.startUrl = startUrl;
        this.webUiProjectId = webUiProjectId;
    }

    /**
     * @return
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return
     */
    public String getTool() {
        return tool;
    }

    /**
     * @param tool
     */
    public void setTool(String tool) {
        this.tool = tool;
    }

    /**
     * @return
     */
    public Set<SpiderDTO> getSpiders() {
        return spiders;
    }

    /**
     * @param spiders
     */
    public void setSpiders(Set<SpiderDTO> spiders) {
        this.spiders = spiders;
    }

    /**
     * @return
     */
    public StatusDTO getStatus() {
        return status;
    }

    /**
     * @param status
     */
    public void setStatus(StatusDTO status) {
        this.status = status;
    }

    /**
     * @return
     */
    public boolean isShow() {
        return show;
    }

    /**
     * @param show
     */
    public void setShow(boolean show) {
        this.show = show;
    }

    /**
     * @return
     */
    public CommiterConfig getCommiterConfig() {
        return commiterConfig;
    }

    /**
     * @param commiterConfig
     */
    public void setCommiterConfig(CommiterConfig commiterConfig) {
        this.commiterConfig = commiterConfig;
    }

    /**
     * @return
     */
    public Agent getAgent() {
        return agent;
    }

    /**
     * @param agent
     */
    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    /**
     * @return
     */
    public Job getJob() {
        return job;
    }

    /**
     * @param job
     */
    public void setJob(Job job) {
        this.job = job;
    }

    /**
     * @return
     */
    public int getSchedulePeriod() {
        return schedulePeriod;
    }

    /**
     * @param schedulePeriod
     */
    public void setSchedulePeriod(int schedulePeriod) {
        this.schedulePeriod = schedulePeriod;
    }

    /**
     * @return
     */
    public String getScheduleUnit() {
        return scheduleUnit;
    }

    /**
     * @param scheduleUnit
     */
    public void setScheduleUnit(String scheduleUnit) {
        this.scheduleUnit = scheduleUnit;
    }

    /**
     * @return
     */
    public Setting getSetting() {
        return setting;
    }

    /**
     * @param setting
     */
    public void setSetting(Setting setting) {
        this.setting = setting;
    }

    /**
     * @return
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @param tableName
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * @return
     */
    public String getStartUrl() {
        return startUrl;
    }

    /**
     * @param startUrl
     */
    public void setStartUrl(String startUrl) {
        this.startUrl = startUrl;
    }

    public String getWebUiProjectId() {
        return webUiProjectId;
    }

    public void setWebUiProjectId(String webUiProjectId) {
        this.webUiProjectId = webUiProjectId;
    }

    @Override
    public String toString() {
        return "ProjectDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", tool='" + tool + '\'' +
                ", spiders=" + spiders +
                ", commiterConfig=" + commiterConfig +
                ", agent=" + agent +
                ", job=" + job +
                ", setting=" + setting +
                ", status=" + status +
                ", show=" + show +
                ", schedulePeriod=" + schedulePeriod +
                ", scheduleUnit='" + scheduleUnit + '\'' +
                ", tableName='" + tableName + '\'' +
                ", startUrl='" + startUrl + '\'' +
                ", webUiProjectId='" + webUiProjectId + '\'' +
                '}';
    }
}
