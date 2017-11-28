package org.mware.sponge.browser.jfx;

import com.sun.javafx.webkit.Accessor;
import com.sun.webkit.JsMwareCustomListener;
import com.sun.webkit.LoadListenerClient;
import com.sun.webkit.WebPage;
import javafx.application.Application;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import org.mware.sponge.browser.tcpip.client.CallbackClient;
import org.mware.sponge.browser.util.PageDownloader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class WebUiBrowser {
    private static final Logger log = LoggerFactory.getLogger(WebUiBrowser.class);

    final AtomicReference<Stage> stage = new AtomicReference<>();
    final AtomicReference<WebView> view = new AtomicReference<>();
    final AtomicReference<String> renderedHtml = new AtomicReference<>();
    final AtomicReference<String> originalHtml = new AtomicReference<>();
    final AtomicReference<String> loadedUrl = new AtomicReference<>();
    final long PAGE_LOAD_TIMEOUT = TimeUnit.MILLISECONDS.convert(20, TimeUnit.SECONDS);
    final int width;
    final int height;
    final String userAgent;
    final AtomicBoolean initialLayoutCompleted = new AtomicBoolean(false);
    final String combinedJs;
    final String sessionId;
    final String parentHost;
    final PageDownloader pageDownloader;

    /**
     * @param parentHost
     * @param width
     * @param height
     * @param userAgent
     * @param sessionId
     * @param combinedJs
     */
    public WebUiBrowser(String parentHost, int width, int height, String userAgent, String sessionId, String combinedJs) {
        this.parentHost = parentHost;
        this.width = width;
        this.height = height;
        this.userAgent = userAgent;
        this.sessionId = sessionId;
        this.combinedJs = combinedJs;

        if(com.sun.glass.ui.Application.GetApplication() == null) {
            new Thread(() -> {
                try {
                    Application.launch(WebUiJavaFxApp.class,
                            new String[]{
                                    Integer.toString(width),
                                    Integer.toString(height),
                                    userAgent
                            });
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }).start();
        }

        stage.set(WebUiJavaFxApp.getStage());
        view.set(WebUiJavaFxApp.getView());

        AppThread.exec(() -> {
            WebPage page = Accessor.getPageFor(view.get().getEngine());
            Accessor.getPageFor(view.get().getEngine()).getPageClient().addJsMwareCustomListener(new JsMwareCustomListener() {
                @Override
                public void didClearWindowObject(long frameID) {
                    if(page.getMainFrame() == frameID) {
                        onJsWindowObjectCleared();
                    }
                }

                @Override
                public void didInitialLayoutComplete(long frameID) {
                    if(page.getMainFrame() == frameID) {
                        onLayoutCompleted();
                    }
                }
            });
            return null;
        });

        this.pageDownloader = new PageDownloader(userAgent);
    }

    public void load(String url, String loadId) {
        loadedUrl.set(url);

        new Thread(() -> {
            originalHtml.set(pageDownloader.downloadPage(url, (int) PAGE_LOAD_TIMEOUT));
        }).start();

        AppThread.exec(() -> {
            view.get().getEngine().load(url);
            Accessor.getPageFor(view.get().getEngine()).addLoadListenerClient(new LoadListenerClient() {
                public void dispatchLoadEvent(final long frame, final int state, String url,
                                              String contentType, double progress, int errorCode) {
                    long mainFrame = Accessor.getPageFor(view.get().getEngine()).getMainFrame();
                    if (state == LoadListenerClient.PAGE_FINISHED
                            || state == LoadListenerClient.LOAD_STOPPED
                            || state == LoadListenerClient.LOAD_FAILED) {
                        if(frame == mainFrame) {
                            log.debug("Page loading finished for url: "+url);
                            renderedHtml.set(Accessor.getPageFor(view.get().getEngine()).getHtml(mainFrame));

                            sendLoadFinished(url, loadId);
                        }
                    }
                }

                public void dispatchResourceLoadEvent(long frame, int state, String url,
                                                      String contentType, double progress, int errorCode) {
                }
            });

            return null;
        });
    }

    public Object executeJavascript(String script) {
        return AppThread.exec(() -> {
            return view.get().getEngine().executeScript(script);
        });
    }

    private void onLayoutCompleted() {
        if(!this.initialLayoutCompleted.get()) {
            this.populateWindowObject();
            this.initialLayoutCompleted.set(true);
        }
    }

    private void onJsWindowObjectCleared() {
        if(this.initialLayoutCompleted.get())
            this.populateWindowObject();
    }

    private void populateWindowObject() {
        JSObject js = (JSObject) view.get().getEngine().executeScript("window");
        js.setMember("__portiaApi", new SpongeJsApi(parentHost, sessionId));
        view.get().getEngine().executeScript(combinedJs+'\n');
    }

    private void sendLoadFinished(String url, String loadId) {
        CallbackClient.getInstance(parentHost).sendLoadFinished(this.sessionId, url, loadId);
    }

    public String getRenderedHtml() {
        return renderedHtml.get();
    }

    public String getOriginalHtml() {
        return originalHtml.get();
    }

    public String getLoadedUrl() {
        return loadedUrl.get();
    }
}
