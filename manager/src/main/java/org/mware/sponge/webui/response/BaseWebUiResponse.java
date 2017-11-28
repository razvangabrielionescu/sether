package org.mware.sponge.webui.response;

import org.json.JSONObject;
import org.mware.sponge.webui.jsonapi.JsonApiResponse;

public abstract class BaseWebUiResponse {
    public JsonApiResponse response = new JsonApiResponse();

    public String getJson() {
        return response.toString();
    }

    public abstract String getCreatedUrl();
}
