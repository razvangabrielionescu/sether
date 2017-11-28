package org.mware.sponge.browser.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by Dan on 10/2/2017.
 */
public interface SpongeBrowserDriverRemote extends Remote {
    /**
     * @param sessionId
     * @param viewPortWidth
     * @param viewPortHeight
     * @param userAgent
     * @param url
     * @param loadId
     * @param combinedJs
     * @throws RemoteException
     */
    public void loadPage(String sessionId, Integer viewPortWidth, Integer viewPortHeight,
                         String userAgent, String url, String loadId, String combinedJs) throws RemoteException;

    /**
     * @param script
     * @throws RemoteException
     */
    public void executeJS(String script) throws RemoteException;

    public String getRenderedHtml() throws RemoteException;

    public String getOriginalHtml() throws RemoteException;

    public String getLoadedUrl() throws RemoteException;

    /**
     * @throws RemoteException
     */
    public void quit() throws RemoteException;
}
