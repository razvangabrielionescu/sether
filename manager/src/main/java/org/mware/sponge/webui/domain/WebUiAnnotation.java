package org.mware.sponge.webui.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "webui_annotation")
public class WebUiAnnotation extends BaseAnnotation implements Serializable {
    @Column
    public String attribute;

    @Column
    @JsonProperty("post-text")
    public String postText;

    @Column
    @JsonProperty("pre-text")
    public String preText;

    @Column
    @JsonProperty("selection-mode")
    public String selectionMode;

    @Column
    public String selector;

    @Column
    @JsonProperty("text-content")
    public String textContent;

    @Column(length = 512)
    public String xpath;

    @Column
    public boolean repeated;

    @Column
    public boolean required;

    @Column
    @ElementCollection
    @CollectionTable(name ="webui_ann_accept")
    @JsonProperty("accept-selectors")
    public List<String> acceptSelectors = new ArrayList<>();

    @Column
    @ElementCollection
    @CollectionTable(name ="webui_ann_reject")
    @JsonProperty("reject-selectors")
    public List<String> rejectSelectors = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JsonIgnore
    public WebUiField field;

    @ManyToMany(mappedBy = "annotations", cascade = CascadeType.ALL)
    @JsonIgnore
    public List<WebUiExtractor> extractors = new ArrayList<>();

    @Override
    public String jsonType() {
        return WebUiObjectType.ANNOTATION;
    }

    public String selfLink() {
        return "/sponge/webui/api/projects/"+((WebUiItem)parent).sample.spider.project.jsonId()
                +"/spiders/"+((WebUiItem)parent).sample.spider.jsonId()+"/samples/"+((WebUiItem)parent).sample.jsonId()
                +"/annotations/"+jsonId();
    }
}