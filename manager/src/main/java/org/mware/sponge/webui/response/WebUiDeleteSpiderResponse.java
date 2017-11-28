package org.mware.sponge.webui.response;

public class WebUiDeleteSpiderResponse extends BaseWebUiResponse {
    public WebUiDeleteSpiderResponse() {
        response.addDeleteExtension();
    }

    @Override
    public String getCreatedUrl() {
        throw new IllegalStateException();
    }
}
