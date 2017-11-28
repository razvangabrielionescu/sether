package org.mware.sponge.util;

/**
 * Created by Dan on 8/28/2017.
 */
public class ProxySettings {
    private boolean usesProxy;
    private String proxyHost;
    private int proxyPort;
    private boolean usesAuth;
    private String username;
    private String password;

    /**
     */
    public ProxySettings() {
    }

    /**
     * @param usesProxy
     * @param proxyHost
     * @param proxyPort
     * @param usesAuth
     * @param username
     * @param password
     */
    public ProxySettings(boolean usesProxy, String proxyHost, int proxyPort, boolean usesAuth, String username, String password) {
        this.usesProxy = usesProxy;
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        this.usesAuth = usesAuth;
        this.username = username;
        this.password = password;
    }

    /**
     * @return
     */
    public boolean isUsesProxy() {
        return usesProxy;
    }

    /**
     * @param usesProxy
     */
    public void setUsesProxy(boolean usesProxy) {
        this.usesProxy = usesProxy;
    }

    /**
     * @return
     */
    public String getProxyHost() {
        return proxyHost;
    }

    /**
     * @param proxyHost
     */
    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    /**
     * @return
     */
    public int getProxyPort() {
        return proxyPort;
    }

    /**
     * @param proxyPort
     */
    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    /**
     * @return
     */
    public boolean isUsesAuth() {
        return usesAuth;
    }

    /**
     * @param usesAuth
     */
    public void setUsesAuth(boolean usesAuth) {
        this.usesAuth = usesAuth;
    }

    /**
     * @return
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "ProxySettings{" +
            "usesProxy=" + usesProxy +
            ", proxyUrl='" + proxyHost + '\'' +
            ", proxyPort=" + proxyPort +
            ", usesAuth=" + usesAuth +
            ", username='" + username + '\'' +
            ", password='" + password + '\'' +
            '}';
    }
}
