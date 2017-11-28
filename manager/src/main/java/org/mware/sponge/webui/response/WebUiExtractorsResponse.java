package org.mware.sponge.webui.response;

import org.mware.sponge.webui.domain.WebUiObjectType;
import org.mware.sponge.webui.domain.WebUiProject;

public class WebUiExtractorsResponse extends BaseWebUiResponse {
    public WebUiExtractorsResponse(WebUiProject project) {
        project.extractors.forEach(extractor ->
            response.newArrayData()
                .type(WebUiObjectType.EXTRACTOR)
                .id(extractor.jsonId())
                .selfLink(extractor.selfLink())
                .attributes(extractor)
                .toOne("project", extractor.project.selfLink(), extractor.project)
        );

        response.selfLink(project.extractorsLink());
    }

    @Override
    public String getCreatedUrl() {
        return null;
    }
}
