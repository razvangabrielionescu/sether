package org.mware.sponge.webui.jsonapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mware.sponge.webui.domain.WebUiObjectType;

import java.io.Serializable;
import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.stream.Collectors;

public class JsonApiResponseData extends JSONObject implements Serializable {
    private ObjectMapper objectMapper = new ObjectMapper();
    private JSONObject relationships = new JSONObject();

    public JsonApiResponseData() {
        put("relationships", relationships);
    }

    public JsonApiResponseData type(String val) {
        put("type", val);
        return this;
    }

    public JsonApiResponseData id(Object val) {
        put("id", val.toString());
        return this;
    }

    public JsonApiResponseData selfLink(String url) {
        put("links", new JSONObject()
                .put("self", url)
        );
        return this;
    }

    public JsonApiResponseData toMany(String relName, String url, List<? extends JsonApiAware> values) {
        JSONObject rel = new JSONObject();

        if(!StringUtils.isEmpty(url)) {
            rel.put("links", new JSONObject()
                    .put("related", url)
            );
        }

        if(values != null && values.size() > 0) {
            rel.put("data", new JSONArray(
                    values.stream()
                            .map(v -> new JSONObject().put("id", v.jsonId()).put("type", v.jsonType()))
                            .collect(Collectors.toList())
            ));
        } else {
            rel.put("data", new JSONArray());
        }

        relationships.put(relName, rel);

        return this;
    }

    public JsonApiResponseData toMany(String relName, String url) {
        JSONObject rel = new JSONObject();

        if(!StringUtils.isEmpty(url)) {
            rel.put("links", new JSONObject()
                    .put("related", url)
            );
        }

        relationships.put(relName, rel);

        return this;
    }

    public JsonApiResponseData toOne(String relName, String url, JsonApiAware value) {
        JSONObject rel = new JSONObject();

        if(!StringUtils.isEmpty(url)) {
            rel.put("links", new JSONObject()
                    .put("related", url)
            );
        }

        if(value != null) {
            rel.put("data", new JSONObject()
                    .put("id", value.jsonId())
                    .put("type", value.jsonType())
            );
        } else {
            rel.put("data", JSONObject.NULL);
        }

        relationships.put(relName, rel);

        return this;
    }

    public JsonApiResponseData attributes(Object obj)  {
        try {
            String attrsJson = objectMapper.writeValueAsString(obj);
            put("attributes", new JSONObject(attrsJson));
            return this;
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
