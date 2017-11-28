package org.mware.sponge.domain;


import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A CommiterFieldMapping.
 */
@Entity
@Table(name = "commiter_field_mapping")
public class CommiterFieldMapping implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "source_field", nullable = false)
    private String sourceField;

    @NotNull
    @Column(name = "destination_field", nullable = false)
    private String destinationField;

    @ManyToOne(fetch = FetchType.EAGER)
    private CommiterConfig commiterConfig;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSourceField() {
        return sourceField;
    }

    /**
     *
     */
    public CommiterFieldMapping() {
    }

    /**
     * @param sourceField
     * @param destinationField
     * @param commiterConfig
     */
    public CommiterFieldMapping(String sourceField, String destinationField, CommiterConfig commiterConfig) {
        this.sourceField = sourceField;
        this.destinationField = destinationField;
        this.commiterConfig = commiterConfig;
    }

    /**
     * @param sourceField
     * @return
     */
    public CommiterFieldMapping sourceField(String sourceField) {
        this.sourceField = sourceField;
        return this;
    }

    public CommiterConfig getCommiterConfig() {
        return commiterConfig;
    }

    public void setCommiterConfig(CommiterConfig commiterConfig) {
        this.commiterConfig = commiterConfig;
    }

    public void setSourceField(String sourceField) {
        this.sourceField = sourceField;
    }

    public String getDestinationField() {
        return destinationField;
    }

    public CommiterFieldMapping destinationField(String destinationField) {
        this.destinationField = destinationField;
        return this;
    }

    public void setDestinationField(String destinationField) {
        this.destinationField = destinationField;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CommiterFieldMapping commiterFieldMapping = (CommiterFieldMapping) o;
        if (commiterFieldMapping.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), commiterFieldMapping.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "CommiterFieldMapping{" +
            "id=" + id +
            ", sourceField='" + sourceField + '\'' +
            ", destinationField='" + destinationField + '\'' +
            ", commiterConfig=" + commiterConfig +
            '}';
    }
}
