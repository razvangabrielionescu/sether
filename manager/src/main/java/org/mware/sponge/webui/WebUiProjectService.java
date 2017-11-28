package org.mware.sponge.webui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mware.sponge.ProjectManager;
import org.mware.sponge.webui.domain.*;
import org.mware.sponge.webui.jsonapi.JsonApiAware;
import org.mware.sponge.webui.repository.*;
import org.mware.sponge.webui.response.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Transactional
public class WebUiProjectService {
    private final Logger log = LoggerFactory.getLogger(WebUiProjectService.class);

    @Autowired
    private WebUiProjectRepository projectRepository;
    @Autowired
    private WebUiSpiderRepository spiderRepository;
    @Autowired
    private WebUiSampleRepository sampleRepository;
    @Autowired
    private WebUiSchemaRepository schemaRepository;
    @Autowired
    private WebUiItemRepository itemRepository;
    @Autowired
    private WebUiAnnotationRepository annotationRepository;
    @Autowired
    private WebUiFieldRepository fieldRepository;
    @Autowired
    private WebUiExtractorRepository extractorRepository;
    @Autowired
    private WebUiStartUrlRepository startUrlRepository;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private ProjectManager projectManager;

    private ObjectMapper objectMapper = new ObjectMapper();

    public WebUiProjectService() {
    }

    public WebUiProjectResponse createProject(String userName, String projectName) {
        WebUiProject project = new WebUiProject();
        project.name = projectName;
        project = projectRepository.save(project);

        projectManager.syncWebUiproject(userName, project);

        return new WebUiProjectResponse(project);
    }

    public WebUiProjectResponse getProject(String projectId) {
        Preconditions.checkNotNull(projectId);

        WebUiProject project = projectRepository.findOne(new Long(projectId));
        return new WebUiProjectResponse(project);
    }

    /**
     * @param projectName
     * @return
     */
    public boolean projectNameIsUnique(String projectName) {
        Preconditions.checkNotNull(projectName);

        Optional<WebUiProject> project = projectRepository.findOneByName(projectName);

        return !project.isPresent();
    }

    public WebUiSpiderResponse getSpider(String projectId, String spiderName) {
        Preconditions.checkNotNull(spiderName);
        WebUiProject project = projectRepository.findOne(new Long(projectId));
        Optional<WebUiSpider> spider = spiderRepository.findOneByNameAndProject(spiderName, project);
        Preconditions.checkArgument(spider.isPresent());
        return new WebUiSpiderResponse(spider.get());
    }

    public WebUiSampleResponse getSample(String sampleId) {
        Preconditions.checkNotNull(sampleId);

        WebUiSample sample = sampleRepository.findOne(new Long(sampleId));
        return new WebUiSampleResponse(sample);
    }

    public WebUiSpiderResponse createSpider(String userName, String projectId, JSONObject jsonObj) {
        Preconditions.checkNotNull(projectId);
        Preconditions.checkArgument(jsonObj.has("data"));
        Preconditions.checkArgument("spiders".equals(jsonObj.getJSONObject("data").getString("type")));
        Preconditions.checkArgument(jsonObj.getJSONObject("data").has("attributes"));

        WebUiProject project = projectRepository.findOne(new Long(projectId));

        JSONObject data = jsonObj.getJSONObject("data");
        JSONObject attributes = data.getJSONObject("attributes");

        WebUiSpider spider = new WebUiSpider();
        spider.name = data.getString("id");
        spider.excludePatterns.addAll(jsonArrayToList(attributes.getJSONArray("exclude-patterns")));
        spider.followPatterns.addAll(jsonArrayToList(attributes.getJSONArray("follow-patterns")));
        spider.jsDisablePatterns.addAll(jsonArrayToList(attributes.getJSONArray("js-disable-patterns")));
        spider.jsEnablePatterns.addAll(jsonArrayToList(attributes.getJSONArray("js-enable-patterns")));
        spider.jsEnabled = attributes.getBoolean("js-enabled");
        spider.linksToFollow = attributes.getString("links-to-follow");
        spider.loginUrl = attributes.optString("login-url");
        spider.loginUser = attributes.optString("login-user");
        spider.loginPassword = attributes.optString("login-password");
        spider.performLogin = attributes.getBoolean("perform-login");
        spider.respectNoFollow = attributes.optBoolean("respect-no-follow", false);
        spider.project = project;

        List startUrls = jsonArrayToList(attributes.getJSONArray("start-urls"));
        for(Object startUrl : startUrls) {
            String url = ((JSONObject)startUrl).getString("url");
            String type = ((JSONObject)startUrl).getString("type");
            spider.addStartUrl(new WebUiStartUrl(url, type));
        }

        spider = spiderRepository.save(spider);

        project.spiders.add(spider);
        project = projectRepository.save(project);

        projectManager.syncWebUiproject(userName, project);

        return new WebUiSpiderResponse(spider);
    }

