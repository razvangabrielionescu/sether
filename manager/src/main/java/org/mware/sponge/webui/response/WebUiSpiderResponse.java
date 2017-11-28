package org.mware.sponge.webui.response;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mware.sponge.webui.domain.WebUiProject;
import org.mware.sponge.webui.domain.WebUiSpider;
import org.mware.sponge.webui.jsonapi.JsonApiResponse;

import java.util.stream.Collectors;

public class WebUiSpiderResponse extends BaseWebUiResponse {
    private WebUiSpider spider;

    public WebUiSpiderResponse(WebUiSpider spider) {
        this.spider = spider;

        response.data()
                .type("spiders")
                .id(spider.jsonId())
                .selfLink(spider.selfLink())
                .attributes(spider)
                .toOne("project", spider.project.selfLink(), spider.project)
                .toMany("samples", spider.samplesLink());

        response.selfLink(spider.selfLink());
    }

    public String getCreatedUrl() {
        return spider.selfLink();
    }
}
