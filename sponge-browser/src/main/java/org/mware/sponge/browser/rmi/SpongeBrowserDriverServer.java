package org.mware.sponge.browser.rmi;

import org.mware.sponge.browser.jfx.WebUiBrowser;
import org.mware.sponge.browser.process.PortGroup;
import org.mware.sponge.browser.process.SocketFactory;
import org.mware.sponge.browser.process.SocketLock;
import org.mware.sponge.browser.util.Constants;
import org.mware.sponge.browser.util.Util;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMISocketFactory;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Dan on 10/2/2017.
 */
public class SpongeBrowserDriverServer extends RemoteObject implements SpongeBrowserDriverRemote {
    private static final AtomicInteger childPort = new AtomicInteger();
    private static final AtomicReference<SocketFactory> socketFactory = new AtomicReference<SocketFactory>();
    private static String parentHost;
    private static Registry registry;
    private WebUiBrowser browser;

    /**
     * @throws RemoteException
     */
    protected SpongeBrowserDriverServer() throws RemoteException {}

    public static void main(String[] args) {
        try {
            parentHost = args[0];

            final String host = Constants.RMI_HOST;
            for (int i = 1; i <= Constants.MAX_FIND_PORT_RETRIES; i++) {
                try {
                    childPort.set(Util.findPort(host));
                    socketFactory.set(new SocketFactory(host,
                            new PortGroup(childPort.get(), 0, 0), new HashSet<SocketLock>()));

                    registry = LocateRegistry.createRegistry(childPort.get(), socketFactory.get(), socketFactory.get());
                    break;
                } catch (Throwable t) {
                    if (i == Constants.MAX_FIND_PORT_RETRIES) {
                        t.printStackTrace();
                    }
                }
            }

            registry.rebind("HeartbeatRemote", new HeartbeatServer());
            registry.rebind("SpongeBrowserDriverRemote", new SpongeBrowserDriverServer());

            RMISocketFactory.setSocketFactory(socketFactory.get());
            System.out.println(Constants.RMI_PORTS_MESSAGE + childPort.get() + "/0/0");
            System.out.println("RMI Server started");
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    static int childPort() {
        return childPort.get();
    }

    static SocketFactory socketFactory() {
        return socketFactory.get();
    }

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
    @Override
    public void loadPage(String sessionId, Integer viewPortWidth, Integer viewPortHeight,
                         String userAgent, String url, String loadId, String combinedJs) throws RemoteException {
        if (browser == null) {
            browser = new WebUiBrowser(parentHost, viewPortWidth, viewPortHeight, userAgent, sessionId, combinedJs);
        }

        browser.load(url, loadId);
    }

    /**
     * @param script
     */
    @Override
    public void executeJS(String script) {
        if (browser == null) {
            return;
        }

        browser.executeJavascript(script);
    }

    /**
     * @throws RemoteException
     */
    @Override
    public void quit() throws RemoteException {
        System.exit(0);
    }

    @Override
    public String getRenderedHtml() throws RemoteException {
        if (browser == null) {
            return null;
        }

        return browser.getRenderedHtml();
    }

    @Override
    public String getOriginalHtml() throws RemoteException {
        if (browser == null) {
            return null;
        }

        return browser.getOriginalHtml();
    }

    @Override
    public String getLoadedUrl() throws RemoteException {
        if (browser == null) {
            return null;
        }

        return browser.getLoadedUrl();
    }
}