    public WebUiSpiderResponse updateSpider(String userName, String projectId, String spiderName, JSONObject jsonObj) {
        Preconditions.checkNotNull(projectId);
        Preconditions.checkNotNull(spiderName);
        Preconditions.checkArgument(jsonObj.has("data"));
        Preconditions.checkArgument("spiders".equals(jsonObj.getJSONObject("data").getString("type")));
        Preconditions.checkArgument(jsonObj.getJSONObject("data").has("attributes"));

        WebUiProject project = projectRepository.findOne(new Long(projectId));
        if (project == null) {
            throw new IllegalArgumentException("Could not find project with id=" + projectId);
        }

        Optional<WebUiSpider> optSpider = spiderRepository.findOneByNameAndProject(spiderName, project);
        if (!optSpider.isPresent()) {
            throw new IllegalArgumentException("Could not find spider with name=" + spiderName + " and project=" + projectId);
        }

        JSONObject data = jsonObj.getJSONObject("data");
        JSONObject attributes = data.getJSONObject("attributes");

        WebUiSpider spider = optSpider.get();
        spider.excludePatterns.clear();
        spider.followPatterns.clear();
        spider.jsDisablePatterns.clear();
        spider.jsEnablePatterns.clear();
        spider.startUrls.clear();
        spider = spiderRepository.saveAndFlush(spider);

        spider.excludePatterns.addAll(jsonArrayToList(attributes.getJSONArray("exclude-patterns")));
        spider.followPatterns.addAll(jsonArrayToList(attributes.getJSONArray("follow-patterns")));
        spider.jsDisablePatterns.addAll(jsonArrayToList(attributes.getJSONArray("js-disable-patterns")));
        spider.jsEnablePatterns.addAll(jsonArrayToList(attributes.getJSONArray("js-enable-patterns")));
        spider.jsEnabled = attributes.getBoolean("js-enabled");
        spider.linksToFollow = attributes.getString("links-to-follow");
        spider.authMethod = attributes.optString("auth-method");
        spider.loginUrl = attributes.optString("login-url");
        spider.loginUser = attributes.optString("login-user");
        spider.loginUserField = attributes.optString("login-user-field");
        spider.loginPassword = attributes.optString("login-password");
        spider.loginPasswordField = attributes.optString("login-password-field");
        spider.performLogin = attributes.getBoolean("perform-login");
        spider.respectNoFollow = attributes.optBoolean("respect-no-follow", false);
        spider.showLinks = attributes.optBoolean("show-links", false);

        List startUrls = jsonArrayToList(attributes.getJSONArray("start-urls"));

        for(Object startUrl : startUrls) {
            String url = ((JSONObject)startUrl).getString("url");
            String type = ((JSONObject)startUrl).getString("type");
            WebUiStartUrl su = new WebUiStartUrl(url, type);
            su.spider = spider;
            su = startUrlRepository.save(su);
            spider.startUrls.add(su);
        }

        spider = spiderRepository.saveAndFlush(spider);

        projectManager.syncWebUiproject(userName, project);

        return new WebUiSpiderResponse(spider);
    }

