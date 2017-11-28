package org.mware.sponge.webui.response;

import org.mware.sponge.webui.domain.WebUiSample;

public class WebUiDeleteItemResponse extends BaseWebUiResponse {
    public WebUiDeleteItemResponse(WebUiSample sample) {
        response.addDeleteExtension();
        response.addUpdateExtension();

        sample.items.stream().forEach(i ->
                response.include()
                        .type("items")
                        .id(i.jsonId())
                        .selfLink(i.selfLink())
                        .attributes(i)
                        .toOne("sample", i.sample.selfLink(), i.sample)
                        .toOne("schema", i.schema.selfLink(), i.schema)
                        .toMany("annotations", i.annotationsLink(), i.annotations)
        );
    }

    @Override
    public String getCreatedUrl() {
        throw new IllegalStateException();
    }
}
