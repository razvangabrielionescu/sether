package org.mware.sponge.webui.domain;

public class WebUiObjectType {
    public final static String PROJECT = "projects";
    public final static String SPIDER = "spiders";
    public final static String ANNOTATION = "annotations";
    public final static String EXTRACTOR = "extractors";
    public final static String FIELD = "fields";
    public final static String ITEM = "items";
    public final static String SAMPLE = "samples";
    public final static String SCHEMA = "schemas";
    public final static String RENDERED_BODY = "rendered-bodys";
    public final static String ORIGINAL_BODY = "original-bodys";

    public static Class<?> getObjectClass(String objectType) {
        switch (objectType) {
            case PROJECT:
                return WebUiProject.class;
            case SPIDER:
                return WebUiSpider.class;
            case ANNOTATION:
                return WebUiAnnotation.class;
            case EXTRACTOR:
                return WebUiExtractor.class;
            case FIELD:
                return WebUiField.class;
            case ITEM:
                return WebUiItem.class;
            case SAMPLE:
                return WebUiSample.class;
            case SCHEMA:
                return WebUiSchema.class;
            case RENDERED_BODY:
            case ORIGINAL_BODY:
                return WebUiRenderedBody.class;
            default:
                return null;
        }
    }
}
