package org.mware.sponge.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Agent.
 */
@Entity
@Table(name = "agent")
public class Agent implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "host", nullable = false)
    private String host;

    @NotNull
    @Column(name = "port", nullable = false)
    private Long port;

    @OneToMany(mappedBy = "agent")
    @JsonIgnore
    private Set<Project> projects = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Agent name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public Agent host(String host) {
        this.host = host;
        return this;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Long getPort() {
        return port;
    }

    public Agent port(Long port) {
        this.port = port;
        return this;
    }

    public void setPort(Long port) {
        this.port = port;
    }

    public Set<Project> getProjects() {
        return projects;
    }

    public Agent projects(Set<Project> projects) {
        this.projects = projects;
        return this;
    }

    public Agent addProject(Project project) {
        this.projects.add(project);
        project.setAgent(this);
        return this;
    }

    public Agent removeProject(Project project) {
        this.projects.remove(project);
        project.setAgent(null);
        return this;
    }

    public void setProjects(Set<Project> projects) {
        this.projects = projects;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Agent agent = (Agent) o;
        if (agent.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), agent.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Agent{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", host='" + getHost() + "'" +
            ", port='" + getPort() + "'" +
            "}";
    }
}
