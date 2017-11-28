package org.mware.sponge.webui;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mware.sponge.exception.SystemConfigurationNotFoundException;
import org.mware.sponge.repository.SystemConfigurationRepository;
import org.mware.sponge.util.Constants;
import org.mware.sponge.webui.browser.BrowserServer;
import org.mware.sponge.webui.browser.SpongeBrowserClient;
import org.mware.sponge.webui.util.WebUiHtmlProcessor;
import org.mware.sponge.webui.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.Executors;

@Service
public class WebUiCommandHandler {
    private static final Logger log = LoggerFactory.getLogger(WebUiCommandHandler.class);

    @Autowired
    private SystemConfigurationRepository systemConfigurationRepository;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private WebUiProjectService projectService;

    @Autowired
    private WebUiHtmlProcessor htmlProcessor;

    private List<BrowserServer> servers;
    private final Map<String, BrowserServer> stickySessions =
            Collections.synchronizedMap(new HashMap<String, BrowserServer>());
    private final Map<String, WebSocketSession> liveSessions =
            Collections.synchronizedMap(new HashMap<String, WebSocketSession>());

    private String combinedJs;

    /**
     *
     */
    public WebUiCommandHandler() { this.initSessionDestroyer(); }

    /**
     * @param session
     * @param payload
     */
    public void loadPage(WebSocketSession session, JSONObject payload) {
        this.prepareServers();
        registerSession(session);

        String url = payload.getString("url");
        JSONObject meta = payload.getJSONObject("_meta");
        String viewPort = meta.getString("viewport");
        String userAgent = meta.getString("user_agent");

        String[] sizeParts = viewPort.split("x");
        Integer viewPortWidth = Integer.parseInt(sizeParts[0]);
        Integer viewPortHeight = Integer.parseInt(sizeParts[1]);

        String loadId = UUID.randomUUID().toString();
        remoteLoadCommand(session.getId(), viewPortWidth.toString(), viewPortHeight.toString(),
                                                            userAgent, url, loadId, getInjectableScript());

        JSONObject response = new JSONObject();
        response.put("_command", "loadStarted");
        response.put("id", loadId);
        response.put("url", "");

        try {
            session.sendMessage(new TextMessage(response.toString()));
        } catch (IOException e) {
            //Harmless exception
            log.trace("Sending message to socket failed with message: "+e.getMessage());
        }
    }

    /**
     * @param session
     * @param payload
     */
    public void interactPage(WebSocketSession session, JSONObject payload) {
        JSONObject event = payload.getJSONObject("interaction");

        if("mouse".equals(event.getString("category")) && "click".equals(event.getString("type"))) {
            String script = String.format("window.livePortiaPage.sendEvent(%s);", event.toString());
            remoteExecuteJavaScript(session.getId(), script);
        }
    }

    /**
     * @param session
     * @param json
     */
    public void saveHtml(WebSocketSession session, JSONObject json) {
        String commandId = json.getJSONObject("_meta").getString("id");
        String sampleId = json.getString("sample");

        String renderedHtml = remoteGetRenderedHtml(session.getId());
        String originalHtml = remoteGetOriginalHtml(session.getId());
        projectService.addSampleRenderedBody(sampleId, renderedHtml);
        projectService.addSampleOriginalBody(sampleId, originalHtml);

        JSONObject response = new JSONObject();
        response.put("_command", "save_html");
        response.put("id", commandId);
        response.put("ok", true);

        try {
            session.sendMessage(new TextMessage(response.toString()));
        } catch (IOException e) {
            //Harmless exception
            log.trace("Sending message to socket failed with message: "+e.getMessage());
        }
    }

