package org.mware.sponge.webui.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "webui_starturl")
public class WebUiStartUrl implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    public Long id;

    @Column
    public String url;

    @Column
    public String type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    public WebUiSpider spider;

    public WebUiStartUrl() {}

    public WebUiStartUrl(String url, String type) {
        this.url = url;
        this.type = type;
    }
}
