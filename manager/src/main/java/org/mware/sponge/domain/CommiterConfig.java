package org.mware.sponge.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A CommiterConfig.
 */
@Entity
@Table(name = "commiter_config")
public class CommiterConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "db_driver_class")
    private String dbDriverClass;

    @Column(name = "db_connection_url")
    private String dbConnectionUrl;

    @Column(name = "db_driver_path")
    private String dbDriverPath;

    @Column(name = "db_username")
    private String dbUsername;

    @Column(name = "db_password")
    private String dbPassword;

    @Column(name = "db_create_missing")
    private String dbCreateMissing;

    @Column(name = "db_create_table_sql")
    private String dbCreateTableSQL;

    @Column(name = "db_commit_batch_size")
    private String dbCommitBatchSize;

    @Column(name = "bc_url")
    private String bcUrl;

    @Column(name = "bc_username")
    private String bcUsername;

    @Column(name = "bc_password")
    private String bcPassword;

    @Column(name = "fs_directory")
    private String fsDirectory;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @NotNull
    private Commiter commiter;

    @OneToMany(mappedBy = "commiterConfig", fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<CommiterFieldMapping> commiterFieldMappings = new HashSet<>();

    @OneToMany(mappedBy = "commiterConfig")
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

    public CommiterConfig name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public CommiterConfig description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDbDriverClass() {
        return dbDriverClass;
    }

    public CommiterConfig dbDriverClass(String dbDriverClass) {
        this.dbDriverClass = dbDriverClass;
        return this;
    }

    public void setDbDriverClass(String dbDriverClass) {
        this.dbDriverClass = dbDriverClass;
    }

    public String getDbConnectionUrl() {
        return dbConnectionUrl;
    }

    public CommiterConfig dbConnectionUrl(String dbConnectionUrl) {
        this.dbConnectionUrl = dbConnectionUrl;
        return this;
    }

    public void setDbConnectionUrl(String dbConnectionUrl) {
        this.dbConnectionUrl = dbConnectionUrl;
    }

    public String getDbDriverPath() {
        return dbDriverPath;
    }

    public CommiterConfig dbDriverPath(String dbDriverPath) {
        this.dbDriverPath = dbDriverPath;
        return this;
    }

    public void setDbDriverPath(String dbDriverPath) {
        this.dbDriverPath = dbDriverPath;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public CommiterConfig dbUsername(String dbUsername) {
        this.dbUsername = dbUsername;
        return this;
    }

    public void setDbUsername(String dbUsername) {
        this.dbUsername = dbUsername;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public CommiterConfig dbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
        return this;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    public String getDbCreateMissing() {
        return dbCreateMissing;
    }

    public CommiterConfig dbCreateMissing(String dbCreateMissing) {
        this.dbCreateMissing = dbCreateMissing;
        return this;
    }

    public void setDbCreateMissing(String dbCreateMissing) {
        this.dbCreateMissing = dbCreateMissing;
    }

    public String getDbCreateTableSQL() {
        return dbCreateTableSQL;
    }

    public CommiterConfig dbCreateTableSQL(String dbCreateTableSQL) {
        this.dbCreateTableSQL = dbCreateTableSQL;
        return this;
    }

    public void setDbCreateTableSQL(String dbCreateTableSQL) {
        this.dbCreateTableSQL = dbCreateTableSQL;
    }

    public String getDbCommitBatchSize() {
        return dbCommitBatchSize;
    }

    public CommiterConfig dbCommitBatchSize(String dbCommitBatchSize) {
        this.dbCommitBatchSize = dbCommitBatchSize;
        return this;
    }

    public void setDbCommitBatchSize(String dbCommitBatchSize) {
        this.dbCommitBatchSize = dbCommitBatchSize;
    }

    public String getBcUrl() {
        return bcUrl;
    }

    public CommiterConfig bcUrl(String bcUrl) {
        this.bcUrl = bcUrl;
        return this;
    }

    public void setBcUrl(String bcUrl) {
        this.bcUrl = bcUrl;
    }

    public String getBcUsername() {
        return bcUsername;
    }

    public CommiterConfig bcUsername(String bcUsername) {
        this.bcUsername = bcUsername;
        return this;
    }

    public void setBcUsername(String bcUsername) {
        this.bcUsername = bcUsername;
    }

    public String getBcPassword() {
        return bcPassword;
    }

    public CommiterConfig bcPassword(String bcPassword) {
        this.bcPassword = bcPassword;
        return this;
    }

    public void setBcPassword(String bcPassword) {
        this.bcPassword = bcPassword;
    }

    public Commiter getCommiter() {
        return commiter;
    }

    public CommiterConfig commiter(Commiter commiter) {
        this.commiter = commiter;
        return this;
    }

    /**
     * @return
     */
    public String getFsDirectory() {
        return fsDirectory;
    }

    /**
     * @param fsDirectory
     */
    public void setFsDirectory(String fsDirectory) {
        this.fsDirectory = fsDirectory;
    }

    public void setCommiter(Commiter commiter) {
        this.commiter = commiter;
    }

    public Set<CommiterFieldMapping> getCommiterFieldMappings() {
        return commiterFieldMappings;
    }

    public CommiterConfig commiterFieldMappings(Set<CommiterFieldMapping> commiterFieldMappings) {
        this.commiterFieldMappings = commiterFieldMappings;
        return this;
    }

    public CommiterConfig addCommiterFieldMapping(CommiterFieldMapping commiterFieldMapping) {
        this.commiterFieldMappings.add(commiterFieldMapping);
        commiterFieldMapping.setCommiterConfig(this);
        return this;
    }

    public CommiterConfig removeCommiterFieldMapping(CommiterFieldMapping commiterFieldMapping) {
        this.commiterFieldMappings.remove(commiterFieldMapping);
        commiterFieldMapping.setCommiterConfig(null);
        return this;
    }

    public void setCommiterFieldMappings(Set<CommiterFieldMapping> commiterFieldMappings) {
        this.commiterFieldMappings = commiterFieldMappings;
    }

    public Set<Project> getProjects() {
        return projects;
    }

    public CommiterConfig projects(Set<Project> projects) {
        this.projects = projects;
        return this;
    }

    public CommiterConfig addProject(Project project) {
        this.projects.add(project);
        project.setCommiterConfig(this);
        return this;
    }

    public CommiterConfig removeProject(Project project) {
        this.projects.remove(project);
        project.setCommiterConfig(null);
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
        CommiterConfig commiterConfig = (CommiterConfig) o;
        if (commiterConfig.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), commiterConfig.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "CommiterConfig{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", dbDriverClass='" + dbDriverClass + '\'' +
            ", dbConnectionUrl='" + dbConnectionUrl + '\'' +
            ", dbDriverPath='" + dbDriverPath + '\'' +
            ", dbUsername='" + dbUsername + '\'' +
            ", dbPassword='" + dbPassword + '\'' +
            ", dbCreateMissing='" + dbCreateMissing + '\'' +
            ", dbCreateTableSQL='" + dbCreateTableSQL + '\'' +
            ", dbCommitBatchSize='" + dbCommitBatchSize + '\'' +
            ", bcUrl='" + bcUrl + '\'' +
            ", bcUsername='" + bcUsername + '\'' +
            ", bcPassword='" + bcPassword + '\'' +
            ", fsDirectory='" + fsDirectory + '\'' +
            ", commiter=" + commiter +
            ", commiterFieldMappings=" + commiterFieldMappings +
            ", projects=" + projects +
            '}';
    }
}
