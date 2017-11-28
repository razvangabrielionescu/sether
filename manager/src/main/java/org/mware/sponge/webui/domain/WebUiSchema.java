package org.mware.sponge.webui.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.mware.sponge.webui.jsonapi.JsonApiAware;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "webui_schema")
public class WebUiSchema implements Serializable, JsonApiAware {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    public Long id;

    @Column
    public String name;

    @Column
    @JsonProperty("default")
    public boolean defaultSchema;

    @OneToMany(mappedBy = "schema", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    public List<WebUiField> fields = new ArrayList<>();

    public void addField(WebUiField field) {
        this.fields.add(field);
        field.schema = this;
    }

    public void removeField(WebUiField field) {
        field.schema = null;
        this.fields.remove(field);
    }

    @OneToOne(mappedBy = "schema")
    @JsonIgnore
    public WebUiItem item;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    public WebUiProject project;

    @Override
    public Object jsonId() {
        return id;
    }

    public String selfLink() {
        return "/sponge/webui/api/projects/"+project.jsonId() +"/schemas/"+jsonId();
    }

    public String fieldsLink() {
        return selfLink()+"/fields";
    }

    @Override
    public String jsonType() {
        return WebUiObjectType.SCHEMA;
    }
}
