package org.mware.sponge.webui.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.mware.sponge.webui.jsonapi.JsonApiAware;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "webui_rendered_bodies")
public class WebUiRenderedBody implements Serializable, JsonApiAware {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    public Long id;

    @Column
    @Lob
    public String html;

    @OneToOne
    public WebUiSample sample;

    @Override
    public Object jsonId() {
        return id;
    }

    @Override
    public String jsonType() {
        return WebUiObjectType.RENDERED_BODY;
    }
}
