package org.mware.sponge.webui.util;

import com.norconex.collector.http.url.Link;
import com.norconex.collector.http.url.impl.GenericLinkExtractor;
import com.norconex.commons.lang.file.ContentType;
import com.norconex.importer.doc.ImporterMetadata;
import com.norconex.importer.handler.ImporterHandlerException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mware.sponge.crawl.tagger.BigConnectDOMTagger;
import org.mware.sponge.webui.WebUiProjectService;
import org.mware.sponge.webui.domain.*;
import org.mware.sponge.webui.repository.WebUiAnnotationRepository;
import org.mware.sponge.webui.repository.WebUiSampleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class WebUiHtmlProcessor {
    @Autowired
    private WebUiProjectService projectService;

    @Autowired
    private WebUiSampleRepository sampleRepository;

    @Autowired
    private WebUiAnnotationRepository annotationRepository;

    GenericLinkExtractor linkExtractor;

    public WebUiHtmlProcessor() {
        linkExtractor = new GenericLinkExtractor();
    }

    public Map<String, String> extractLinks(String sampleId) {
        String originalHtml = projectService.getSampleOriginalHtml(sampleId);
        String renderedHtml = projectService.getSampleRenderedHtml(sampleId);
        WebUiSample sample = sampleRepository.findOne(new Long(sampleId));
        return extractLinks(sample.url, originalHtml, renderedHtml);
    }

    public Map<String, String> extractLinks(String url, String originalHtml, String renderedHtml) {
        Map<String, String> result = new HashMap<>();

        try {
            Set<Link> originalLinks = linkExtractor.extractLinks(
                    IOUtils.toInputStream(originalHtml, Charset.forName("UTF-8")),
                    url,
                    ContentType.HTML
            );

            Set<Link> renderedLinks = linkExtractor.extractLinks(
                    IOUtils.toInputStream(renderedHtml, Charset.forName("UTF-8")),
                    url,
                    ContentType.HTML
            );

            Set<Link> jsLinks = renderedLinks.stream()
                    .filter(l -> !originalLinks.contains(l))
                    .collect(Collectors.toSet());

            originalLinks.forEach(l -> result.put(l.getUrl(), "raw"));
            jsLinks.forEach(l -> result.put(l.getUrl(), "js"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public JSONArray extractItems(String url, String sampleId, String renderedHtml, String originalHtml) {
        WebUiSample sample = sampleRepository.findOne(new Long(sampleId));
        JSONArray result = new JSONArray();

        BigConnectDOMTagger domTagger = new BigConnectDOMTagger();
        for (WebUiItem item : sample.items) {
            for (BaseAnnotation annotation : item.annotations) {
                if(annotation instanceof WebUiAnnotation) {
                    WebUiAnnotation ann = (WebUiAnnotation)annotation;
                    WebUiField field = ann.field;
                    domTagger.addDOMExtractDetails(new BigConnectDOMTagger.DOMExtractDetails(
                            ann.selector + "|" + ann.acceptSelectors.get(0),
                            item.id+"\u001f"+ann.id+"\u001f"+field.name,
                            false
                    ));
                }
            }
        }

        // if js is enable on the spider, extract items from rendered document,
        // otherwise extract from original document
        String html = sample.spider.jsEnabled ? renderedHtml : originalHtml;
        InputStream documentInput = IOUtils.toInputStream(html, Charset.forName("UTF-8"));

        ImporterMetadata metadata = new ImporterMetadata();
        metadata.addString(ImporterMetadata.DOC_CONTENT_TYPE, ContentType.HTML.toString());

        try {
            domTagger.tagDocument(url, documentInput, metadata, false);
            Set<String> extractedFields = metadata.keySet();
            // group fields by item
            Map<String, List<String>> fieldsByItem =
                    extractedFields.stream().collect(Collectors.groupingBy(n -> StringUtils.split(n, '\u001f')[0]));

            for(String itemId : fieldsByItem.keySet()) {
                if(itemId.equals(ImporterMetadata.DOC_CONTENT_TYPE))
                    continue;

                JSONObject fieldObj = new JSONObject();
                fieldsByItem.get(itemId).forEach(f -> {
                    String annotationId = StringUtils.split(f, '\u001f')[1];
                    String fieldName = StringUtils.split(f, '\u001f')[2];
                    List<String> fieldValues = metadata.getStrings(f);
                    fieldValues = applyExtractors(annotationId, fieldValues);
                    fieldObj.put(fieldName, fieldValues);
                });
                fieldObj.put("url", url);
                result.put(fieldObj);
            }

        } catch (ImporterHandlerException e) {
            e.printStackTrace();
        }

        return result;
    }

    private List<String> applyExtractors(String annotationId, List<String> fieldValues) {
        List<String> result = new ArrayList<>();
        WebUiAnnotation annotation = annotationRepository.findOne(new Long(annotationId));
        for(String fieldValue : fieldValues) {
            String newFieldValue = fieldValue;
            for(WebUiExtractor extractor : annotation.extractors) {
                String extractedValue = applyExtractor(extractor, fieldValue);
                if(extractedValue != null)
                    newFieldValue = extractedValue;
            }
            result.add(newFieldValue);
        }

        return result;
    }

    private String applyExtractor(WebUiExtractor extractor, String value) {
        // TODO: apply extractor
        return value;
    }
}