    /**
     * @param session
     * @param json
     */
    public void extractItems(WebSocketSession session, JSONObject json) {
        String sampleId = json.optString("sample");

        JSONObject response = new JSONObject();
        response.put("changed", new JSONArray());
        response.put("changes", new JSONArray());
        response.put("type", "raw");
        response.put("_command", "extract_items");

        String loadedUrl = remoteGetLoadedUrl(session.getId());

        if(StringUtils.isEmpty(sampleId)) {
            String renderedHtml = remoteGetRenderedHtml(session.getId());
            String originalHtml = remoteGetOriginalHtml(session.getId());

            response.put("items", new JSONArray());
            response.put("links", htmlProcessor.extractLinks(loadedUrl, originalHtml, renderedHtml));
        } else {
            String renderedHtml = projectService.getSampleRenderedHtml(sampleId);
            String originalHtml = projectService.getSampleOriginalHtml(sampleId);
            response.put("items", htmlProcessor.extractItems(loadedUrl, sampleId, renderedHtml, originalHtml));
            response.put("links", htmlProcessor.extractLinks(sampleId));
        }

        try {
            session.sendMessage(new TextMessage(response.toString()));
        } catch (IOException e) {
            //Harmless exception
            log.trace("Sending message to socket failed with message: "+e.getMessage());
        }
    }

    /**
     * @param sessionId
     * @param url
     * @param loadId
     * @throws IOException
     */
    public void sendLoadFinished(String sessionId, String url, String loadId) throws IOException {
        WebSocketSession _session = getWebSocketSession(sessionId);

        JSONObject loadFinished = new JSONObject();
        loadFinished.put("_command", "loadFinished");
        loadFinished.put("id", loadId);
        loadFinished.put("url", url);

        _session.sendMessage(new TextMessage(loadFinished.toString()));

        // send metadata
        String renderedHtml = remoteGetRenderedHtml(sessionId);
        String originalHtml = remoteGetOriginalHtml(sessionId);

        JSONObject loadCompleteJson = new JSONObject();
        loadCompleteJson.put("_command", "metadata");
        loadCompleteJson.put("loaded", true);
        loadCompleteJson.put("url", url);
        loadCompleteJson.put("items", new JSONArray());
        loadCompleteJson.put("links", htmlProcessor.extractLinks(url, originalHtml, renderedHtml));

        JSONObject response = new JSONObject();
        response.put("status", 200);
        response.put("headers", new JSONObject());

        loadCompleteJson.put("response", response);
        loadCompleteJson.put("fp", Util.shaHex(URLEncoder.encode(url, "UTF-8")));

        if (_session != null) {
            _session.sendMessage(new TextMessage(loadCompleteJson.toString()));
        } else {
            log.error("Session was not registered");
        }
    }

    /**
     * @param sessionId
     * @param message
     */
    public void sendWebsocketMessage(String sessionId, String message) throws IOException {
        WebSocketSession _session = getWebSocketSession(sessionId);
        if (_session != null) {
            _session.sendMessage(new TextMessage(message));
        } else {
            log.error("Session was not registered");
        }
    }

    private WebSocketSession getWebSocketSession(String sessionId) throws IOException {
        if (!liveSessions.containsKey(sessionId)) {
            throw new IOException("Trying to send message to a non-existent websocket session");
        }

        return liveSessions.get(sessionId);
    }

    private BrowserServer loadBalanceServer(String sessionId) {
        if (stickySessions.containsKey(sessionId)) {
            return stickySessions.get(sessionId);
        }

        BrowserServer lazyest = null;
        int minimumProcesses = Integer.MAX_VALUE;
        for (BrowserServer server : servers) {
            if (server.getActiveProcesses() < minimumProcesses) {
                lazyest = server;
                minimumProcesses = server.getActiveProcesses();
            }
        }

        return lazyest;
    }

    private String remoteGetRenderedHtml(String sessionId) {
        BrowserServer server = loadBalanceServer(sessionId);
        log.debug("Load Balancer chose server: "+server+" for session: "+sessionId);
        return SpongeBrowserClient.getInstance().sendGetRenderedHtmlCommand(server, sessionId);
    }

    private String remoteGetOriginalHtml(String sessionId) {
        BrowserServer server = loadBalanceServer(sessionId);
        log.debug("Load Balancer chose server: "+server+" for session: "+sessionId);
        return SpongeBrowserClient.getInstance().sendGetOriginalHtmlCommand(server, sessionId);
    }

    private String remoteGetLoadedUrl(String sessionId) {
        BrowserServer server = loadBalanceServer(sessionId);
        log.debug("Load Balancer chose server: "+server+" for session: "+sessionId);
        return SpongeBrowserClient.getInstance().sendGetLoadedUrlCommand(server, sessionId);
    }

