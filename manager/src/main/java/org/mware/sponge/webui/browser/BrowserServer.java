package org.mware.sponge.webui.browser;

/**
 * Created by Dan on 10/3/2017.
 */
public class BrowserServer {
    private String host;
    private int port;
    private int activeProcesses;

    /**
     * @param host
     * @param port
     */
    public BrowserServer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * @return
     */
    public String getHost() {
        return host;
    }

    /**
     * @param host
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return
     */
    public int getPort() {
        return port;
    }

    /**
     * @param port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @return
     */
    public int getActiveProcesses() {
        return activeProcesses;
    }

    /**
     * @param activeProcesses
     */
    public void setActiveProcesses(int activeProcesses) {
        this.activeProcesses = activeProcesses;
    }

    @Override
    public String toString() {
        return "BrowserServer{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", activeProcesses=" + activeProcesses +
                '}';
    }
}