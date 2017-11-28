package org.mware.sponge.util;

/**
 * Created by Dan on 9/7/2017.
 */
public class GenericSettings {
    private String userAgent;

    /**
     *
     */
    public GenericSettings() {
    }

    /**
     * @param userAgent
     */
    public GenericSettings(String userAgent) {
        this.userAgent = userAgent;
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

    @Override
    public String toString() {
        return "GenericSettings{" +
            "userAgent='" + userAgent + '\'' +
            '}';
    }
}
