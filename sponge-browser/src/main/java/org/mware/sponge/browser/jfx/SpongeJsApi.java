package org.mware.sponge.browser.jfx;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mware.sponge.browser.tcpip.client.CallbackClient;
import java.net.MalformedURLException;
import java.net.URL;

public class SpongeJsApi {
    private final String sessionId;
    private final String parentHost;

    /**
     * @param parentHost
     * @param sessionId
     */
    public SpongeJsApi(String parentHost, String sessionId) {
        this.parentHost = parentHost;
        this.sessionId = sessionId;
    }

    /**
     * @param message
     */
    public void sendMessage(String message) {
        JSONArray array = new JSONArray(message);
        String command = array.getString(0);
        JSONArray commandData = array.getJSONArray(1);

        JSONObject response = new JSONObject();
        response.put("_command", command);
        response.put("_data", commandData);


        CallbackClient.getInstance(parentHost).sendWebsocketMessage(this.sessionId, response.toString());
    }

    /**
     * @param url
     * @param baseUri
     * @return
     */
    public String wrapUrl(String url, String baseUri) {
        if(!StringUtils.isEmpty(baseUri)) {
            try {
                URL base = new URL(baseUri);
                String root = base.getProtocol() + "://" + base.getHost();
                if(base.getPort() > 0) {
                    root += ":"+base.getPort();
                }

                if(url.startsWith("/")) {
                    url = root + url;
                } else {
//                    url = root + "/" + url;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        return url;
    }

    /**
     * @param css
     * @param baseUri
     * @return
     */
    public String processCss(String css, String baseUri) {
        return css;
    }
}