    public BaseWebUiResponse deleteSpider(String userName, String projectId, String spiderName) {
        Preconditions.checkNotNull(projectId);
        Preconditions.checkNotNull(spiderName);

        WebUiDeleteSpiderResponse resp = new WebUiDeleteSpiderResponse();

        WebUiProject project = projectRepository.findOne(new Long(projectId));
        if(project == null)
            throw new IllegalArgumentException("Could not find project with id="+projectId);

        Optional<WebUiSpider> optSpider = spiderRepository.findOneByNameAndProject(spiderName, project);
        if(!optSpider.isPresent())
            throw new IllegalArgumentException("Could not find spider with name="+spiderName+" and project="+projectId);

        WebUiSpider spider = optSpider.get();

        spider.samples.forEach(sample -> {
            sample.items.forEach(item -> {
                item.annotations.forEach(annotation -> {
                    resp.response.addMetaDelete(WebUiObjectType.ANNOTATION, annotation.jsonId().toString());
                });
                resp.response.addMetaDelete(WebUiObjectType.ITEM, item.jsonId().toString());
                Optional<WebUiSchema> schema = schemaRepository.findOneByNameAndProject(sample.name, project);
                if(schema.isPresent()) {
                    schemaRepository.delete(schema.get());
                    resp.response.addMetaDelete(WebUiObjectType.SCHEMA, schema.get().jsonId().toString());
                }
            });
            resp.response.addMetaDelete(WebUiObjectType.SAMPLE, sample.jsonId().toString());
        });

        spiderRepository.delete(spider);

        projectManager.syncWebUiproject(userName, project);

        return resp;
    }

    public WebUiSamplesReponse getSamples(String projectId, String spiderName) {
        Preconditions.checkNotNull(projectId);
        Preconditions.checkNotNull(spiderName);

        WebUiProject project = projectRepository.findOne(new Long(projectId));
        Optional<WebUiSpider> spider = spiderRepository.findOneByNameAndProject(spiderName, project);
        return new WebUiSamplesReponse(spider.get());
    }

    public WebUiSampleResponse createSample(String projectId, String spiderName, JSONObject jsonObj) {
        Preconditions.checkNotNull(projectId);
        Preconditions.checkNotNull(spiderName);
        Preconditions.checkArgument(jsonObj.has("data"));
        Preconditions.checkArgument(jsonObj.getJSONObject("data").has("attributes"));
        JSONObject data = jsonObj.getJSONObject("data");
        JSONObject attributes = data.getJSONObject("attributes");

        WebUiProject project = projectRepository.findOne(new Long(projectId));
        Optional<WebUiSpider> spider = spiderRepository.findOneByNameAndProject(spiderName, project);

        WebUiSample sample = new WebUiSample();
        sample.name = attributes.getString("name");
        sample.body = attributes.getString("body");
        sample.url = attributes.getString("url");
        sample.spider = spider.get(); // associate sample with spider
        sample = sampleRepository.save(sample);

        // create schema for default item
        WebUiSchema schema = new WebUiSchema();
        schema.name = sample.name;
        schema.project = project;
        schema = schemaRepository.save(schema);
        project.schemas.add(schema);
        projectRepository.save(project);

        // create first item
        WebUiItem item = new WebUiItem();
        item.name = schema.name;
        item.schema = schema; // associate item with schema
        item.sample = sample; // associate item with sample
        item = itemRepository.save(item);

        schema.item = item;
        schemaRepository.save(schema);

        sample.items.add(item);
        sampleRepository.save(sample);

        // get sample again to fetch new relations
        sample = sampleRepository.findOne(sample.id);

        return new WebUiSampleResponse(sample);
    }

    public WebUiSampleResponse updateSample(String projectId, String sampleId, JSONObject jsonObj) {
        Preconditions.checkNotNull(sampleId);

        Preconditions.checkArgument(jsonObj.has("data"));
        Preconditions.checkArgument(jsonObj.getJSONObject("data").has("attributes"));
        JSONObject data = jsonObj.getJSONObject("data");
        JSONObject attributes = data.getJSONObject("attributes");

        WebUiProject project = projectRepository.findOne(new Long(projectId));
        WebUiSample sample = sampleRepository.findOne(new Long(sampleId));
        Optional<WebUiSchema> schema = schemaRepository.findOneByNameAndProject(sample.name, project);

        sample.body = attributes.getString("body");
        sample.name = attributes.getString("name");
        sample.url = attributes.getString("url");

        sampleRepository.save(sample);

        // update schema name
        schema.get().name = sample.name;
        schemaRepository.save(schema.get());

        return new WebUiSampleResponse(sample);
    }

    public void addSampleRenderedBody(String sampleId, String html) {
        Preconditions.checkNotNull(sampleId);

        WebUiSample sample = sampleRepository.findOne(new Long(sampleId));
        WebUiRenderedBody renderedBody = new WebUiRenderedBody();
        renderedBody.sample = sample;
        renderedBody.html = html;
        sample.renderedBody = renderedBody;

        sampleRepository.save(sample);
    }

