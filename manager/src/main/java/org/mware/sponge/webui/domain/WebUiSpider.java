package org.mware.sponge.webui.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.mware.sponge.webui.jsonapi.JsonApiAware;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "webui_spider",
        uniqueConstraints = @UniqueConstraint(columnNames = { "name", "project_id" })
)
public class WebUiSpider implements Serializable, JsonApiAware {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    public Long id;

    @Column(name = "name")
    @JsonIgnore
    public String name;

    @JsonProperty("allowed-domains")
    @ElementCollection
    @CollectionTable(name ="webui_spider_domains")
    public List<String> allowedDomains = new ArrayList<>();

    @Column
    @ElementCollection
    @CollectionTable(name ="webui_spider_exclude")
    @JsonProperty("exclude-patterns")
    public List<String> excludePatterns = new ArrayList<>();

    @Column
    @ElementCollection
    @CollectionTable(name ="webui_spider_follow")
    @JsonProperty("follow-patterns")
    public List<String> followPatterns = new ArrayList<>();

    @Column
    @ElementCollection
    @CollectionTable(name ="webui_spider_jsenable")
    @JsonProperty("js-disable-patterns")
    public List<String> jsDisablePatterns = new ArrayList<>();

    @Column
    @ElementCollection
    @CollectionTable(name ="webui_spider_jsdisable")
    @JsonProperty("js-enable-patterns")
    public List<String> jsEnablePatterns = new ArrayList<>();

    @OneToMany(mappedBy = "spider", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonProperty("start-urls")
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public List<WebUiStartUrl> startUrls = new ArrayList<>();

    public void addStartUrl(WebUiStartUrl startUrl) {
        startUrl.spider = this;
        this.startUrls.add(startUrl);
    }

    public void removeStartUrl(WebUiStartUrl startUrl) {
        startUrl.spider = null;
        this.startUrls.remove(startUrl);
    }

    @Column
    @JsonProperty("js-enabled")
    public boolean jsEnabled = false;

    @Column
    @JsonProperty("links-to-follow")
    public String linksToFollow = "all";

    @Column
    @JsonProperty("show-links")
    public boolean showLinks;

    @Column
    @JsonProperty("auth-method")
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public String authMethod;

    @Column
    @JsonProperty("login-url")
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public String loginUrl;

    @Column
    @JsonProperty("login-user")
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public String loginUser;

    @Column
    @JsonProperty("login-user-field")
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public String loginUserField;

    @Column
    @JsonProperty("login-password")
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public String loginPassword;

    @Column
    @JsonProperty("login-password-field")
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public String loginPasswordField;

    @Column
    @JsonProperty("perform-login")
    public boolean performLogin;

    @Column
    @JsonProperty("respect-no-follow")
    public boolean respectNoFollow;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id")
    @JsonIgnore
    public WebUiProject project;

    @JsonIgnore
    @OneToMany(mappedBy = "spider", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<WebUiSample> samples = new ArrayList<>();

    public void addSample(WebUiSample sample) {
        sample.spider = this;
        this.samples.add(sample);
    }

    public void removeSample(WebUiSample sample) {
        sample.spider = null;
        this.samples.remove(sample);
    }

    @Override
    public Object jsonId() {
        return name;
    }

    public String selfLink() {
        return "/sponge/webui/api/projects/"+project.jsonId() +"/spiders/"+jsonId();
    }

    public String samplesLink() {
        return selfLink() + "/samples";
    }

    @Override
    public String jsonType() {
        return WebUiObjectType.SPIDER;
    }
}
