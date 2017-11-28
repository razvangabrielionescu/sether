package org.mware.sponge.webui.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "webui_item")
public class WebUiItem extends BaseAnnotation implements Serializable {
    @Column
    public String name;

    @Column
    @JsonProperty("repeated-selector")
    public String repeatedSelector;

    @Column
    public String selector;

    @Column
    public int siblings = 0;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    public WebUiSample sample;

    @OneToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    public WebUiSchema schema;

    @OneToMany(mappedBy = "parent")
    @JsonIgnore
    public List<BaseAnnotation> annotations = new ArrayList<>();

    public void addAnnotation(BaseAnnotation base) {
        base.parent = this;
        this.annotations.add(base);
    }

    public void removeAnnotation(BaseAnnotation base) {
        base.parent = null;
        this.annotations.remove(base);
    }

    public String selfLink() {
        return "/sponge/webui/api/projects/"+sample.spider.project.jsonId() +"/spiders/"+sample.spider.jsonId()+"/samples/"+sample.jsonId()+"/items/"+jsonId();
    }

    public String annotationsLink() {
        return "/sponge/webui/api/projects/"+sample.spider.project.jsonId() +"/spiders/"+sample.spider.jsonId()+"/samples/"+sample.jsonId()+"/annotations?filter[parent]="+jsonId();
    }

    @Override
    public String jsonType() {
        return WebUiObjectType.ITEM;
    }
}