    public void addSampleOriginalBody(String sampleId, String html) {
        Preconditions.checkNotNull(sampleId);

        WebUiSample sample = sampleRepository.findOne(new Long(sampleId));
        WebUiOriginalBody renderedBody = new WebUiOriginalBody();
        renderedBody.sample = sample;
        renderedBody.html = html;
        sample.originalBody = renderedBody;

        sampleRepository.save(sample);
    }

    public WebUiSample getSampleEntity(String sampleId) {
        Preconditions.checkNotNull(sampleId);
        return sampleRepository.findOne(new Long(sampleId));
    }

    public String getSampleRenderedHtml(String sampleId) {
        WebUiSample sample = sampleRepository.findOne(new Long(sampleId));
        if(sample.renderedBody != null)
            return sample.renderedBody.html;
        else
            return null;
    }

    public String getSampleOriginalHtml(String sampleId) {
        WebUiSample sample = sampleRepository.findOne(new Long(sampleId));
        if(sample.renderedBody != null)
            return sample.renderedBody.html;
        else
            return null;
    }

    public WebUiDeleteSampleResponse deleteSample(String projectId, String sampleId) {
        WebUiProject project = projectRepository.findOne(new Long(projectId));
        WebUiSample sample = sampleRepository.findOne(new Long(sampleId));
        WebUiDeleteSampleResponse resp = new WebUiDeleteSampleResponse();

        if(sample.renderedBody != null) {
            resp.response.addMetaDelete(WebUiObjectType.RENDERED_BODY, sample.renderedBody.jsonId().toString());
        }

        if(sample.originalBody != null) {
            resp.response.addMetaDelete(WebUiObjectType.ORIGINAL_BODY, sample.originalBody.jsonId().toString());
        }

        Optional<WebUiSchema> schema = schemaRepository.findOneByNameAndProject(sample.name, project);
        if(schema.isPresent()) {
            schema.get().fields.forEach(field -> {
                field.annotations.forEach(annotation -> {
                    resp.response.addMetaDelete(WebUiObjectType.ANNOTATION, annotation.jsonId().toString());
                });

                resp.response.addMetaDelete(WebUiObjectType.FIELD, field.jsonId().toString());
            });
            schemaRepository.delete(schema.get());
        }

        sample.items.forEach(i -> {
            resp.response.addMetaDelete(WebUiObjectType.ITEM, i.jsonId().toString());
        });

        sampleRepository.delete(sample);

        return resp;
    }

