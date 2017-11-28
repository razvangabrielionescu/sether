package org.mware.sponge.webui.response;

import org.mware.sponge.webui.domain.WebUiObjectType;
import org.mware.sponge.webui.domain.WebUiSchema;

public class WebUiSchemaResponse extends BaseWebUiResponse {
    public WebUiSchemaResponse(WebUiSchema schema) {
        response.data()
                .type(WebUiObjectType.SCHEMA)
                .id(schema.jsonId())
                .selfLink(schema.selfLink())
                .attributes(schema)
                .toMany("fields", schema.fieldsLink(), schema.fields)
                .toOne("project", schema.project.selfLink(), schema.project);

        schema.fields.stream().forEach(f ->
                response.include()
                        .type("fields")
                        .id(f.jsonId())
                        .attributes(f)
                        .selfLink(f.selfLink())
                        .toOne("schema", f.schema.selfLink(), f.schema)
        );
    }

    @Override
    public String getCreatedUrl() {
        throw new IllegalStateException();
    }
}
