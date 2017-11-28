package org.mware.sponge.web.rest;

import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mware.sponge.SpongeApp;
import org.mware.sponge.webui.WebUiProjectService;
import org.mware.sponge.webui.WebUiRestResource;
import org.mware.sponge.webui.domain.*;
import org.mware.sponge.webui.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mware.sponge.web.rest.RegexMatcher.matchesRegex;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpongeApp.class)
@Transactional
public class WebUiRestResourceTest {
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
    private WebUiRenderedBodyRepository renderedBodyRepository;
    @Autowired
    private WebUiOriginalBodyRepository originalBodyRepository;
    @Autowired
    private WebUiAnnotationRepository annotationRepository;
    @Autowired
    private WebUiFieldRepository fieldRepository;
    @Autowired
    private WebUiProjectService projectService;

    private MockMvc restMvc;

    @Autowired
    private EntityManager em;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        WebUiRestResource webUiRestResource = new WebUiRestResource(projectService);
        this.restMvc = MockMvcBuilders.standaloneSetup(webUiRestResource).build();
    }

    @Test
    public void testCreateProject() throws Exception {
        String projectName = UUID.randomUUID().toString();

        String createProjectJson = "{" +
                "   'data':{" +
                "      'attributes':{" +
                "         'name':'"+projectName+"'" +
                "      }," +
                "      'type':'projects'" +
                "   }" +
                "}";

        restMvc.perform(post("/webui/api/projects")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(createProjectJson)
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.type").value("projects"))
                .andExpect(jsonPath("$.data.attributes.name").value(projectName))
                .andExpect(jsonPath("$.data.relationships.extractors.links.related", matchesRegex("/sponge/webui/api/projects/[0-9]*/extractors")))
                .andExpect(jsonPath("$.data.relationships.schemas.links.related", matchesRegex("/sponge/webui/api/projects/[0-9]*/schemas")))
                .andExpect(jsonPath("$.data.relationships.spiders.links.related", matchesRegex("/sponge/webui/api/projects/[0-9]*/spiders")));

        // Validate the Project in the database
        Optional<WebUiProject> project = projectRepository.findOneByName(projectName);
        assertThat(project.isPresent());
        assertThat(project.get().name).isEqualTo(projectName);
    }

    @Test
    public void testGetProject() throws Exception {
        String projectName = UUID.randomUUID().toString();

        WebUiProject project = new WebUiProject();
        project.name = projectName;
        project = projectRepository.save(project);

        String spiderName1 = UUID.randomUUID().toString();
        WebUiSpider spider1 = new WebUiSpider();
        spider1.project = project;
        spider1.name = spiderName1;
        spider1.startUrls.add(new WebUiStartUrl("http://one.bigconnect.io/", "url"));
        spider1 = spiderRepository.save(spider1);

        String spiderName2 = UUID.randomUUID().toString();
        WebUiSpider spider2 = new WebUiSpider();
        spider2.project = project;
        spider2.name = spiderName2;
        spider2.startUrls.add(new WebUiStartUrl("http://two.bigconnect.io/", "url"));
        spider2 = spiderRepository.save(spider2);

        String schemaName1 = UUID.randomUUID().toString();
        WebUiSchema schema1 = new WebUiSchema();
        schema1.name = schemaName1;
        schema1.project = project;
        schema1 = schemaRepository.save(schema1);

        String schemaName2 = UUID.randomUUID().toString();
        WebUiSchema schema2 = new WebUiSchema();
        schema2.name = schemaName2;
        schema2.project = project;
        schema2 = schemaRepository.save(schema2);

        project.spiders.add(spider1);
        project.spiders.add(spider2);
        project.schemas.add(schema1);
        project.schemas.add(schema2);
        projectRepository.save(project);

        String json = restMvc.perform(get("/webui/api/projects/"+project.id)
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.type").value("projects"))
                .andExpect(jsonPath("$.data.attributes.name").value(projectName))
                .andExpect(jsonPath("$.data.relationships.extractors.links.related").value("/sponge/webui/api/projects/"+project.jsonId()+"/extractors"))
                .andExpect(jsonPath("$.data.relationships.schemas.links.related").value("/sponge/webui/api/projects/"+project.jsonId()+"/schemas"))
                .andExpect(jsonPath("$.data.relationships.spiders.links.related").value("/sponge/webui/api/projects/"+project.jsonId()+"/spiders"))
                .andExpect(jsonPath("$.links.self").value("/sponge/webui/api/projects/"+project.jsonId()))
                .andReturn().getResponse().getContentAsString();

        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'schemas')].id")).contains(schema1.jsonId().toString(), schema2.jsonId().toString());
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'schemas')].type")).contains("schemas");
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'schemas')].links.self")).contains(
                "/sponge/webui/api/projects/"+project.jsonId()+"/schemas/"+ schema1.jsonId(),
                "/sponge/webui/api/projects/"+project.jsonId()+"/schemas/"+ schema2.jsonId()
        );
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'schemas')].attributes.default")).contains(false);
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'schemas')].attributes.name")).contains(schemaName1, schemaName2);
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'schemas')].relationships.fields.links.related")).contains(
                "/sponge/webui/api/projects/"+project.id+"/schemas/"+schema1.id+"/fields",
                "/sponge/webui/api/projects/"+project.id+"/schemas/"+schema2.id+"/fields"
        );
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'schemas')].relationships.project.links.related")).contains("/sponge/webui/api/projects/"+project.id);
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'schemas')].relationships.project.data.type")).contains("projects");
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'schemas')].relationships.project.data.id")).contains(project.id.intValue());

    }

    @Test
    public void testCreateSpider() throws Exception {
        String projectName = UUID.randomUUID().toString();

        WebUiProject project = new WebUiProject();
        project.name = projectName;
        project = projectRepository.save(project);
        Long projectId = project.id;

        String spiderName = UUID.randomUUID().toString();

        String createSpiderJson = "{  " +
                "   'data':{  " +
                "      'id':'"+spiderName+"'," +
                "      'type':'spiders'," +
                "      'attributes':{  " +
                "         'name':null," +
                "         'start-urls':[  " +
                "            {  " +
                "               'url':'http://bits.bigconnect.io/'," +
                "               'type':'url'" +
                "            }" +
                "         ]," +
                "         'respect-nofollow':false," +
                "         'page-actions':[]," +
                "         'perform-login':false," +
                "         'login-url':null," +
                "         'login-user':null," +
                "         'login-password':null," +
                "         'links-to-follow':'all'," +
                "         'follow-patterns':[],  " +
                "         'exclude-patterns':[],  " +
                "         'show-links':false," +
                "         'respect-no-follow':true," +
                "         'js-enabled':false," +
                "         'js-enable-patterns':[],  " +
                "         'js-disable-patterns':[]  " +
                "      }," +
                "      'relationships':{  " +
                "         'project':{  " +
                "            'data':{  " +
                "               'type':'projects'," +
                "               'id':'"+projectId+"'" +
                "            }" +
                "         }" +
                "      }" +
                "   }" +
                "}";

        restMvc.perform(post("/webui/api/projects/"+projectId+"/spiders")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(createSpiderJson)
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(spiderName))
                .andExpect(jsonPath("$.data.links.self").value("/sponge/webui/api/projects/"+projectId+"/spiders/"+spiderName))
                .andExpect(jsonPath("$.data.attributes.links-to-follow").value("all"))
                .andExpect(jsonPath("$.data.attributes.start-urls.[0].url").value("http://bits.bigconnect.io/"))
                .andExpect(jsonPath("$.data.attributes.start-urls.[0].type").value("url"))
                .andExpect(jsonPath("$.data.relationships.project.links.related").value("/sponge/webui/api/projects/"+projectId))
                .andExpect(jsonPath("$.data.relationships.project.data.id").value(projectId))
                .andExpect(jsonPath("$.data.relationships.project.data.type").value("projects"))
                .andExpect(jsonPath("$.data.relationships.samples.links.related").value("/sponge/webui/api/projects/"+projectId+"/spiders/"+spiderName+"/samples"));

        Optional<WebUiSpider> spider = spiderRepository.findOneByNameAndProject(spiderName, project);
        assertThat(spider.isPresent());
        assertThat(spider.get().name).isEqualTo(spiderName);
        assertThat(spider.get().project.id).isEqualTo(projectId);
    }

    @Test
    public void testGetSpider() throws Exception {
        WebUiProject project = new WebUiProject();
        project.name = "Project "+UUID.randomUUID().toString();
        project = projectRepository.save(project);

        String spiderName = UUID.randomUUID().toString();
        WebUiSpider spider = new WebUiSpider();
        spider.project = project;
        spider.name = spiderName;
        spider.excludePatterns = Arrays.asList("exclude1", "exclude2");
        spider.followPatterns = Arrays.asList("follow1", "follow2");
        spider.allowedDomains = Arrays.asList("domain1.com", "domain2.com");
        spider.jsDisablePatterns = Arrays.asList("jsdisable1", "jsdisable2");
        spider.jsEnablePatterns = Arrays.asList("jsenable1", "jsenable2");
        spider.jsEnabled = true;
        spider.linksToFollow = "none";
        spider.loginUrl = "http://www.loginurl.com";
        spider.loginUser = "user1";
        spider.loginPassword = "password1";
        spider.performLogin = true;
        spider.respectNoFollow = true;
        spider.startUrls.add(new WebUiStartUrl("http://bits.bigconnect.io/", "url"));
        spider = spiderRepository.save(spider);

        restMvc.perform(get("/webui/api/projects/"+project.id+"/spiders/"+spider.name)
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(spider.name))
                .andExpect(jsonPath("$.data.type").value("spiders"))
                .andExpect(jsonPath("$.data.links.self").value("/sponge/webui/api/projects/"+project.id+"/spiders/"+spider.name))
                .andExpect(jsonPath("$.links.self").value("/sponge/webui/api/projects/"+project.id+"/spiders/"+spider.name))
                .andExpect(jsonPath("$.data.attributes.allowed-domains[?(@ == 'domain1.com')]").value("domain1.com"))
                .andExpect(jsonPath("$.data.attributes.allowed-domains[?(@ == 'domain2.com')]").value("domain2.com"))
                .andExpect(jsonPath("$.data.attributes.exclude-patterns[?(@ == 'exclude1')]").value("exclude1"))
                .andExpect(jsonPath("$.data.attributes.exclude-patterns[?(@ == 'exclude2')]").value("exclude2"))
                .andExpect(jsonPath("$.data.attributes.follow-patterns[?(@ == 'follow1')]").value("follow1"))
                .andExpect(jsonPath("$.data.attributes.follow-patterns[?(@ == 'follow2')]").value("follow2"))
                .andExpect(jsonPath("$.data.attributes.js-disable-patterns[?(@ == 'jsdisable1')]").value("jsdisable1"))
                .andExpect(jsonPath("$.data.attributes.js-disable-patterns[?(@ == 'jsdisable2')]").value("jsdisable2"))
                .andExpect(jsonPath("$.data.attributes.js-enable-patterns[?(@ == 'jsenable1')]").value("jsenable1"))
                .andExpect(jsonPath("$.data.attributes.js-enable-patterns[?(@ == 'jsenable2')]").value("jsenable2"))
                .andExpect(jsonPath("$.data.attributes.js-enabled").value(spider.jsEnabled))
                .andExpect(jsonPath("$.data.attributes.links-to-follow").value(spider.linksToFollow))
                .andExpect(jsonPath("$.data.attributes.login-password").value(spider.loginPassword))
                .andExpect(jsonPath("$.data.attributes.login-url").value(spider.loginUrl))
                .andExpect(jsonPath("$.data.attributes.login-user").value(spider.loginUser))
                .andExpect(jsonPath("$.data.attributes.perform-login").value(spider.performLogin))
                .andExpect(jsonPath("$.data.attributes.respect-no-follow").value(spider.respectNoFollow))
                .andExpect(jsonPath("$.data.attributes.start-urls.[0].url").value("http://bits.bigconnect.io/"))
                .andExpect(jsonPath("$.data.attributes.start-urls.[0].type").value("url"))
                .andExpect(jsonPath("$.data.relationships.project.links.related").value("/sponge/webui/api/projects/"+project.id))
                .andExpect(jsonPath("$.data.relationships.project.data.id").value(project.id))
                .andExpect(jsonPath("$.data.relationships.project.data.type").value("projects"))
                .andExpect(jsonPath("$.data.relationships.samples.links.related").value("/sponge/webui/api/projects/"+project.id+"/spiders/"+spider.name+"/samples"));
    }

    @Test
    public void testUpdateSpider() throws Exception {
        WebUiProject project = new WebUiProject();
        project.name = "Project "+UUID.randomUUID().toString();
        project = projectRepository.save(project);

        String spiderName = UUID.randomUUID().toString();
        WebUiSpider spider = new WebUiSpider();
        spider.project = project;
        spider.name = spiderName;
        spider = spiderRepository.save(spider);

        String updateSpiderJson = "{  " +
                "   'data':{  " +
                "      'id':'"+spider.jsonId()+"'," +
                "      'attributes':{  " +
                "         'name':null," +
                "         'start-urls':[  " +
                "            {  " +
                "               'url':'http://bits.bigconnect.io/1'," +
                "               'type':'url'" +
                "            }," +
                "            {  " +
                "               'url':'http://bits.bigconnect.io/2'," +
                "               'type':'url'" +
                "            }" +
                "         ]," +
                "         'respect-nofollow':true," +
                "         'page-actions':[  " +
                "         ]," +
                "         'perform-login':true," +
                "         'login-url':'lurl'," +
                "         'login-user':'luser'," +
                "         'login-password':'password'," +
                "         'links-to-follow':'patterns'," +
                "         'follow-patterns':[  " +
                "            'urlf1', 'urlf2'" +
                "         ]," +
                "         'exclude-patterns':[  " +
                "            'urle1', 'urle2'" +
                "         ]," +
                "         'show-links':false," +
                "         'respect-no-follow':true," +
                "         'js-enabled':true," +
                "         'js-enable-patterns':[  " +
                "            'jse1', 'jse2'" +
                "         ]," +
                "         'js-disable-patterns':[  " +
                "            'jsd1', 'jsd2'" +
                "         ]" +
                "      }," +
                "      'relationships':{  " +
                "         'project':{  " +
                "            'data':{  " +
                "               'type':'projects'," +
                "               'id': '"+project.jsonId()+"'" +
                "            }" +
                "         }" +
                "      }," +
                "      'type':'spiders'" +
                "   }" +
                "}";

        String json = restMvc.perform(patch("/webui/api/projects/"+project.jsonId()+"/spiders/"+spider.jsonId())
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(updateSpiderJson)
        )
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.data.id").value(spider.name))
                .andExpect(jsonPath("$.data.type").value("spiders"))
                .andExpect(jsonPath("$.data.links.self").value("/sponge/webui/api/projects/"+project.id+"/spiders/"+spider.name))
                .andExpect(jsonPath("$.data.attributes.exclude-patterns[?(@ == 'urle1')]").value("urle1"))
                .andExpect(jsonPath("$.data.attributes.exclude-patterns[?(@ == 'urle2')]").value("urle2"))
                .andExpect(jsonPath("$.data.attributes.follow-patterns[?(@ == 'urlf1')]").value("urlf1"))
                .andExpect(jsonPath("$.data.attributes.follow-patterns[?(@ == 'urlf2')]").value("urlf2"))
                .andExpect(jsonPath("$.data.attributes.js-disable-patterns[?(@ == 'jsd1')]").value("jsd1"))
                .andExpect(jsonPath("$.data.attributes.js-disable-patterns[?(@ == 'jsd2')]").value("jsd2"))
                .andExpect(jsonPath("$.data.attributes.js-enable-patterns[?(@ == 'jse1')]").value("jse1"))
                .andExpect(jsonPath("$.data.attributes.js-enable-patterns[?(@ == 'jse2')]").value("jse2"))
                .andExpect(jsonPath("$.data.attributes.js-enabled").value(true))
                .andExpect(jsonPath("$.data.attributes.links-to-follow").value("patterns"))
                .andExpect(jsonPath("$.data.attributes.login-password").value("password"))
                .andExpect(jsonPath("$.data.attributes.login-url").value("lurl"))
                .andExpect(jsonPath("$.data.attributes.login-user").value("luser"))
                .andExpect(jsonPath("$.data.attributes.perform-login").value(true))
                .andExpect(jsonPath("$.data.attributes.respect-no-follow").value(true))
                .andExpect(jsonPath("$.data.relationships.project.links.related").value("/sponge/webui/api/projects/"+project.id))
                .andExpect(jsonPath("$.data.relationships.project.data.id").value(project.id))
                .andExpect(jsonPath("$.data.relationships.project.data.type").value("projects"))
                .andExpect(jsonPath("$.data.relationships.samples.links.related").value("/sponge/webui/api/projects/"+project.id+"/spiders/"+spider.name+"/samples"))
                .andReturn().getResponse().getContentAsString();

        Optional<WebUiSpider> optSpider = spiderRepository.findOneByNameAndProject(spiderName, project);
        assertThat(optSpider.isPresent()).isTrue();

        assertThat((JSONArray)JsonPath.read(json, "$.data.attributes.start-urls[*].url")).contains("http://bits.bigconnect.io/1","http://bits.bigconnect.io/2");
        assertThat((JSONArray)JsonPath.read(json, "$.data.attributes.start-urls[*].type")).contains("url");
    }

    @Test
    public void testGetSamples() throws Exception {
        WebUiProject project = new WebUiProject();
        project.name = "Project "+UUID.randomUUID().toString();
        project = projectRepository.save(project);

        String spiderName = UUID.randomUUID().toString();
        WebUiSpider spider = new WebUiSpider();
        spider.project = project;
        spider.name = spiderName;
        spider.startUrls.add(new WebUiStartUrl("http://bits.bigconnect.io/", "url"));
        spider = spiderRepository.save(spider);

        String sampleName1 = UUID.randomUUID().toString();
        String sampleName2 = UUID.randomUUID().toString();
        WebUiSample sample1 = new WebUiSample();
        sample1.name = sampleName1;
        sample1.spider = spider;
        sample1 = sampleRepository.save(sample1);

        WebUiSample sample2 = new WebUiSample();
        sample2.name = sampleName2;
        sample2.spider = spider;
        sample2 = sampleRepository.save(sample2);

        spider.samples.add(sample1);
        spider.samples.add(sample2);
        spider = spiderRepository.save(spider);

        String json = restMvc.perform(get("/webui/api/projects/"+project.id+"/spiders/"+spider.name+"/samples")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.links.self").value("/sponge/webui/api/projects/"+project.id+"/spiders/"+spider.name+"/samples"))
                .andReturn().getResponse().getContentAsString();

        assertThat((JSONArray)JsonPath.read(json, "$.data[*].id")).contains(sample1.jsonId().toString(), sample2.jsonId().toString());
        assertThat((JSONArray)JsonPath.read(json, "$.data[*].type")).contains("samples");
        assertThat((JSONArray)JsonPath.read(json, "$.data[*].attributes.name")).contains(sampleName1, sampleName2);
        assertThat((JSONArray)JsonPath.read(json, "$.data[*].links.self")).contains(
                "/sponge/webui/api/projects/"+project.id+"/spiders/"+spider.name+"/samples/"+sample1.jsonId(),
                "/sponge/webui/api/projects/"+project.id+"/spiders/"+spider.name+"/samples/"+sample2.jsonId()
        );
        assertThat((JSONArray)JsonPath.read(json, "$.data[*].relationships.items.links.related")).contains(
                "/sponge/webui/api/projects/"+project.id+"/spiders/"+spider.name+"/samples/"+sample1.jsonId()+"/items?filter[parent]=null",
                "/sponge/webui/api/projects/"+project.id+"/spiders/"+spider.name+"/samples/"+sample2.jsonId()+"/items?filter[parent]=null"
        );
        assertThat((JSONArray)JsonPath.read(json, "$.data[*].relationships.spider.links.related")).contains("/sponge/webui/api/projects/"+project.id+"/spiders/"+spider.name);
        assertThat((JSONArray)JsonPath.read(json, "$.data[*].relationships.spider.data.id")).contains(spider.jsonId().toString());
        assertThat((JSONArray)JsonPath.read(json, "$.data[*].relationships.spider.data.type")).contains("spiders");
    }

    @Test
    public void testCreateSample() throws Exception {
        WebUiProject project = new WebUiProject();
        project.name = "Project "+UUID.randomUUID().toString();
        project = projectRepository.save(project);

        String spiderName = UUID.randomUUID().toString();
        WebUiSpider spider = new WebUiSpider();
        spider.project = project;
        spider.name = spiderName;
        spider.startUrls.add(new WebUiStartUrl("http://bits.bigconnect.io/", "url"));
        spider = spiderRepository.save(spider);

        String sampleName = UUID.randomUUID().toString();

        String createSampleJson = "{  " +
                "   'data':{  " +
                "      'attributes':{  " +
                "         'name':'"+sampleName+"'," +
                "         'url':'http://bits.bigconnect.io/'," +
                "         'body':'original_body'" +
                "      }," +
                "      'relationships':{  " +
                "         'spider':{  " +
                "            'data':{  " +
                "               'type':'spiders'," +
                "               'id':'"+spiderName+"'" +
                "            }" +
                "         }" +
                "      }," +
                "      'type':'samples'" +
                "   }" +
                "}";

        ResultActions result = restMvc.perform(post("/webui/api/projects/"+project.id+"/spiders/"+spiderName+"/samples")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(createSampleJson)
        )
                .andExpect(status().isCreated());

        List<WebUiSample> samples = sampleRepository.findAllByName(sampleName);
        assertThat(samples.size() == 1);
        WebUiSample sample = samples.get(0);
        assertThat(sample.name).isEqualTo(sampleName);
        assertThat(sample.body).isEqualTo("original_body");
        assertThat(sample.url).isEqualTo("http://bits.bigconnect.io/");
        assertThat(sample.spider.id).isEqualTo(spider.id);
        assertThat(sample.items).hasSize(1);
        assertThat(sample.spider.project.schemas).hasSize(1);

        WebUiSchema schema = sample.spider.project.schemas.get(0);
        WebUiItem item = sample.items.get(0);

        String json = result
                .andExpect(jsonPath("$.data.type").value("samples"))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.links.self").value("/sponge/webui/api/projects/"+project.id+"/spiders/"+spider.name+"/samples/"+sample.id))
                .andExpect(jsonPath("$.data.attributes.name").value(sampleName))
                .andExpect(jsonPath("$.data.attributes.url").value("http://bits.bigconnect.io/"))
                .andExpect(jsonPath("$.data.relationships.items.links.related").value("/sponge/webui/api/projects/"+project.id+"/spiders/"+spider.name+"/samples/"+sample.id+"/items?filter[parent]=null"))
                .andExpect(jsonPath("$.data.relationships.items.data[0].type").value("items"))
                .andExpect(jsonPath("$.data.relationships.items.data[0].id").value(item.id))
                .andExpect(jsonPath("$.data.relationships.spider.links.related").value("/sponge/webui/api/projects/"+project.id+"/spiders/"+spider.name))
                .andExpect(jsonPath("$.data.relationships.spider.data.type").value("spiders"))
                .andExpect(jsonPath("$.data.relationships.spider.data.id").value(spider.name))
                .andReturn().getResponse().getContentAsString();

        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'items')].id")).contains(item.jsonId().toString());
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'items')].type")).contains("items");
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'items')].links.self")).contains("/sponge/webui/api/projects/"+project.id+"/spiders/"+spider.name+"/samples/"+sample.id+"/items/"+item.id);
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'items')].attributes.siblings")).contains(0);
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'items')].relationships.annotations.links.related")).contains("/sponge/webui/api/projects/"+project.id+"/spiders/"+spider.name+"/samples/"+sample.id+"/annotations?filter[parent]="+item.id);
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'items')].relationships.sample.links.related")).contains("/sponge/webui/api/projects/"+project.id+"/spiders/"+spider.name+"/samples/"+sample.id);
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'items')].relationships.sample.data.type")).contains("samples");
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'items')].relationships.sample.data.id")).contains(sample.id.intValue());
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'items')].relationships.schema.links.related")).contains("/sponge/webui/api/projects/"+project.id+"/schemas/"+schema.id);
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'items')].relationships.schema.data.type")).contains("schemas");
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'items')].relationships.schema.data.id")).contains(schema.id.intValue());

        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'schemas')].id")).contains(schema.jsonId().toString());
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'schemas')].type")).contains("schemas");
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'schemas')].links.self")).contains("/sponge/webui/api/projects/"+project.id+"/schemas/"+schema.id);
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'schemas')].attributes.default")).contains(false);
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'schemas')].attributes.name")).contains(sampleName);
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'schemas')].relationships.fields.links.related")).contains("/sponge/webui/api/projects/"+project.id+"/schemas/"+schema.id+"/fields");
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'schemas')].relationships.project.links.related")).contains("/sponge/webui/api/projects/"+project.id);
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'schemas')].relationships.project.data.type")).contains("projects");
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'schemas')].relationships.project.data.id")).contains(project.id.intValue());
    }

    @Test
    public void testUpdateSample() throws Exception {
        WebUiProject project = new WebUiProject();
        project.name = "Project "+UUID.randomUUID().toString();
        project = projectRepository.save(project);

        WebUiSchema schema = new WebUiSchema();
        schema.name = UUID.randomUUID().toString();
        schema.project = project;
        schema = schemaRepository.save(schema);
        project.schemas.add(schema);

        String spiderName = UUID.randomUUID().toString();
        WebUiSpider spider = new WebUiSpider();
        spider.project = project;
        spider.name = spiderName;
        spider = spiderRepository.save(spider);
        project.spiders.add(spider);
        project= projectRepository.save(project);

        String sampleName = UUID.randomUUID().toString();
        WebUiSample sample = new WebUiSample();
        sample.name = sampleName;
        sample.spider = spider;
        sample = sampleRepository.save(sample);
        spider.samples.add(sample);

        WebUiRenderedBody body = new WebUiRenderedBody();
        body.sample = sample;
        body = renderedBodyRepository.save(body);

        WebUiItem item = new WebUiItem();
        item.sample = sample;
        item.schema = schema;
        item = itemRepository.save(item);

        sample.items.add(item);
        sample.renderedBody = body;
        sample = sampleRepository.save(sample);

        schema.item = item;
        schema = schemaRepository.save(schema);

        spiderRepository.save(spider);

        String updateSampleJson = "{  " +
                "   'data':{  " +
                "      'id':'146c-4173-a0b2'," +
                "      'attributes':{  " +
                "         'name':'index of 1'," +
                "         'url':'http://bits.bigconnect.io/'," +
                "         'body':'original_body'" +
                "      }," +
                "      'relationships':{  " +
                "         'spider':{  " +
                "            'data':{  " +
                "               'type':'spiders'," +
                "               'id':'bits.bigconnect.io'" +
                "            }" +
                "         }" +
                "      }," +
                "      'type':'samples'" +
                "   }" +
                "}";

        String json = restMvc.perform(patch("/webui/api/projects/"+project.jsonId()+"/spiders/"+spider.jsonId()+"/samples/"+sample.jsonId())
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(updateSampleJson)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.type").value("samples"))
                .andExpect(jsonPath("$.data.id").value(sample.jsonId().toString()))
                .andExpect(jsonPath("$.data.attributes.name").value("index of 1"))
                .andExpect(jsonPath("$.data.attributes.url").value("http://bits.bigconnect.io/"))
                .andExpect(jsonPath("$.data.relationships.items.links.related").value("/sponge/webui/api/projects/"+project.id+"/spiders/"+spider.name+"/samples/"+sample.jsonId()+"/items?filter[parent]=null"))
                .andExpect(jsonPath("$.data.relationships.rendered-body.data.type").value("rendered-bodys"))
                .andExpect(jsonPath("$.data.relationships.rendered-body.data.id").value(body.jsonId().toString()))
                .andExpect(jsonPath("$.data.relationships.spider.links.related").value("/sponge/webui/api/projects/"+project.id+"/spiders/"+spider.name))
                .andExpect(jsonPath("$.data.relationships.spider.data.type").value("spiders"))
                .andExpect(jsonPath("$.data.relationships.spider.data.id").value(spider.jsonId().toString()))
                .andReturn().getResponse().getContentAsString();


        assertThat((JSONArray)JsonPath.read(json, "$.data.relationships.items.data[*].id")).contains(item.id.intValue());
        assertThat((JSONArray)JsonPath.read(json, "$.data.relationships.items.data[*].type")).contains("items");
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'items')].id")).contains(item.jsonId().toString());
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'items')].type")).contains("items");
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'items')].links.self")).contains("/sponge/webui/api/projects/"+project.id+"/spiders/"+spider.name+"/samples/"+sample.id+"/items/"+item.id);
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'items')].attributes.siblings")).contains(0);
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'items')].relationships.annotations.links.related")).contains("/sponge/webui/api/projects/"+project.id+"/spiders/"+spider.name+"/samples/"+sample.id+"/annotations?filter[parent]="+item.id);
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'items')].relationships.sample.links.related")).contains("/sponge/webui/api/projects/"+project.id+"/spiders/"+spider.name+"/samples/"+sample.id);
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'items')].relationships.sample.data.type")).contains("samples");
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'items')].relationships.sample.data.id")).contains(sample.id.intValue());
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'items')].relationships.schema.links.related")).contains("/sponge/webui/api/projects/"+project.id+"/schemas/"+schema.id);
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'items')].relationships.schema.data.type")).contains("schemas");
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'items')].relationships.schema.data.id")).contains(schema.id.intValue());

        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'schemas')].id")).contains(schema.jsonId().toString());
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'schemas')].type")).contains("schemas");
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'schemas')].links.self")).contains("/sponge/webui/api/projects/"+project.id+"/schemas/"+schema.id);
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'schemas')].attributes.default")).contains(false);
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'schemas')].attributes.name")).contains(schema.name);
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'schemas')].relationships.fields.links.related")).contains("/sponge/webui/api/projects/"+project.id+"/schemas/"+schema.id+"/fields");
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'schemas')].relationships.project.links.related")).contains("/sponge/webui/api/projects/"+project.id);
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'schemas')].relationships.project.data.type")).contains("projects");
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'schemas')].relationships.project.data.id")).contains(project.id.intValue());
    }

    @Test
    public void testGetSample() throws Exception {
        WebUiProject project = new WebUiProject();
        project.name = "Project "+UUID.randomUUID().toString();
        project = projectRepository.save(project);

        WebUiSchema schema = new WebUiSchema();
        schema.name = UUID.randomUUID().toString();
        schema.project = project;
        schema = schemaRepository.save(schema);
        project.schemas.add(schema);

        String spiderName = UUID.randomUUID().toString();
        WebUiSpider spider = new WebUiSpider();
        spider.project = project;
        spider.name = spiderName;
        spider = spiderRepository.save(spider);
        project.spiders.add(spider);
        project= projectRepository.save(project);

        String sampleName = UUID.randomUUID().toString();
        WebUiSample sample = new WebUiSample();
        sample.name = sampleName;
        sample.url = "http://bits.bigconnect.io/";
        sample.spider = spider;
        sample = sampleRepository.save(sample);
        spider.samples.add(sample);

        WebUiRenderedBody body = new WebUiRenderedBody();
        body.sample = sample;
        body = renderedBodyRepository.save(body);

        WebUiItem item = new WebUiItem();
        item.sample = sample;
        item.schema = schema;
        item = itemRepository.save(item);

        sample.items.add(item);
        sample.renderedBody = body;
        sample = sampleRepository.save(sample);

        schema.item = item;
        schema = schemaRepository.save(schema);

        spiderRepository.save(spider);

        String json = restMvc.perform(get("/webui/api/projects/"+project.jsonId()+"/spiders/"+spider.jsonId()+"/samples/"+sample.jsonId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.links.self").value("/sponge/webui/api/projects/"+project.id+"/spiders/"+spider.name+"/samples/"+sample.jsonId()))
                .andExpect(jsonPath("$.data.type").value("samples"))
                .andExpect(jsonPath("$.data.id").value(sample.jsonId().toString()))
                .andExpect(jsonPath("$.data.attributes.name").value(sampleName))
                .andExpect(jsonPath("$.data.attributes.url").value("http://bits.bigconnect.io/"))
                .andExpect(jsonPath("$.data.relationships.items.links.related").value("/sponge/webui/api/projects/"+project.id+"/spiders/"+spider.name+"/samples/"+sample.jsonId()+"/items?filter[parent]=null"))
                .andExpect(jsonPath("$.data.relationships.rendered-body.data.type").value("rendered-bodys"))
                .andExpect(jsonPath("$.data.relationships.rendered-body.data.id").value(body.jsonId().toString()))
                .andExpect(jsonPath("$.data.relationships.spider.links.related").value("/sponge/webui/api/projects/"+project.id+"/spiders/"+spider.name))
                .andExpect(jsonPath("$.data.relationships.spider.data.type").value("spiders"))
                .andExpect(jsonPath("$.data.relationships.spider.data.id").value(spider.jsonId().toString()))
                .andReturn().getResponse().getContentAsString();


        assertThat((JSONArray)JsonPath.read(json, "$.data.relationships.items.data[*].id")).contains(item.id.intValue());
        assertThat((JSONArray)JsonPath.read(json, "$.data.relationships.items.data[*].type")).contains("items");
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'items')].id")).contains(item.jsonId().toString());
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'items')].type")).contains("items");
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'items')].links.self")).contains("/sponge/webui/api/projects/"+project.id+"/spiders/"+spider.name+"/samples/"+sample.id+"/items/"+item.id);
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'items')].attributes.siblings")).contains(0);
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'items')].relationships.annotations.links.related")).contains("/sponge/webui/api/projects/"+project.id+"/spiders/"+spider.name+"/samples/"+sample.id+"/annotations?filter[parent]="+item.id);
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'items')].relationships.sample.links.related")).contains("/sponge/webui/api/projects/"+project.id+"/spiders/"+spider.name+"/samples/"+sample.id);
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'items')].relationships.sample.data.type")).contains("samples");
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'items')].relationships.sample.data.id")).contains(sample.id.intValue());
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'items')].relationships.schema.links.related")).contains("/sponge/webui/api/projects/"+project.id+"/schemas/"+schema.id);
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'items')].relationships.schema.data.type")).contains("schemas");
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'items')].relationships.schema.data.id")).contains(schema.id.intValue());

        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'schemas')].id")).contains(schema.jsonId().toString());
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'schemas')].type")).contains("schemas");
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'schemas')].links.self")).contains("/sponge/webui/api/projects/"+project.id+"/schemas/"+schema.id);
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'schemas')].attributes.default")).contains(false);
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'schemas')].attributes.name")).contains(schema.name);
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'schemas')].relationships.fields.links.related")).contains("/sponge/webui/api/projects/"+project.id+"/schemas/"+schema.id+"/fields");
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'schemas')].relationships.project.links.related")).contains("/sponge/webui/api/projects/"+project.id);
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'schemas')].relationships.project.data.type")).contains("projects");
        assertThat((JSONArray)JsonPath.read(json, "$.included[?(@.type == 'schemas')].relationships.project.data.id")).contains(project.id.intValue());
    }

    @Test
    public void testDeleteSample() throws Exception {
        WebUiProject project = new WebUiProject();
        project.name = "Project "+UUID.randomUUID().toString();
        project = projectRepository.save(project);

        WebUiSchema schema = new WebUiSchema();
        schema.name = UUID.randomUUID().toString();
        schema.project = project;
        schema = schemaRepository.save(schema);
        project.schemas.add(schema);

        String spiderName = UUID.randomUUID().toString();
        WebUiSpider spider = new WebUiSpider();
        spider.project = project;
        spider.name = spiderName;
        spider = spiderRepository.save(spider);
        project.spiders.add(spider);
        project= projectRepository.save(project);

        String sampleName = UUID.randomUUID().toString();
        WebUiSample sample = new WebUiSample();
        sample.name = sampleName;
        sample.spider = spider;
        sample = sampleRepository.save(sample);
        spider.samples.add(sample);

        WebUiRenderedBody renderedBody = new WebUiRenderedBody();
        renderedBody.sample = sample;
        renderedBody = renderedBodyRepository.save(renderedBody);

        WebUiOriginalBody originalBody = new WebUiOriginalBody();
        originalBody.sample = sample;
        originalBody = originalBodyRepository.save(originalBody);

        WebUiItem item1 = new WebUiItem();
        item1.sample = sample;
        item1.schema = schema;
        item1 = itemRepository.save(item1);


        sample.items.add(item1);
        sample.renderedBody = renderedBody;
        sample.originalBody = originalBody;
        sample = sampleRepository.save(sample);

        schema.item = item1;
        schema = schemaRepository.save(schema);

        spiderRepository.save(spider);

        String json = restMvc.perform(delete("/webui/api/projects/"+project.jsonId()+"/spiders/"+spider.jsonId()+"/samples/"+sample.jsonId()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThat((JSONArray)JsonPath.read(json, "$.meta.deleted[?(@.type == 'items')].id")).contains(item1.jsonId().toString());
        assertThat((JSONArray)JsonPath.read(json, "$.meta.deleted[?(@.type == 'schemas')].id")).contains(schema.jsonId().toString());
        assertThat((JSONArray)JsonPath.read(json, "$.meta.deleted[?(@.type == 'rendered-bodys')].id")).contains(renderedBody.jsonId().toString());
        assertThat((JSONArray)JsonPath.read(json, "$.meta.deleted[?(@.type == 'original-bodys')].id")).contains(originalBody.jsonId().toString());

        assertThat(sampleRepository.findOne(sample.id)).isNull();
        assertThat(itemRepository.findOne(item1.id)).isNull();
        assertThat(renderedBodyRepository.findOne(renderedBody.id)).isNull();
        assertThat(renderedBodyRepository.findOne(originalBody.id)).isNull();
        assertThat(schemaRepository.findOne(schema.id)).isNull();
    }

    public void testDeleteSpider() {
    }

    @Test
    public void testCreateAnnotation() throws Exception {
        WebUiProject project = new WebUiProject();
        project.name = "Project "+UUID.randomUUID().toString();
        project = projectRepository.save(project);

        String sampleName = UUID.randomUUID().toString();

        WebUiSchema schema = new WebUiSchema();
        schema.name = sampleName;
        schema.project = project;
        schema = schemaRepository.save(schema);
        project.schemas.add(schema);

        String spiderName = UUID.randomUUID().toString();
        WebUiSpider spider = new WebUiSpider();
        spider.project = project;
        spider.name = spiderName;
        spider = spiderRepository.save(spider);
        project.spiders.add(spider);
        project= projectRepository.save(project);

        WebUiSample sample = new WebUiSample();
        sample.name = sampleName;
        sample.url = "http://bits.bigconnect.io/";
        sample.spider = spider;
        sample = sampleRepository.save(sample);
        spider.samples.add(sample);

        WebUiRenderedBody body = new WebUiRenderedBody();
        body.sample = sample;
        body = renderedBodyRepository.save(body);

        WebUiItem item = new WebUiItem();
        item.sample = sample;
        item.schema = schema;
        item = itemRepository.save(item);

        sample.items.add(item);
        sample.renderedBody = body;
        sample = sampleRepository.save(sample);

        schema.item = item;
        schema = schemaRepository.save(schema);

        spiderRepository.save(spider);

        String createAnnotationJson = "{  " +
                "   'data':{  " +
                "      'attributes':{  " +
                "         'attribute':'href'," +
                "         'text-content':'content'," +
                "         'required':false," +
                "         'repeated':false," +
                "         'selection-mode':'auto'," +
                "         'selector':'pre > a:nth-child(10)'," +
                "         'xpath':'//pre/a[10]'," +
                "         'accept-selectors':[  " +
                "            'a:nth-child(10)'" +
                "         ]," +
                "         'reject-selectors':[  " +
                "         ]," +
                "         'pre-text':null," +
                "         'post-text':null" +
                "      }," +
                "      'relationships':{  " +
                "         'field':{  " +
                "            'data':null" +
                "         }," +
                "         'extractors':{  " +
                "            'data':[  " +
                "            ]" +
                "         }," +
                "         'parent':{  " +
                "            'data':{  " +
                "               'type':'items'," +
                "               'id':'"+item.jsonId()+"'" +
                "            }" +
                "         }" +
                "      }," +
                "      'type':'annotations'" +
                "   }," +
                "   'links':{  " +
                "      'profile':[  " +
                "         'https://portia.scrapinghub.com/jsonapi/extensions/updates'" +
                "      ]" +
                "   }," +
                "   'aliases':{  " +
                "      'updates':'https://portia.scrapinghub.com/jsonapi/extensions/updates'" +
                "   }," +
                "   'meta':{  " +
                "      'updates':[  " +
                "         {  " +
                "            'id':'"+item.jsonId()+"'," +
                "            'attributes':{  " +
                "               'selector':'pre'," +
                "               'repeated-selector':null," +
                "               'siblings':0" +
                "            }," +
                "            'type':'items'" +
                "         }" +
                "      ]" +
                "   }" +
                "}";

        ResultActions result = restMvc.perform(post("/webui/api/projects/"+project.id+"/spiders/"+spiderName+"/samples/"+sample.jsonId()+"/annotations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(createAnnotationJson)
        )
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    public void testUpdateAnnotation() throws Exception {
        WebUiProject project = new WebUiProject();
        project.name = "Project "+UUID.randomUUID().toString();
        project = projectRepository.save(project);

        String sampleName = UUID.randomUUID().toString();

        WebUiSchema schema = new WebUiSchema();
        schema.name = sampleName;
        schema.project = project;
        schema = schemaRepository.save(schema);
        project.schemas.add(schema);

        String spiderName = UUID.randomUUID().toString();
        WebUiSpider spider = new WebUiSpider();
        spider.project = project;
        spider.name = spiderName;
        spider = spiderRepository.save(spider);
        project.spiders.add(spider);
        project= projectRepository.save(project);

        WebUiSample sample = new WebUiSample();
        sample.name = sampleName;
        sample.url = "http://bits.bigconnect.io/";
        sample.spider = spider;
        sample = sampleRepository.save(sample);
        spider.samples.add(sample);

        WebUiRenderedBody body = new WebUiRenderedBody();
        body.sample = sample;
        body = renderedBodyRepository.save(body);

        WebUiItem item = new WebUiItem();
        item.sample = sample;
        item.schema = schema;
        item = itemRepository.save(item);

        sample.items.add(item);
        sample.renderedBody = body;
        sample = sampleRepository.save(sample);

        schema.item = item;
        schema = schemaRepository.save(schema);

        spiderRepository.save(spider);

        // create annotation
        WebUiAnnotation annotation = new WebUiAnnotation();
        annotation.parent = item;
        annotation = annotationRepository.save(annotation);
        item.annotations.add(annotation);
        item = itemRepository.save(item);

        WebUiField field = new WebUiField();
        field.name = "field1";
        field.type = "text";
        field.schema = schema;
        field.annotations.add(annotation);
        field = fieldRepository.save(field);
        schema.fields.add(field);
        schema = schemaRepository.save(schema);

        annotation.field = field;
        annotation = annotationRepository.save(annotation);

        String updateJson = "{  " +
                "   'data':{  " +
                "      'id':'"+annotation.jsonId()+"'," +
                "      'attributes':{  " +
                "         'attribute':'href'," +
                "         'text-content':'content'," +
                "         'required':false," +
                "         'repeated':false," +
                "         'selection-mode':'auto'," +
                "         'selector':'pre > a:nth-child(10)'," +
                "         'xpath':'//pre/a[10]'," +
                "         'accept-selectors':[  " +
                "            'a:nth-child(10)'" +
                "         ]," +
                "         'reject-selectors':[  " +
                "         ]," +
                "         'pre-text':null," +
                "         'post-text':null" +
                "      }," +
                "      'relationships':{  " +
                "         'field':{  " +
                "            'data':{  " +
                "               'type':'fields'," +
                "               'id':'"+field.jsonId()+"'" +
                "            }" +
                "         }," +
                "         'extractors':{  " +
                "            'data':[  " +
                "            ]" +
                "         }," +
                "         'parent':{  " +
                "            'data':{  " +
                "               'type':'items'," +
                "               'id':'"+item.jsonId()+"'" +
                "            }" +
                "         }" +
                "      }," +
                "      'type':'annotations'" +
                "   }" +
                "}";

        ResultActions result = restMvc.perform(patch("/webui/api/projects/"+project.id+"/spiders/"+spiderName+"/samples/"+sample.jsonId()+"/annotations/"+annotation.jsonId())
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(updateJson)
        )
                .andDo(print())
                .andExpect(status().isOk());
    }

    public void testDeleteAnnotation() {
    }

    public void testGetExtractors() {
    }

    @Test
    public void testCreateExtractor() throws Exception {
        WebUiProject project = new WebUiProject();
        project.name = "Project "+UUID.randomUUID().toString();
        project = projectRepository.save(project);

        String sampleName = UUID.randomUUID().toString();

        WebUiSchema schema = new WebUiSchema();
        schema.name = sampleName;
        schema.project = project;
        schema = schemaRepository.save(schema);
        project.schemas.add(schema);

        String spiderName = UUID.randomUUID().toString();
        WebUiSpider spider = new WebUiSpider();
        spider.project = project;
        spider.name = spiderName;
        spider = spiderRepository.save(spider);
        project.spiders.add(spider);
        project= projectRepository.save(project);

        WebUiSample sample = new WebUiSample();
        sample.name = sampleName;
        sample.url = "http://bits.bigconnect.io/";
        sample.spider = spider;
        sample = sampleRepository.save(sample);
        spider.samples.add(sample);

        WebUiRenderedBody body = new WebUiRenderedBody();
        body.sample = sample;
        body = renderedBodyRepository.save(body);

        WebUiItem item = new WebUiItem();
        item.sample = sample;
        item.schema = schema;
        item = itemRepository.save(item);

        sample.items.add(item);
        sample.renderedBody = body;
        sample = sampleRepository.save(sample);

        schema.item = item;
        schema = schemaRepository.save(schema);

        spiderRepository.save(spider);

        // create annotation
        WebUiAnnotation annotation = new WebUiAnnotation();
        annotation.parent = item;
        annotation = annotationRepository.save(annotation);
        item.annotations.add(annotation);
        item = itemRepository.save(item);

        WebUiField field = new WebUiField();
        field.name = "field1";
        field.type = "text";
        field.schema = schema;
        field.annotations.add(annotation);
        field = fieldRepository.save(field);
        schema.fields.add(field);
        schema = schemaRepository.save(schema);

        annotation.field = field;
        annotation = annotationRepository.save(annotation);

        String createJson = "{  " +
                "   'data':{  " +
                "      'attributes':{  " +
                "         'type':'regex'," +
                "         'value':'(.*)'" +
                "      }," +
                "      'relationships':{  " +
                "         'project':{  " +
                "            'data':{  " +
                "               'type':'projects'," +
                "               'id':'"+project.jsonId()+"'" +
                "            }" +
                "         }," +
                "         'annotations':{  " +
                "            'data':[  " +
                "            ]" +
                "         }" +
                "      }," +
                "      'type':'extractors'" +
                "   }" +
                "}";
        ResultActions result = restMvc.perform(post("/webui/api/projects/"+project.id+"/extractors")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(createJson)
        )
                .andDo(print())
                .andExpect(status().isCreated());
    }

    public void testUpdateExtractor() {
    }

    public void testCreateField() {
    }

    public void testUpdateField() {
    }

    public void testDeleteField() {
    }

    public void testGetFields() {
    }

    public void testUpdateItem() {
    }

    public void testGetSchema() {
    }

    public void testCreateSchema() {
    }

    public void testUpdateSchema() {
    }
}
