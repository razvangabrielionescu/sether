package org.mware.sponge.webui.response;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mware.sponge.webui.domain.WebUiObjectType;
import org.mware.sponge.webui.domain.WebUiProject;
import org.mware.sponge.webui.domain.WebUiSpider;

public class WebUiSamplesReponse extends BaseWebUiResponse {
    public WebUiSamplesReponse(WebUiSpider spider) {
        spider.samples.forEach(sample ->
            response.newArrayData()
                .type(WebUiObjectType.SAMPLE)
                .id(sample.jsonId())
                .selfLink(sample.selfLink())
                .attributes(sample)
                .toMany("items", sample.itemsLink(), sample.items)
                .toOne("spider", sample.spider.selfLink(), sample.spider)
                .toOne("rendered-body", null, sample.renderedBody)
        );

        if(!response.has("data"))
            response.put("data", new JSONArray());

        response.selfLink(spider.samplesLink());
    }

    @Override
    public String getCreatedUrl() {
        throw new UnsupportedOperationException("This is a GET response.");
    }
}
