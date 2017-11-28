package org.mware.sponge.webui.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.mware.sponge.webui.jsonapi.JsonApiAware;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "webui_field")
public class WebUiField implements Serializable, JsonApiAware {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    public Long id;

    @Column
    public String name;

    @Column
    public String type;

    @Column
    public boolean required;

    @Column
    public boolean vary;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    public WebUiSchema schema;

    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL)
    @JsonIgnore
    public List<WebUiAnnotation> annotations = new ArrayList<>();

    public void addAnnotation(WebUiAnnotation annotation) {
        annotation.field = this;
        this.annotations.add(annotation);
    }

    public void removeAnnotation(WebUiAnnotation annotation) {
        annotation.field = null;
        this.annotations.remove(annotation);
    }

    @Override
    public Object jsonId() {
        return id;
    }

    @Override
    public String jsonType() {
        return WebUiObjectType.FIELD;
    }

    public String selfLink() {
        return "/sponge/webui/api/projects/"+schema.project.jsonId() +"/schemas/"+schema.jsonId()+"/fields/"+jsonId();
    }
}
