package org.mware.sponge.webui.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.mware.sponge.domain.Project;
import org.mware.sponge.webui.jsonapi.JsonApiAware;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "webui_project")
public class WebUiProject implements Serializable, JsonApiAware {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(unique = true)
    public String name;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    public List<WebUiSpider> spiders = new ArrayList<>();

    public void addSpider(WebUiSpider spider) {
        this.spiders.add(spider);
        spider.project = this;
    }

    public void removeSpider(WebUiSpider spider) {
        spider.project = null;
        this.spiders.remove(spider);
    }

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    public List<WebUiSchema> schemas = new ArrayList<>();

    public void addSchema(WebUiSchema schema) {
        this.schemas.add(schema);
        schema.project = this;
    }

    public void removeSchema(WebUiSchema schema) {
        schema.project = null;
        this.schemas.remove(schema);
    }

    @OneToMany(mappedBy = "project", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonIgnore
    public List<WebUiExtractor> extractors = new ArrayList<>();

    public void addExtractor(WebUiExtractor extractor) {
        this.extractors.add(extractor);
        extractor.project = this;
    }

    public void removeExtractor(WebUiExtractor extractor) {
        extractor.project = null;
        this.extractors.remove(extractor);
    }

    @OneToOne
    @JsonIgnore
    public Project spongeProject;

    @Override
    public Object jsonId() {
        return id;
    }

    public String selfLink() {
        return "/sponge/webui/api/projects/"+ jsonId();
    }

    public String extractorsLink() {
        return selfLink()+"/extractors";
    }

    public String spidersLink() {
        return selfLink()+"/spiders";
    }

    public String schemasLink() {
        return selfLink()+"/schemas";
    }

    @Override
    public String jsonType() {
        return WebUiObjectType.PROJECT;
    }
}
