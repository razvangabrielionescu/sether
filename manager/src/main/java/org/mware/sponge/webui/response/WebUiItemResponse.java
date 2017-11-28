package org.mware.sponge.webui.response;

import org.mware.sponge.webui.domain.*;
import org.mware.sponge.webui.jsonapi.JsonApiResponseData;
import org.mware.sponge.webui.repository.WebUiAnnotationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WebUiItemResponse extends BaseWebUiResponse {
    private WebUiItem item;

    public WebUiItemResponse(WebUiProject project, WebUiItem item) {
        this.item = item;

        response.data()
                .type(WebUiObjectType.ITEM)
                .id(item.jsonId())
                .selfLink(item.selfLink())
                .attributes(item)
                .toMany("annotations", item.annotationsLink(), item.annotations)
                .toOne("schema", item.schema.selfLink(), item.schema)
                .toOne("sample", item.sample.selfLink(), item.sample);

        if(item.parent != null)
            response.data().toOne("parent", ((WebUiItem)item.parent).selfLink(), item.parent);
        else
            response.data().toOne("parent", null, null);

        List<WebUiAnnotation> annotations = new ArrayList<>();
        project.schemas.forEach(schema -> {
            schema.fields.forEach(field -> annotations.addAll(field.annotations));
        });

        annotations.stream().forEach(a ->
                response.include()
                .type(WebUiObjectType.ANNOTATION)
                .id(a.jsonId())
                .selfLink(a.selfLink())
                .attributes(a)
                .toMany("extractors", null, a.extractors)
                .toOne("field", a.field.selfLink(), a.field)
                .toOne("parent", ((WebUiItem)a.parent).selfLink(), a.parent)
        );

        item.sample.items.stream().forEach(i -> {
            if(!i.id.equals(item.id)) {
                JsonApiResponseData inc = response.include();
                inc
                        .type("items")
                        .id(i.jsonId())
                        .selfLink(i.selfLink())
                        .attributes(i)
                        .toOne("sample", i.sample.selfLink(), i.sample)
                        .toOne("schema", i.schema.selfLink(), i.schema)
                        .toMany("annotations", i.annotationsLink(), i.annotations);

                if (i.parent != null)
                    inc.toOne("parent", ((WebUiItem) item.parent).selfLink(), item.parent);
                else
                    inc.toOne("parent", null, null);
            }
        });

        WebUiSchema schema = item.schema;

        response.include()
                .type("schemas")
                .id(schema.jsonId())
                .selfLink(schema.selfLink())
                .attributes(schema)
                .toOne("project", schema.project.selfLink(), schema.project)
                .toMany("fields", schema.fieldsLink(), schema.fields);

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
        return item.selfLink();
    }
}
