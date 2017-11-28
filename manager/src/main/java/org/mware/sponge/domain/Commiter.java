package org.mware.sponge.domain;


import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Commiter.
 */
@Entity
@Table(name = "commiter")
public class Commiter implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @NotNull
    @Column(name = "clazz", nullable = false)
    private String clazz;

    @Column(name = "description")
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Commiter name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClazz() {
        return clazz;
    }

    public Commiter clazz(String clazz) {
        this.clazz = clazz;
        return this;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getDescription() {
        return description;
    }

    public Commiter description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Commiter commiter = (Commiter) o;
        if (commiter.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), commiter.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Commiter{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", clazz='" + getClazz() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
