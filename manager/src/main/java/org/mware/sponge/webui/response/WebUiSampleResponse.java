package org.mware.sponge.webui.response;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mware.sponge.webui.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WebUiSampleResponse extends BaseWebUiResponse {
    private WebUiSample sample;

    public WebUiSampleResponse(WebUiSample sample) {
        this.sample = sample;

        response.data()
                .type(WebUiObjectType.SAMPLE)
                .id(sample.jsonId())
                .selfLink(sample.selfLink())
                .attributes(sample)
                .toOne("spider", sample.spider.selfLink(), sample.spider)
                .toOne("rendered-body", null, sample.renderedBody)
                .toMany("items", sample.itemsLink(), sample.items);


        response.selfLink(sample.selfLink());

        // include schemas
        sample.spider.project.schemas.stream().forEach(schema ->
                response.include()
                        .type(WebUiObjectType.SCHEMA)
                        .id(schema.jsonId())
                        .selfLink(schema.selfLink())
                        .attributes(schema)
                        .toOne("project", sample.spider.project.selfLink(), sample.spider.project)
                        .toMany("fields", schema.fieldsLink(), schema.fields)
        );

        // include schema fields
        sample.spider.project.schemas.stream().forEach(schema ->
                schema.fields.stream().forEach(f ->
                        response.include()
                                .type(WebUiObjectType.FIELD)
                                .id(f.jsonId())
                                .attributes(f)
                                .selfLink(f.selfLink())
                                .toOne("schema", f.schema.selfLink(), f.schema)
                )
        );

        // include annotations
        List<WebUiAnnotation> annotations = new ArrayList<>();
        sample.spider.project.schemas.forEach(schema -> {
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

        // include items
        sample.items.stream().forEach(item ->
                response.include()
                        .type(WebUiObjectType.ITEM)
                        .id(item.jsonId())
                        .selfLink(item.selfLink())
                        .attributes(item)
                        .toOne("sample", item.sample.selfLink(), item.sample)
                        .toOne("schema", item.schema.selfLink(), item.schema)
                        .toMany("annotations", item.annotationsLink(), item.annotations)
        );

        // include extractors
        sample.spider.project.extractors.forEach(extractor ->
                response.include()
                        .type(WebUiObjectType.EXTRACTOR)
                        .id(extractor.jsonId())
                        .selfLink(extractor.selfLink())
                        .attributes(extractor)
                        .toOne("project", extractor.project.selfLink(), extractor.project)
        );
    }

    public String getCreatedUrl() {
        return sample.selfLink();
    }
}