    public BaseWebUiResponse createAnnotation(String userName, String projectId, String spiderName, String sampleId, JSONObject jsonObj) {
        Preconditions.checkArgument(jsonObj.has("data"));
        Preconditions.checkArgument(jsonObj.has("meta"));
        Preconditions.checkArgument(jsonObj.getJSONObject("data").has("attributes"));
        Preconditions.checkArgument(jsonObj.getJSONObject("meta").has("updates"));

        WebUiProject project = projectRepository.findOne(new Long(projectId));
        Optional<WebUiSpider> spider = spiderRepository.findOneByNameAndProject(spiderName, project);
        WebUiSample sample = sampleRepository.findOne(new Long(sampleId));

        JSONObject data = jsonObj.getJSONObject("data");
        JSONObject meta = jsonObj.getJSONObject("meta");
        JSONObject attributes = data.getJSONObject("attributes");

        Long parentItemId = data.getJSONObject("relationships").getJSONObject("parent").getJSONObject("data").getLong("id");
        WebUiItem parentItem = itemRepository.findOne(parentItemId);

        if(data.getString("type").equals(WebUiObjectType.ANNOTATION)) {
            // create field for annotation
            int previousFields = parentItem.schema.fields.size();

            WebUiAnnotation annotation = new WebUiAnnotation();
            annotation.acceptSelectors = jsonArrayToList(attributes.optJSONArray("accept-selectors"));
            annotation.rejectSelectors = jsonArrayToList(attributes.optJSONArray("reject-selectors"));
            annotation.attribute = attributes.getString("attribute");
            annotation.postText = attributes.optString("post-text");
            annotation.preText = attributes.optString("pre-text");
            annotation.selector = attributes.getString("selector");
            annotation.xpath = attributes.getString("xpath");
            annotation.selectionMode = attributes.getString("selection-mode");
            annotation.textContent = attributes.getString("text-content");
            annotation.repeated = attributes.getBoolean("repeated");
            annotation.required = attributes.getBoolean("required");
            annotation = annotationRepository.save(annotation);

            parentItem.addAnnotation(annotation);
            itemRepository.save(parentItem);

            WebUiField field = new WebUiField();
            field.name = "field" + (previousFields + 1);
            field.type = "text";
            field.addAnnotation(annotation);
            field = fieldRepository.save(field);
            parentItem.schema.addField(field);

            schemaRepository.save(parentItem.schema);

            try {
                processUpdates(meta.getJSONArray("updates"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            WebUiAnnotationResponse resp = new WebUiAnnotationResponse(project, sample, parentItem.schema, parentItem, annotation);
            resp.response.addMetaUpdate(WebUiObjectType.ITEM, parentItem.jsonId().toString());
            resp.response.addUpdateExtension();

            projectManager.syncWebUiproject(userName, project);

            return resp;
        } else {
            // nested item
            AtomicInteger subitemCount = new AtomicInteger(0);
            parentItem.annotations.forEach(child -> {
                if(child instanceof WebUiItem)
                    subitemCount.getAndIncrement();
            });

            String itemName = "subitem"+subitemCount.incrementAndGet();

            // create new schema for item
            WebUiSchema schema = new WebUiSchema();
            schema.name = itemName;
            schema = schemaRepository.save(schema);
            project.addSchema(schema);
            projectRepository.save(project);

            WebUiItem item = new WebUiItem();
            item.name = itemName;
            item.repeatedSelector = attributes.optString("repeated-selector");
            item.selector = attributes.optString("selector");
            item.siblings = attributes.optInt("siblings", 0);
            item.parent = parentItem;
            item.schema = schema;
            final WebUiItem savedItem = itemRepository.save(item);
            parentItem.addAnnotation(savedItem);
            itemRepository.save(parentItem);
            sample.addItem(item);
            sample = sampleRepository.save(sample);

            try {
                processUpdates(meta.getJSONArray("updates"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            WebUiItemResponse resp = new WebUiItemResponse(project, savedItem);
            resp.response.addMetaUpdate(WebUiObjectType.ITEM, parentItem.jsonId().toString());
            parentItem.annotations.forEach(child -> {
               if(child instanceof WebUiAnnotation)
                    resp.response.addMetaUpdate(WebUiObjectType.ANNOTATION, child.jsonId().toString());

            });
            resp.response.addUpdateExtension();

            projectManager.syncWebUiproject(userName, project);

            return resp;
        }
    }

    public WebUiDeleteAnnotationResponse deleteAnnotation(String userName, String projectId, String spiderId, String sampleId, String annotationId) {
        WebUiAnnotation annotation = annotationRepository.findOne(new Long(annotationId));
        WebUiField field = annotation.field;
        WebUiSchema schema = field.schema;
        schema.removeField(field);
        schemaRepository.save(schema);

        WebUiItem item = (WebUiItem) annotation.parent;
        item.removeAnnotation(annotation);
        item = itemRepository.save(item);

        WebUiSample sample = sampleRepository.findOne(new Long(sampleId));
        WebUiDeleteAnnotationResponse resp = new WebUiDeleteAnnotationResponse(sample);
        resp.response.addMetaDelete(WebUiObjectType.FIELD, field.jsonId().toString());
        resp.response.addMetaUpdate(WebUiObjectType.ITEM, item.jsonId().toString());
        resp.response.addDeleteExtension();
        resp.response.addUpdateExtension();

        WebUiProject project = projectRepository.findOne(new Long(projectId));
        projectManager.syncWebUiproject(userName, project);

        return resp;
    }

    public WebUiAnnotationResponse updateAnnotation(String userName, String projectId, String spiderName, String sampleId, String annotationId, JSONObject jsonObj) {
        Preconditions.checkArgument(jsonObj.has("data"));
        Preconditions.checkArgument(jsonObj.getJSONObject("data").has("attributes"));
        Preconditions.checkArgument(jsonObj.getJSONObject("data").has("relationships"));

        WebUiProject project = projectRepository.findOne(new Long(projectId));
        WebUiSample sample = sampleRepository.findOne(new Long(sampleId));

        JSONObject data = jsonObj.getJSONObject("data");
        JSONObject attributes = data.getJSONObject("attributes");
        JSONObject relationships = data.getJSONObject("relationships");

        final WebUiAnnotation annotation = annotationRepository.findOne(new Long(annotationId));
        annotation.acceptSelectors = jsonArrayToList(attributes.getJSONArray("accept-selectors"));
        annotation.rejectSelectors = jsonArrayToList(attributes.getJSONArray("reject-selectors"));
        annotation.attribute = attributes.getString("attribute");
        annotation.postText = attributes.optString("post-text");
        annotation.preText = attributes.optString("pre-text");
        annotation.selector = attributes.getString("selector");
        annotation.xpath = attributes.getString("xpath");
        annotation.selectionMode = attributes.getString("selection-mode");
        annotation.textContent = attributes.getString("text-content");
        annotation.repeated = attributes.getBoolean("repeated");
        annotation.required = attributes.getBoolean("required");
        // remove old annotation - field relationship
        annotation.field.removeAnnotation(annotation);

        // add new field to annotation
        JSONObject fieldJson = relationships.getJSONObject("field");
        WebUiField field = fieldRepository.findOne(fieldJson.getJSONObject("data").getLong("id"));
        field.addAnnotation(annotation);
        fieldRepository.save(field);

        // update extractors
        annotation.extractors.forEach(e -> {
            e.annotations.remove(annotation);
            extractorRepository.save(e);
        });

        annotation.extractors.clear();

        JSONArray extractorsJson = relationships.getJSONObject("extractors").optJSONArray("data");
        if(extractorsJson != null) {
            for (int i = 0; i < extractorsJson.length(); i++) {
                JSONObject extractorJson = extractorsJson.getJSONObject(i);
                WebUiExtractor extractor = extractorRepository.findOne(extractorJson.getLong("id"));
                extractor.annotations.add(annotation);
                annotation.extractors.add(extractor);
            }
        }
        WebUiAnnotation savedAnnotation = annotationRepository.save(annotation);

        projectManager.syncWebUiproject(userName, project);

        return new WebUiAnnotationResponse(project, sample, annotation.field.schema, (WebUiItem) savedAnnotation.parent, savedAnnotation);
    }

    public WebUiExtractorResponse createExtractor(String userName, String projectId, JSONObject jsonObj) {
        Preconditions.checkArgument(jsonObj.has("data"));
        Preconditions.checkArgument(jsonObj.getJSONObject("data").has("attributes"));

        JSONObject data = jsonObj.getJSONObject("data");
        JSONObject attributes = data.getJSONObject("attributes");

        WebUiProject project = projectRepository.findOne(new Long(projectId));
        WebUiExtractor extractor = new WebUiExtractor();
        extractor.type = attributes.optString("type");
        extractor.value = attributes.optString("value");
        extractor = extractorRepository.save(extractor);
        project.addExtractor(extractor);
        projectRepository.save(project);

        projectManager.syncWebUiproject(userName, project);

        return new WebUiExtractorResponse(extractor);
    }

    public WebUiExtractorResponse updateExtractor(String userName, String projectId, String extractorId, JSONObject jsonObj) {
        Preconditions.checkArgument(jsonObj.has("data"));
        Preconditions.checkArgument(jsonObj.getJSONObject("data").has("attributes"));
        Preconditions.checkArgument(jsonObj.getJSONObject("data").has("relationships"));

        WebUiExtractor extractor = extractorRepository.findOne(new Long(extractorId));
        if(extractor == null)
            throw new IllegalArgumentException("Cannot find extractor with id="+extractorId);

        JSONObject data = jsonObj.getJSONObject("data");
        JSONObject attributes = data.getJSONObject("attributes");

        extractor.type = attributes.optString("type");
        extractor.value = attributes.optString("value");
        extractor = extractorRepository.save(extractor);

        WebUiProject project = projectRepository.findOne(new Long(projectId));
        projectManager.syncWebUiproject(userName, project);

        return new WebUiExtractorResponse(extractor);
    }

    public WebUiFieldResponse createField(String userName, String schemaId, JSONObject jsonObj) {
        Preconditions.checkArgument(jsonObj.has("data"));
        Preconditions.checkArgument(jsonObj.getJSONObject("data").has("attributes"));

        JSONObject data = jsonObj.getJSONObject("data");
        JSONObject attributes = data.getJSONObject("attributes");

        WebUiSchema schema = schemaRepository.findOne(new Long(schemaId));
        WebUiField field = new WebUiField();
        field.name = attributes.optString("name", null);
        field.required = attributes.optBoolean("required", false);
        field.type = attributes.getString("type");
        field.vary = attributes.optBoolean("vary", false);
        field = fieldRepository.save(field);
        schema.addField(field);
        schemaRepository.save(schema);

        projectManager.syncWebUiproject(userName, schema.project);

        return new WebUiFieldResponse(field);
    }

    public BaseWebUiResponse deleteField(String userName, String projectId, String fieldId) {
        WebUiField field = fieldRepository.findOne(new Long(fieldId));
        fieldRepository.delete(field);

        WebUiProject project = projectRepository.findOne(new Long(projectId));
        projectManager.syncWebUiproject(userName, project);

        return null;
    }

    public BaseWebUiResponse updateField(String userName, String projectId, String fieldId, JSONObject jsonObj) {
        Preconditions.checkArgument(jsonObj.has("data"));
        Preconditions.checkArgument(jsonObj.getJSONObject("data").has("attributes"));
        Preconditions.checkArgument(jsonObj.getJSONObject("data").has("relationships"));

        JSONObject data = jsonObj.getJSONObject("data");
        JSONObject attributes = data.getJSONObject("attributes");

        WebUiField field = fieldRepository.findOne(new Long(fieldId));
        field.name = attributes.getString("name");
        field.required = attributes.optBoolean("required", false);
        field.vary = attributes.optBoolean("vary", false);
        field.type = attributes.getString("type");
        field = fieldRepository.save(field);

        WebUiProject project = projectRepository.findOne(new Long(projectId));
        projectManager.syncWebUiproject(userName, project);

        return new WebUiFieldResponse(field);
    }

    public BaseWebUiResponse createItem(String userName, String projectId, String spiderName, String sampleId, JSONObject jsonObj) {
        Preconditions.checkArgument(jsonObj.has("data"));
        Preconditions.checkArgument(jsonObj.has("meta"));
        Preconditions.checkArgument(jsonObj.getJSONObject("data").has("attributes"));
        Preconditions.checkArgument(jsonObj.getJSONObject("meta").has("updates"));

        WebUiProject project = projectRepository.findOne(new Long(projectId));
        WebUiSample sample = sampleRepository.findOne(new Long(sampleId));

        JSONObject data = jsonObj.getJSONObject("data");
        JSONObject meta = jsonObj.getJSONObject("meta");
        JSONObject attributes = data.getJSONObject("attributes");

        AtomicInteger sampleItemCount = new AtomicInteger(0);
        sample.items.forEach(child -> {
            sampleItemCount.getAndIncrement();
        });

        String itemName = sample.name + sampleItemCount.incrementAndGet();

        // create new schema for item
        WebUiSchema schema = new WebUiSchema();
        schema.name = itemName;
        schema = schemaRepository.save(schema);
        project.addSchema(schema);
        projectRepository.save(project);

        WebUiItem item = new WebUiItem();
        item.name = itemName;
        item.repeatedSelector = attributes.optString("repeated-selector");
        item.selector = attributes.optString("selector");
        item.siblings = attributes.optInt("siblings", 0);
        item.schema = schema;

        final WebUiItem savedItem = itemRepository.save(item);
        sample.addItem(item);
        sampleRepository.save(sample);

        schema.item = item;
        schema = schemaRepository.save(schema);

        try {
            processUpdates(meta.getJSONArray("updates"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        WebUiItemResponse resp = new WebUiItemResponse(project, savedItem);
        sample.items.forEach(sampleItem -> {
            if(sampleItem.id != savedItem.id)
                resp.response.addMetaUpdate(WebUiObjectType.ITEM, sampleItem.jsonId().toString());

            sampleItem.annotations.forEach(ann -> {
                if(ann instanceof WebUiAnnotation)
                    resp.response.addMetaUpdate(WebUiObjectType.ANNOTATION, ann.jsonId().toString());
                else if (ann instanceof WebUiItem)
                    resp.response.addMetaUpdate(WebUiObjectType.ITEM, ann.jsonId().toString());
            });
        });
        resp.response.addUpdateExtension();

        projectManager.syncWebUiproject(userName, project);

        return resp;

    }

    public BaseWebUiResponse updateItem(String userName, String projectId, String spiderId, String sampleId, String itemId, JSONObject jsonObj) {
        Preconditions.checkArgument(jsonObj.has("data"));
        Preconditions.checkArgument(jsonObj.getJSONObject("data").has("attributes"));

        JSONObject data = jsonObj.getJSONObject("data");
        JSONObject attributes = data.getJSONObject("attributes");

        WebUiProject project = projectRepository.findOne(new Long(projectId));
        WebUiItem item = itemRepository.findOne(new Long(itemId));
        item.name = attributes.optString("name");
        item.repeatedSelector = attributes.optString("repeated-selector");
        item.selector = attributes.optString("selector");
        item.siblings = attributes.optInt("siblings", 0);
        item = itemRepository.save(item);

        projectManager.syncWebUiproject(userName, project);

        return new WebUiItemResponse(project, item);
    }

    public BaseWebUiResponse deleteItem(String userName, String projectId, String spiderId, String sampleId, String itemId, JSONObject jsonObj) {
        WebUiItem item = itemRepository.findOne(new Long(itemId));

        WebUiProject project = projectRepository.findOne(new Long(projectId));
        project.removeSchema(item.schema);
        projectRepository.save(project);

        if(item.parent != null) {
            ((WebUiItem)item.parent).annotations.remove(item);
            itemRepository.save(((WebUiItem)item.parent));
        }

        WebUiSample sample = sampleRepository.findOne(new Long(sampleId));
        sample.removeItem(item);
        sample = sampleRepository.save(sample);

        WebUiDeleteItemResponse resp = new WebUiDeleteItemResponse(sample);
        resp.response.addMetaDelete(WebUiObjectType.SCHEMA, item.schema.jsonId().toString());
        if(item.parent != null) {
            resp.response.addMetaUpdate(WebUiObjectType.ITEM, item.parent.jsonId().toString());
        }

        if(jsonObj.has("meta")) {
            JSONObject meta = jsonObj.getJSONObject("meta");

            for (int i = 0; i < meta.getJSONArray("updates").length(); i++) {
                JSONObject updateJson = meta.getJSONArray("updates").getJSONObject(i);
                String objectType = updateJson.getString("type");
                Long id = updateJson.getLong("id");
                resp.response.addMetaUpdate(objectType, id.toString());
            }
        }

        projectManager.syncWebUiproject(userName, project);

        return resp;
    }

    public WebUiExtractorResponse getExtractor(String projectId) {
        throw new UnsupportedOperationException();
    }

    public WebUiExtractorsResponse getExtractors(String projectId) {
        WebUiProject project = projectRepository.findOne(new Long(projectId));
        return new WebUiExtractorsResponse(project);
    }

    public BaseWebUiResponse updateSchema(String userName, String schemaId, JSONObject jsonObj) {
        Preconditions.checkArgument(jsonObj.has("data"));
        Preconditions.checkArgument(jsonObj.getJSONObject("data").has("attributes"));

        JSONObject data = jsonObj.getJSONObject("data");
        JSONObject attributes = data.getJSONObject("attributes");

        WebUiSchema schema = schemaRepository.findOne(new Long(schemaId));
        schema.name = attributes.optString("name", "");
        schema.defaultSchema = attributes.optBoolean("default", false);
        schema = schemaRepository.save(schema);

        projectManager.syncWebUiproject(userName, schema.project);

        return new WebUiSchemaResponse(schema);
    }

    public BaseWebUiResponse getSchema(String schemaId) {
        WebUiSchema schema = schemaRepository.findOne(new Long(schemaId));
        return new WebUiSchemaResponse(schema);
    }

    private void processUpdates(JSONArray jsonUpdates) throws IOException {
        Preconditions.checkNotNull(jsonUpdates);

        for(int i=0; i<jsonUpdates.length(); i++) {
            // one update object has the form:
            // {
            //   attributes: {...}
            //   id: 5
            //   type: "items"
            // }
            JSONObject updateJson = jsonUpdates.getJSONObject(i);
            String objectType = updateJson.getString("type");
            Long id = updateJson.getLong("id");
            Class objectClass = WebUiObjectType.getObjectClass(objectType);
            JsonApiAware entity = (JsonApiAware) entityManager.find(objectClass, id);
            entity = objectMapper.readerForUpdating(entity).readValue(updateJson.getJSONObject("attributes").toString());
            entityManager.merge(entity);
        }
    }

    private List jsonArrayToList(JSONArray jsonArray) {
        List result = new ArrayList();
        if(jsonArray != null) {
            for(int i = 0; i<jsonArray.length(); i++) {
                result.add(i, jsonArray.get(i));
            }
        }
        return result;
    }
}
