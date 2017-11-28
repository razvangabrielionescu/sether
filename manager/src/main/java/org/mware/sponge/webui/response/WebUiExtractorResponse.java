package org.mware.sponge.webui.response;

import org.mware.sponge.webui.domain.WebUiExtractor;
import org.mware.sponge.webui.domain.WebUiObjectType;

public class WebUiExtractorResponse extends BaseWebUiResponse {
    private WebUiExtractor extractor;

    public WebUiExtractorResponse(WebUiExtractor extractor) {
        this.extractor = extractor;

        response.data()
                .type(WebUiObjectType.EXTRACTOR)
                .id(extractor.jsonId())
                .selfLink(extractor.selfLink())
                .attributes(extractor)
                .toOne("project", extractor.project.selfLink(), extractor.project);
    }

    @Override
    public String getCreatedUrl() {
        return extractor.selfLink();
    }
}
