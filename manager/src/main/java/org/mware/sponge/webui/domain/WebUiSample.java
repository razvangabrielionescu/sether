package org.mware.sponge.webui.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.mware.sponge.webui.jsonapi.JsonApiAware;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "webui_sample")
public class WebUiSample implements Serializable, JsonApiAware {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    public Long id;

    @Column
    public String name;

    @Column
    public String url;

    @Column
    @JsonIgnore
    public String body;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    public WebUiSpider spider;

    @OneToMany(mappedBy = "sample", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    public List<WebUiItem> items = new ArrayList<>();

    public void addItem(WebUiItem item) {
        item.sample = this;
        this.items.add(item);
    }

    public void removeItem(WebUiItem item) {
        item.sample = null;
        this.items.remove(item);
    }

    @OneToOne(mappedBy = "sample", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    public WebUiRenderedBody renderedBody;

    @OneToOne(mappedBy = "sample", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    public WebUiOriginalBody originalBody;

    @Override
    public Object jsonId() {
        return id;
    }

    public String selfLink() {
        return "/sponge/webui/api/projects/"+spider.project.jsonId() +"/spiders/"+spider.jsonId()+"/samples/"+jsonId();
    }

    public String itemsLink() {
        return selfLink() + "/items?filter[parent]=null";
    }

    @Override
    public String jsonType() {
        return WebUiObjectType.SAMPLE;
    }
}
