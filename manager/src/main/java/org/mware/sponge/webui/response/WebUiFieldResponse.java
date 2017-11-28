package org.mware.sponge.webui.response;

import org.mware.sponge.webui.domain.WebUiField;
import org.mware.sponge.webui.domain.WebUiObjectType;

public class WebUiFieldResponse extends BaseWebUiResponse {
    private WebUiField field;

    public WebUiFieldResponse(WebUiField field) {
        this.field = field;

        response.data()
                .type(WebUiObjectType.FIELD)
                .id(field.id)
                .attributes(field)
                .selfLink(field.selfLink())
                .toOne("schema", field.schema.selfLink(), field.schema);
    }

    @Override
    public String getCreatedUrl() {
        return field.selfLink();
    }
}
