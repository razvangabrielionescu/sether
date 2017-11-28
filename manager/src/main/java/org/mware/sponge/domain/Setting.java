package org.mware.sponge.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Setting.
 */
@Entity
@Table(name = "setting")
public class Setting implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "stay_on_domain", nullable = false)
    private Boolean stayOnDomain;

    @NotNull
    @Column(name = "stay_on_port", nullable = false)
    private Boolean stayOnPort;

    @NotNull
    @Column(name = "stay_on_protocol", nullable = false)
    private Boolean stayOnProtocol;

    @Column(name = "num_threads")
    private Integer numThreads;

    @Column(name = "max_depth")
    private Integer maxDepth;

    @Column(name = "filter_extensions")
    private String filterExtensions;

    @OneToOne(mappedBy = "setting", fetch = FetchType.LAZY)
    @JsonIgnore
    private Project project;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean isStayOnDomain() {
        return stayOnDomain;
    }

    public Setting stayOnDomain(Boolean stayOnDomain) {
        this.stayOnDomain = stayOnDomain;
        return this;
    }

    public void setStayOnDomain(Boolean stayOnDomain) {
        this.stayOnDomain = stayOnDomain;
    }

    public Boolean isStayOnPort() {
        return stayOnPort;
    }

    public Setting stayOnPort(Boolean stayOnPort) {
        this.stayOnPort = stayOnPort;
        return this;
    }

    public void setStayOnPort(Boolean stayOnPort) {
        this.stayOnPort = stayOnPort;
    }

    public Boolean isStayOnProtocol() {
        return stayOnProtocol;
    }

    public Setting stayOnProtocol(Boolean stayOnProtocol) {
        this.stayOnProtocol = stayOnProtocol;
        return this;
    }

    public void setStayOnProtocol(Boolean stayOnProtocol) {
        this.stayOnProtocol = stayOnProtocol;
    }

    public Integer getNumThreads() {
        return numThreads;
    }

    public Setting numThreads(Integer numThreads) {
        this.numThreads = numThreads;
        return this;
    }

    public void setNumThreads(Integer numThreads) {
        this.numThreads = numThreads;
    }

    public Integer getMaxDepth() {
        return maxDepth;
    }

    public Setting maxDepth(Integer maxDepth) {
        this.maxDepth = maxDepth;
        return this;
    }

    public void setMaxDepth(Integer maxDepth) {
        this.maxDepth = maxDepth;
    }

    public String getFilterExtensions() {
        return filterExtensions;
    }

    public Setting filterExtensions(String filterExtensions) {
        this.filterExtensions = filterExtensions;
        return this;
    }

    public void setFilterExtensions(String filterExtensions) {
        this.filterExtensions = filterExtensions;
    }

    public Project getProject() {
        return project;
    }

    public Setting project(Project project) {
        this.project = project;
        return this;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Setting setting = (Setting) o;
        if (setting.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), setting.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Setting{" +
            "id=" + getId() +
            ", stayOnDomain='" + isStayOnDomain() + "'" +
            ", stayOnPort='" + isStayOnPort() + "'" +
            ", stayOnProtocol='" + isStayOnProtocol() + "'" +
            ", numThreads='" + getNumThreads() + "'" +
            ", maxDepth='" + getMaxDepth() + "'" +
            ", filterExtensions='" + getFilterExtensions() + "'" +
            "}";
    }
}
