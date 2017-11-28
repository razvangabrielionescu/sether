package org.mware.sponge.browser.jfx;

import com.sun.javafx.webkit.Accessor;
import com.sun.webkit.WebPage;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

public class WebUiJavaFxApp extends Application {
    private static final int HISTORY_SIZE = 8;
    private static final Object lock = new Object();
    private static Stage myStage;
    private static WebView myView;
    private int width;
    private int height;
    private String userAgent;

    public WebUiJavaFxApp() {
    }

    static Stage getStage() {
        synchronized (lock) {
            while (myStage == null) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {}
            }
            return myStage;
        }
    }

    static WebView getView() {
        synchronized (lock) {
            while (myView == null) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {}
            }
            return myView;
        }
    }

    @Override
    public void init() throws Exception {
        List<String> params = getParameters().getRaw();
        width = Integer.parseInt(params.get(0));
        height = Integer.parseInt(params.get(1));
        userAgent = params.get(2);
    }

    @Override
    public void start(Stage stage) throws Exception {
        System.setProperty("headless.geometry", width + "x" + height);
        if (stage == null) {
            stage = new Stage();
        }

        Platform.setImplicitExit(true);

        WebView view = new WebView();
        view.setCache(false);
        StackPane root = new StackPane();
        root.setCache(false);
        stage.initStyle(StageStyle.UNDECORATED);
        WebEngine engine = view.getEngine();
        File style = File.createTempFile("jbd_style_", ".css");
        style.deleteOnExit();
        Files.write(style.toPath(),
                "body::-webkit-scrollbar {width: 0px !important;height:0px !important;}".getBytes("utf-8"));
        engine.setUserStyleSheetLocation(style.toPath().toUri().toURL().toExternalForm());
        engine.getHistory().setMaxSize(HISTORY_SIZE);

        WebPage page = Accessor.getPageFor(engine);
        page.setDeveloperExtrasEnabled(false);
        page.setUsePageCache(false);
        page.setUserAgent(userAgent);

        root.getChildren().add(view);
        stage.setScene(new Scene(root, width, height));
        stage.sizeToScene();
        stage.show();
        synchronized (lock) {
            myStage = stage;
            myView = view;
            lock.notifyAll();
        }
    }
}

