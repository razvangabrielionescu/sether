package org.mware.sponge.browser.util;

/**
 * Created by Dan on 10/20/2017.
 */
public class PageCache {
    private String sessionId;
    private Integer viewPortWidth;
    private Integer viewPortHeight;
    private String userAgent;
    private String url;
    private String loadId;
    private String combinedJs;

    /**
     * @param sessionId
     * @param viewPortWidth
     * @param viewPortHeight
     * @param userAgent
     * @param url
     * @param loadId
     * @param combinedJs
     */
    public PageCache(String sessionId, Integer viewPortWidth, Integer viewPortHeight,
                        String userAgent, String url, String loadId, String combinedJs) {
        this.sessionId = sessionId;
        this.viewPortWidth = viewPortWidth;
        this.viewPortHeight = viewPortHeight;
        this.userAgent = userAgent;
        this.url = url;
        this.loadId = loadId;
        this.combinedJs = combinedJs;
    }

    /**
     * @return
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * @param sessionId
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * @return
     */
    public Integer getViewPortWidth() {
        return viewPortWidth;
    }

    /**
     * @param viewPortWidth
     */
    public void setViewPortWidth(Integer viewPortWidth) {
        this.viewPortWidth = viewPortWidth;
    }

    /**
     * @return
     */
    public Integer getViewPortHeight() {
        return viewPortHeight;
    }

    /**
     * @param viewPortHeight
     */
    public void setViewPortHeight(Integer viewPortHeight) {
        this.viewPortHeight = viewPortHeight;
    }

    /**
     * @return
     */
    public String getUserAgent() {
        return userAgent;
    }

    /**
     * @param userAgent
     */
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    /**
     * @return
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return
     */
    public String getLoadId() {
        return loadId;
    }

    /**
     * @param loadId
     */
    public void setLoadId(String loadId) {
        this.loadId = loadId;
    }

    /**
     * @return
     */
    public String getCombinedJs() {
        return combinedJs;
    }

    /**
     * @param combinedJs
     */
    public void setCombinedJs(String combinedJs) {
        this.combinedJs = combinedJs;
    }

    @Override
    public String toString() {
        return "PageCache{" +
                "sessionId='" + sessionId + '\'' +
                ", viewPortWidth=" + viewPortWidth +
                ", viewPortHeight=" + viewPortHeight +
                ", userAgent='" + userAgent + '\'' +
                ", url='" + url + '\'' +
                ", loadId='" + loadId + '\'' +
                ", combinedJs='" + combinedJs + '\'' +
                '}';
    }
}
