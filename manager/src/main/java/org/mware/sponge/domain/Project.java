package org.mware.sponge.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.mware.sponge.webui.domain.WebUiProject;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A WebUiProject.
 */
@Entity
@Table(name = "project")
public class Project implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "tool")
    private String tool;

    @Column(name = "user_name")
    private String userName;

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.EAGER)
    private CommiterConfig commiterConfig;

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.EAGER)
    private Agent agent;

    @ManyToOne(fetch = FetchType.EAGER)
    private Job job;

    @OneToMany(mappedBy = "project", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private Set<Spider> spiders = new HashSet<>();

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private Setting setting;

    @OneToOne(mappedBy = "spongeProject", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private WebUiProject webUiProject;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Project name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public Project description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTool() {
        return tool;
    }

    public Project tool(String tool) {
        this.tool = tool;
        return this;
    }

    public void setTool(String tool) {
        this.tool = tool;
    }

    public String getUserName() {
        return userName;
    }

    public Project userName(String userName) {
        this.userName = userName;
        return this;
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
    public Setting getSetting() {
        return setting;
    }

    /**
     * @param setting
     */
    public void setSetting(Setting setting) {
        this.setting = setting;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Set<Spider> getSpiders() {
        return spiders;
    }

    public Project spiders(Set<Spider> spiders) {
        this.spiders = spiders;
        return this;
    }

    public Project addSpider(Spider spider) {
        this.spiders.add(spider);
        spider.setProject(this);
        return this;
    }

    public Project removeSpider(Spider spider) {
        this.spiders.remove(spider);
        spider.setProject(null);
        return this;
    }

    public void setSpiders(Set<Spider> spiders) {
        this.spiders = spiders;
    }

    public WebUiProject getWebUiProject() {
        return webUiProject;
    }

    public void setWebUiProject(WebUiProject webUiProject) {
        this.webUiProject = webUiProject;
    }

    public Long webUiProjectId() {
        if(webUiProject != null)
            return webUiProject.id;
        else
            return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Project project = (Project) o;
        if (project.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), project.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    /**
     * @return
     */
    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", tool='" + tool + '\'' +
                ", userName='" + userName + '\'' +
                ", commiterConfig=" + commiterConfig +
                ", agent=" + agent +
                ", job=" + job +
                ", spiders=" + spiders +
                ", setting=" + setting +
                ", webUiProject=" + webUiProject +
                '}';
    }
}