    private void remoteExecuteJavaScript(String sessionId, String script) {
        BrowserServer server = loadBalanceServer(sessionId);
        log.debug("Load Balancer chose server: "+server+" for session: "+sessionId);

        SpongeBrowserClient.getInstance().sendExecuteJavaScriptCommand(server, sessionId, script);
    }

    private void remoteLoadCommand(String sessionId, String viewPortWidth, String viewPortHeight,
                                   String userAgent, String url, String loadId, String combinedJs) {
        BrowserServer server = loadBalanceServer(sessionId);
        log.debug("Load Balancer chose server: "+server+" for session: "+sessionId);

        String numProcesses = SpongeBrowserClient.getInstance().sendLoadPageCommand(server, sessionId, viewPortWidth,
                                                                     viewPortHeight, userAgent, url, loadId, combinedJs);
        if (!StringUtils.isEmpty(numProcesses)) {
            server.setActiveProcesses(Integer.parseInt(numProcesses));
            log.info("Browser server info updated: " + server);
        }
    }

    private void prepareServers() {
        if (servers != null) {
            return;
        }

        String browserAgents = null;
        try {
            browserAgents = systemConfigurationRepository.findOneByConfigKey(
                    Constants.CONFIG_KEY_BROWSER_AGENTS).map(config -> {
                return config.getConfigValue();
            }).orElseThrow(() -> new SystemConfigurationNotFoundException(
                    "System Configuration: " + Constants.CONFIG_KEY_BROWSER_AGENTS +
                            " was not found in the database"));
            log.info("Sponge browser agents: " + browserAgents);
        } catch(SystemConfigurationNotFoundException e) {
            log.error("Browser servers preparation failed with message: "+e.getMessage());
        }

        if (browserAgents == null) {
            return;
        }

        String[] agents = browserAgents.split("\\,");
        servers = new ArrayList<BrowserServer>();
        String[] agentSplit = null;
        for (String agent : agents) {
            if (StringUtils.isEmpty(agent)) {
                log.warn("Invalid configuration for browser agent: EMPTY string");
                continue;
            }
            if (!agent.contains(":")) {
                log.warn("Invalid configuration for browser agent: Bad Format");
                continue;
            }
            agentSplit = agent.split(":");
            servers.add(new BrowserServer(agentSplit[0], Integer.parseInt(agentSplit[1])));
            log.info("Browser agent configured on host: "+agentSplit[0]+" and port: "+agentSplit[1]);
        }
    }

    private void registerSession(WebSocketSession session) {
        if (liveSessions.containsKey(session.getId())) {
            log.warn("Session: "+session.getId()+" was already registered. Overriding the old one");
        }

        liveSessions.put(session.getId(), session);
    }

    private String getInjectableScript() {
        if (combinedJs == null) {
            try {
                ResourceLoader resourceLoader = applicationContext
                        .getAutowireCapableBeanFactory().createBean(DefaultResourceLoader.class);
                InputStream is = resourceLoader.getResource("classpath:combined.js").getInputStream();
                combinedJs = IOUtils.toString(is, Charset.forName("UTF-8"));
                is.close();
            } catch (IOException e) {
                log.error("Error loading combinedJs with message: "+e.getMessage());
            }
        }

        return combinedJs;
    }

    private void initSessionDestroyer() {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {}

                while (true) {
                    try {
                        synchronized (liveSessions) {
                            Iterator<Map.Entry<String, WebSocketSession>> _iterator
                                                        = liveSessions.entrySet().iterator();
                            WebSocketSession session = null;
                            while (_iterator.hasNext()) {
                                session = _iterator.next().getValue();
                                if (!session.isOpen()) {
                                    log.info("Dead session: " + session.getId());
                                    BrowserServer server = loadBalanceServer(session.getId());
                                    SpongeBrowserClient.getInstance().sendCloseSessionCommand(server, session.getId());
                                    stickySessions.remove(session.getId());
                                    _iterator.remove();
                                }
                            }
                        }
                    } catch (Throwable t) {
                        log.trace("Something unexpected happened in session destroyer but show must go on: "
                                                                                                +t.getMessage());
                        t.printStackTrace();
                    }

                    try {
                        // sleep a bit to avoid busy loop
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {}
                }
            }
        });
    }
}