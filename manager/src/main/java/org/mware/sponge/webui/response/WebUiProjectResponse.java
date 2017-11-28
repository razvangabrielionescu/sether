package org.mware.sponge.webui.response;

import org.mware.sponge.webui.domain.WebUiProject;

public class WebUiProjectResponse extends BaseWebUiResponse {
    private WebUiProject project;

    public WebUiProjectResponse(WebUiProject project) {
        this.project = project;

        response.data()
                .type("projects")
                .id(project.jsonId())
                .selfLink(project.selfLink())
                .attributes(project)
                .toMany("extractors", project.extractorsLink(), project.extractors)
                .toMany("schemas", project.schemasLink(), project.schemas)
                .toMany("spiders", project.spidersLink(), project.spiders);

        response.selfLink(project.selfLink());

        project.schemas.stream().forEach(schema ->
            response.include()
                .type("schemas")
                .id(schema.jsonId())
                .selfLink(schema.selfLink())
                .attributes(schema)
                .toOne("project", project.selfLink(), project)
                .toMany("fields", schema.fieldsLink(), schema.fields)
        );

        project.spiders.stream().forEach(spider ->
                response.include()
                        .type("spiders")
                        .id(spider.jsonId())
                        .selfLink(spider.selfLink())
                        .attributes(spider)
                        .toOne("project", project.selfLink(), project)
        );
    }

    public String getCreatedUrl() {
        return project.selfLink();
    }
}
