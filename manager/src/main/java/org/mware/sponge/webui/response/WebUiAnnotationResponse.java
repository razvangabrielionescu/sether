package org.mware.sponge.webui.response;

import org.mware.sponge.webui.domain.*;

public class WebUiAnnotationResponse extends BaseWebUiResponse {
    private WebUiProject project;
    private WebUiSample sample;
    private WebUiItem item;
    private WebUiAnnotation annotation;

    public WebUiAnnotationResponse(
            WebUiProject project,
            WebUiSample sample,
            WebUiSchema schema,
            WebUiItem item,
            WebUiAnnotation annotation)
    {
        this.project = project;
        this.sample = sample;
        this.item = item;
        this.annotation = annotation;

        response.data()
                .id(annotation.jsonId())
                .selfLink(annotation.selfLink())
                .type(WebUiObjectType.ANNOTATION)
                .attributes(annotation)
                .toMany("extractors", null, annotation.extractors)
                .toOne("field", annotation.field.selfLink(), annotation.field)
                .toOne("parent", ((WebUiItem)annotation.parent).selfLink(), annotation.parent);

        sample.spider.project.schemas.stream().forEach(s ->
                response.include()
                        .type("schemas")
                        .id(s.jsonId())
                        .selfLink(s.selfLink())
                        .attributes(s)
                        .toOne("project", sample.spider.project.selfLink(), sample.spider.project)
                        .toMany("fields", s.fieldsLink(), s.fields)
        );

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

        schema.fields.stream().forEach(f ->
                response.include()
                        .type("fields")
                        .id(f.jsonId())
                        .attributes(f)
                        .selfLink(f.selfLink())
                        .toOne("schema", f.schema.selfLink(), f.schema)
        );
    }

    public String getCreatedUrl() {
        return "/sponge/webui/api/projects/"+ project.id +"/spiders/"+sample.spider.id+"/samples/"+sample.id+"/annotations/"+annotation.id;
    }
}
