package org.mware.sponge.webui.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.BatchSize;
import org.mware.sponge.webui.jsonapi.JsonApiAware;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "webui_extractor")
public class WebUiExtractor implements Serializable, JsonApiAware {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    public Long id;

    @Column
    public String type;

    @Column
    public String value;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    public WebUiProject project;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "webui_extractor_annotation",
            joinColumns = {@JoinColumn(name = "extractor_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "annotation_id", referencedColumnName = "id")})
    @BatchSize(size = 20)
    @JsonIgnore
    public List<WebUiAnnotation> annotations = new ArrayList<>();

    @Override
    public Object jsonId() {
        return id;
    }

    @Override
    public String jsonType() {
        return WebUiObjectType.EXTRACTOR;
    }

    public String selfLink() {
        return "/sponge/webui/api/projects/"+ jsonId()+"/extractors/"+jsonId();
    }
}
