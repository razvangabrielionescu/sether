package org.mware.sponge.webui;

import org.json.JSONObject;
import org.mware.sponge.webui.response.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.standard.Media;
import java.net.URI;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/webui")
public class WebUiRestResource {
    private final Logger log = LoggerFactory.getLogger(WebUiRestResource.class);

    private WebUiProjectService projectService;

    public WebUiRestResource(WebUiProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/server_capabilities")
    public ServerCapabilities serverCapabilities() {
        return new ServerCapabilities();
    }

    @PostMapping(value = "/api/projects")
    public ResponseEntity<String> createProject(Principal principal, @RequestBody String json) {
        JSONObject jsonObj = new JSONObject(json);
        String projectName = jsonObj.getJSONObject("data").getJSONObject("attributes").getString("name");
        BaseWebUiResponse response = projectService.createProject(principal.getName(), projectName);
        return ResponseEntity.created(URI.create(response.getCreatedUrl())).body(response.getJson());
    }

    @GetMapping(value = "/api/projects/{projectId:.+}")
    public ResponseEntity<String> getProject(@PathVariable String projectId) {
        BaseWebUiResponse response = projectService.getProject(projectId);
        return ResponseEntity.ok().body(response.getJson());
    }

    /**
     * @param projectName
     * @return
     */
    @GetMapping(value = "/api/projects/unique/{projectName}")
    public ResponseEntity<String> projectIsUnique(@PathVariable String projectName) {
        boolean projectIsUnique = projectService.projectNameIsUnique(projectName);
        return ResponseEntity.ok().body(String.valueOf(projectIsUnique));
    }

    @PostMapping(value = "/api/projects/{projectId}/spiders")
    public ResponseEntity<String> createSpider(Principal principal, @RequestBody String json, @PathVariable String projectId) {
        JSONObject jsonObj = new JSONObject(json);
        BaseWebUiResponse response = projectService.createSpider(principal.getName(), projectId, jsonObj);
        return ResponseEntity.created(URI.create(response.getCreatedUrl())).body(response.getJson());
    }

    @GetMapping(value = "/api/projects/{projectId}/spiders/{spiderName:.+}")
    public ResponseEntity<String> getSpider(@PathVariable String projectId, @PathVariable String spiderName) {
        BaseWebUiResponse response = projectService.getSpider(projectId, spiderName);
        return ResponseEntity.ok().body(response.getJson());
    }

    @PatchMapping(value = "/api/projects/{projectId}/spiders/{spiderName:.+}")
    public ResponseEntity<String> updateSpider(Principal principal, @RequestBody String json, @PathVariable String projectId, @PathVariable String spiderName) {
        log.debug("Updating spider: "+spiderName+" with json: "+json);
        JSONObject jsonObj = new JSONObject(json);
        BaseWebUiResponse response = projectService.updateSpider(principal.getName(), projectId, spiderName, jsonObj);
        return ResponseEntity.ok().body(response.getJson());
    }

    @DeleteMapping(value = "/api/projects/{projectId}/spiders/{spiderName:.+}")
    public ResponseEntity<String> deleteSpider(Principal principal, @PathVariable String projectId, @PathVariable String spiderName) {
        BaseWebUiResponse response = projectService.deleteSpider(principal.getName(), projectId, spiderName);
        return ResponseEntity.ok().body(response.getJson());
    }

    @GetMapping(value = "/api/projects/{projectId}/spiders/{spiderId}/samples")
    public ResponseEntity<String> getSamples(@PathVariable String projectId, @PathVariable String spiderId) {
        BaseWebUiResponse response = projectService.getSamples(projectId, spiderId);
        return ResponseEntity.ok().body(response.getJson());
    }

    @PostMapping(value = "/api/projects/{projectId}/spiders/{spiderId}/samples")
    public ResponseEntity<String> createSample(@RequestBody String json, @PathVariable String projectId, @PathVariable String spiderId) {
        JSONObject jsonObj = new JSONObject(json);
        BaseWebUiResponse response = projectService.createSample(projectId, spiderId, jsonObj);
        return ResponseEntity.created(URI.create(response.getCreatedUrl())).body(response.getJson());
    }

    @GetMapping(value = "/api/projects/{projectId}/spiders/{spiderId}/samples/{sampleId}")
    public ResponseEntity<String> getSample(@PathVariable String projectId, @PathVariable String spiderId, @PathVariable String sampleId) {
        WebUiSampleResponse response = projectService.getSample(sampleId);
        return ResponseEntity.ok().body(response.getJson());
    }

    @PatchMapping(value = "/api/projects/{projectId}/spiders/{spiderId}/samples/{sampleId}")
    public ResponseEntity<String> updateSample(Principal principal, @RequestBody String json, @PathVariable String projectId, @PathVariable String spiderId, @PathVariable String sampleId) {
        JSONObject jsonObj = new JSONObject(json);
        BaseWebUiResponse response = projectService.updateSample(projectId, sampleId, jsonObj);
        return ResponseEntity.ok().body(response.getJson());
    }

    @DeleteMapping(value = "/api/projects/{projectId}/spiders/{spiderId}/samples/{sampleId}")
    public ResponseEntity<String> deleteSample(@PathVariable String projectId, @PathVariable String spiderId, @PathVariable String sampleId) {
        BaseWebUiResponse response = projectService.deleteSample(projectId, sampleId);
        return ResponseEntity.ok().body(response.getJson());
    }

    @PostMapping(value = "/api/projects/{projectId}/spiders/{spiderId}/samples/{sampleId}/annotations")
    public ResponseEntity<String> createAnnotation(Principal principal, @RequestBody String json, @PathVariable String projectId, @PathVariable String spiderId, @PathVariable String sampleId) {
        JSONObject jsonObj = new JSONObject(json);
        BaseWebUiResponse response = projectService.createAnnotation(principal.getName(), projectId, spiderId, sampleId, jsonObj);
        return ResponseEntity.created(URI.create(response.getCreatedUrl())).body(response.getJson());
    }

    @PatchMapping(value = "/api/projects/{projectId}/spiders/{spiderId}/samples/{sampleId}/annotations/{annotationId}")
    public ResponseEntity<String> updateAnnotation(Principal principal, @RequestBody String json, @PathVariable String projectId, @PathVariable String spiderId, @PathVariable String sampleId, @PathVariable String annotationId) {
        JSONObject jsonObj = new JSONObject(json);
        BaseWebUiResponse response = projectService.updateAnnotation(principal.getName(), projectId, spiderId, sampleId, annotationId, jsonObj);
        return ResponseEntity.ok().body(response.getJson());
    }

    @PostMapping(value = "/api/projects/{projectId}/spiders/{spiderId}/samples/{sampleId}/items")
    public ResponseEntity<String> createItem(Principal principal, @RequestBody String json, @PathVariable String projectId, @PathVariable String spiderId, @PathVariable String sampleId) {
        JSONObject jsonObj = new JSONObject(json);
        BaseWebUiResponse response = projectService.createItem(principal.getName(), projectId, spiderId, sampleId, jsonObj);
        return ResponseEntity.created(URI.create(response.getCreatedUrl())).body(response.getJson());
    }

    @PatchMapping(value = "/api/projects/{projectId}/spiders/{spiderId}/samples/{sampleId}/items/{itemId}")
    public ResponseEntity<String> updateItem(Principal principal, @RequestBody String json, @PathVariable String projectId, @PathVariable String spiderId, @PathVariable String sampleId, @PathVariable String itemId) {
        JSONObject jsonObj = new JSONObject(json);
        BaseWebUiResponse response = projectService.updateItem(principal.getName(), projectId, spiderId, sampleId, itemId, jsonObj);
        return ResponseEntity.ok().body(response.getJson());
    }

    @DeleteMapping(value = "/api/projects/{projectId}/spiders/{spiderId}/samples/{sampleId}/items/{itemId}")
    public ResponseEntity<String> deleteItem(Principal principal, @RequestBody String json, @PathVariable String projectId, @PathVariable String spiderId, @PathVariable String sampleId, @PathVariable String itemId) {
        JSONObject jsonObj = new JSONObject(json);
        BaseWebUiResponse response = projectService.deleteItem(principal.getName(), projectId, spiderId, sampleId, itemId, jsonObj);
        return ResponseEntity.ok().body(response.getJson());
    }

    @DeleteMapping(value = "/api/projects/{projectId}/spiders/{spiderId}/samples/{sampleId}/annotations/{annotationId}")
    public ResponseEntity<String> deleteAnnotation(Principal principal, @PathVariable String projectId, @PathVariable String spiderId, @PathVariable String sampleId, @PathVariable String annotationId) {
        BaseWebUiResponse response = projectService.deleteAnnotation(principal.getName(), projectId, spiderId, sampleId, annotationId);
        return ResponseEntity.ok().body(response.getJson());
    }

    @PostMapping(value = "/api/projects/{projectId}/extractors")
    public ResponseEntity<String> createExtractor(Principal principal, @RequestBody String json, @PathVariable String projectId) {
        JSONObject jsonObj = new JSONObject(json);
        BaseWebUiResponse response = projectService.createExtractor(principal.getName(), projectId, jsonObj);
        return ResponseEntity.created(URI.create(response.getCreatedUrl())).body(response.getJson());
    }

    @GetMapping(value = "/api/projects/{projectId}/extractors")
    public ResponseEntity<String> getExtractors(@PathVariable String projectId) {
        BaseWebUiResponse response = projectService.getExtractors(projectId);
        return ResponseEntity.ok().body(response.getJson());
    }

    @PatchMapping(value = "/api/projects/{projectId}/extractors/{extractorId}")
    public ResponseEntity<String> updateExtractor(Principal principal, @RequestBody String json, @PathVariable String projectId, @PathVariable String extractorId) {
        JSONObject jsonObj = new JSONObject(json);
        BaseWebUiResponse response = projectService.updateExtractor(principal.getName(), projectId, extractorId, jsonObj);
        return ResponseEntity.ok().body(response.getJson());
    }

    @GetMapping(value = "/api/projects/{projectId}/extractors/{extractorId}")
    public ResponseEntity<String> getExtractor(@PathVariable String projectId, @PathVariable String extractorId) {
        BaseWebUiResponse response = projectService.getExtractor(projectId);
        return ResponseEntity.ok().body(response.getJson());
    }

    @GetMapping(value = "/api/projects/{projectId}/schemas/{schemaId}")
    public ResponseEntity<String> getSchema(@PathVariable String projectId, @PathVariable String schemaId) {
        BaseWebUiResponse response = projectService.getSchema(schemaId);
        return ResponseEntity.ok().body(response.getJson());
    }

    @PatchMapping(value = "/api/projects/{projectId}/schemas/{schemaId}")
    public ResponseEntity<String> updateSchema(Principal principal, @RequestBody String json, @PathVariable String projectId, @PathVariable String schemaId) {
        JSONObject jsonObj = new JSONObject(json);
        BaseWebUiResponse response = projectService.updateSchema(principal.getName(), schemaId, jsonObj);
        return ResponseEntity.ok().body(response.getJson());
    }

    @DeleteMapping(value = "/api/projects/{projectId}/schemas/{schemaId}/fields/{fieldId}")
    public ResponseEntity<String> deleteField(Principal principal, @PathVariable String projectId, @PathVariable String schemaId, @PathVariable String fieldId) {
        projectService.deleteField(principal.getName(), projectId, fieldId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/api/projects/{projectId}/schemas/{schemaId}/fields/{fieldId}")
    public ResponseEntity<String> updateField(Principal principal, @RequestBody String json, @PathVariable String projectId, @PathVariable String schemaId, @PathVariable String fieldId) {
        JSONObject jsonObj = new JSONObject(json);
        BaseWebUiResponse response = projectService.updateField(principal.getName(), projectId, fieldId, jsonObj);
        return ResponseEntity.ok().body(response.getJson());
    }

    @PostMapping(value = "/api/projects/{projectId}/schemas/{schemaId}/fields")
    public ResponseEntity<String> createField(Principal principal, @RequestBody String json, @PathVariable String projectId, @PathVariable String schemaId) {
        JSONObject jsonObj = new JSONObject(json);
        BaseWebUiResponse response = projectService.createField(principal.getName(), schemaId, jsonObj);
        return ResponseEntity.created(URI.create(response.getCreatedUrl())).body(response.getJson());
    }

    class ServerCapabilities {
        public Map<String, Boolean> capabilities = new HashMap<>();
        public String username = "";
        public Map<String, String> custom = new HashMap<>();

        public ServerCapabilities() {
            capabilities.put("create_projects", true);
            capabilities.put("delete_projects", true);
            capabilities.put("deploy_projects", false);
            capabilities.put("rename_projects", true);
            capabilities.put("rename_spiders", true);
            capabilities.put("rename_templates", true);
            capabilities.put("version_control", false);
        }
    }
}
