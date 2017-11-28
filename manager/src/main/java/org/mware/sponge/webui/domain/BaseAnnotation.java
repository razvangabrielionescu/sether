package org.mware.sponge.webui.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.mware.sponge.webui.jsonapi.JsonApiAware;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class BaseAnnotation implements JsonApiAware {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @JsonIgnore
    public Long id;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JsonIgnore
    public BaseAnnotation parent;

    @Override
    public Object jsonId() {
        return id;
    }
}
