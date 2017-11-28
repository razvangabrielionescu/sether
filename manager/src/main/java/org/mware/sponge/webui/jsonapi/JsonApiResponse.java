package org.mware.sponge.webui.jsonapi;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class JsonApiResponse extends JSONObject implements Serializable {
    // these have to match webui/app/js/adapters/application.js
    private final static String DELETED_EXTENSION = "https://portia.scrapinghub.com/jsonapi/extensions/deleted";
    private final static String UPDATES_EXTENSION = "https://portia.scrapinghub.com/jsonapi/extensions/updates";

    private JsonApiResponseIncluded included = new JsonApiResponseIncluded();
    private JSONObject meta = new JSONObject();

    public JsonApiResponse() {
        put("included", included);
        put("meta", meta);
    }

    public JsonApiResponseData data() {
        if(has("data") && !(get("data") instanceof JSONObject))
            throw new UnsupportedOperationException("data object already initialized as non-object");

        if(has("data")) {
            return (JsonApiResponseData) getJSONObject("data");
        } else {
            JsonApiResponseData dataObj = new JsonApiResponseData();
            put("data", dataObj);
            return dataObj;
        }
    }

    public JsonApiResponseData newArrayData() {
        JsonApiResponseData dataObject = new JsonApiResponseData();
        if(has("data") && get("data") instanceof JSONObject)
            throw new UnsupportedOperationException("data object already initialized as non-array");

        JSONArray existingArray = optJSONArray("data");
        if(existingArray == null) {
            existingArray = new JSONArray();
            put("data", existingArray);
        }

        existingArray.put(dataObject);
        return dataObject;
    }

    public JsonApiResponse selfLink(String url) {
        if(!has("links"))
            put("links", new JSONObject());

        getJSONObject("links").put("self", url);

        return this;
    }

    public JsonApiResponseData include() {
        JsonApiResponseData includedObject = new JsonApiResponseData();
        included.put(includedObject);
        return includedObject;
    }

    public void addMetaUpdate(String type, String id) {
        if(!meta.has("updates"))
            meta.put("updates", new JSONArray());

        meta.getJSONArray("updates").put(new JSONObject()
                .put("id", id)
                .put("type", type)
        );
    }

    public void addMetaDelete(String type, String id) {
        if(!meta.has("deleted"))
            meta.put("deleted", new JSONArray());

        meta.getJSONArray("deleted").put(new JSONObject()
                .put("id", id)
                .put("type", type)
        );
    }

    public void addUpdateExtension() {
        addAlias("updates", UPDATES_EXTENSION);
        addProfileLink(UPDATES_EXTENSION);
    }

    public void addDeleteExtension() {
        addAlias("deleted", DELETED_EXTENSION);
        addProfileLink(DELETED_EXTENSION);
    }

    private void addAlias(String type, String url) {
        if(!has("aliases"))
            put("aliases", new JSONObject());

        getJSONObject("aliases").put(type, url);
    }

    private void addProfileLink(String url) {
        if(!has("links"))
            put("links", new JSONObject());

        if(!getJSONObject("links").has("profile"))
            getJSONObject("links").put("profile", new JSONArray());

        getJSONObject("links").getJSONArray("profile").put(url);
    }
}
