package org.mware.sponge.browser;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import org.apache.commons.lang.StringUtils;
import org.mware.sponge.browser.process.PortGroup;
import org.mware.sponge.browser.process.SocketFactory;
import org.mware.sponge.browser.process.SocketLock;
import org.mware.sponge.browser.rmi.*;
import org.mware.sponge.browser.util.Constants;
import org.mware.sponge.browser.util.FileRemover;
import org.mware.sponge.browser.util.PageCache;
import org.mware.sponge.browser.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.listener.ProcessListener;
import org.zeroturnaround.process.PidProcess;
import org.zeroturnaround.process.Processes;
import org.zeroturnaround.exec.stream.LogOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by Dan on 10/2/2017.
 */
@SuppressWarnings("ALL")
public class SpongeBrowserDriver {
    private static final Logger log = LoggerFactory.getLogger(SpongeBrowserDriver.class);

    static {
        File javaBin = new File(System.getProperty("process.java.home") + "/bin/java");
        if (!javaBin.exists()) {
            javaBin = new File(javaBin.getAbsolutePath() + ".exe");
        }
        JAVA_BIN = javaBin.getAbsolutePath();
    }

    private static volatile List<String> classpathSimpleArgs;
    private static volatile List<String> classpathUnpackedArgs;
    private static final AtomicReference<List<String>> classpathArgs = new AtomicReference<>();
    private static final AtomicBoolean firstLaunch = new AtomicBoolean(true);
    private static final AtomicInteger runningInstances = new AtomicInteger(0);
    private static final AtomicLong sessionIdCounter = new AtomicLong();
    private static final Set<SocketLock> locks = new HashSet<SocketLock>();
    private static final String JAVA_BIN;

    private SpongeBrowserDriverRemote remote;

    private final AtomicReference<Process> process = new AtomicReference<Process>();
    private final AtomicBoolean processEnded = new AtomicBoolean();
    private final AtomicReference<PortGroup> portGroup = new AtomicReference<PortGroup>();
    private SocketLock lock = new SocketLock();
    private Thread heartbeatThread;
    private String parentHost;
    private PageCache pageCache;

    /**
     * @param parentHost
     */
    public SpongeBrowserDriver(String parentHost) {
        this.parentHost = parentHost;

        synchronized (locks) {
            locks.add(lock);
        }

        synchronized (firstLaunch) {
            if (firstLaunch.compareAndSet(true, false)) {
                initClasspath();
                classpathArgs.set(classpathUnpackedArgs);
                launchProcess();
                if (portGroup.get() == null) {
                    classpathArgs.set(classpathSimpleArgs);
                }
            }
        }

        initRemoteProcess();
    }

    private void initRemoteProcess() {
        if (portGroup.get() == null) {
            launchProcess();
        }

        if (portGroup.get() == null) {
            endProcess();
        }
        HeartbeatRemote heartbeatTmp = null;
        SpongeBrowserDriverRemote instanceTmp = null;
        try {
            synchronized (lock.validated()) {
                Registry registry = LocateRegistry
                        .getRegistry(Constants.RMI_HOST, (int)portGroup.get().getChild(),
                                new SocketFactory(Constants.RMI_HOST, portGroup.get(), locks));
                heartbeatTmp = (HeartbeatRemote) registry.lookup("HeartbeatRemote");
                instanceTmp = (SpongeBrowserDriverRemote) registry.lookup("SpongeBrowserDriverRemote");
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        final HeartbeatRemote heartbeat = heartbeatTmp;
        heartbeatThread = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    if (processEnded.get()) {
                        return;
                    }
                    try {
                        heartbeat.heartbeat();
                    } catch (RemoteException e) {}
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {}
                }
            }
        });
        heartbeatThread.setName("Heartbeat");
        heartbeatThread.start();
        remote = instanceTmp;
    }

    private void relaunch(boolean withPageLoad) {
        portGroup.set(null);
        lock = new SocketLock();
        initRemoteProcess();

        if (withPageLoad &&
                pageCache != null && remote != null) {
            try {
                remote.loadPage(pageCache.getSessionId(), pageCache.getViewPortWidth(), pageCache.getViewPortHeight(),
                        pageCache.getUserAgent(), pageCache.getUrl(), pageCache.getLoadId(), pageCache.getCombinedJs());
            } catch (RemoteException e) {
                log.error("Error sending command to browser process: "+e.getMessage());
            }
        }
    }

    private static void initClasspath() {
        List<String> classpathSimpleTmp = new ArrayList<String>();
        List<String> classpathUnpackedTmp = new ArrayList<String>();
        try {
            List<File> classpathElements = new FastClasspathScanner().getUniqueClasspathElements();
            final File classpathDir = Files.createTempDirectory("jbd_classpath_").toFile();
            Runtime.getRuntime().addShutdownHook(new FileRemover(classpathDir));
            List<String> pathsSimple = new ArrayList<String>();
            List<String> pathsUnpacked = new ArrayList<String>();
            for (File curElement : classpathElements) {
                String rootLevelElement = curElement.getAbsoluteFile().toURI().toURL().toExternalForm();
                pathsSimple.add(rootLevelElement);
                pathsUnpacked.add(rootLevelElement);
                if (curElement.isFile() && curElement.getPath().endsWith(".jar")) {
                    try (ZipFile jar = new ZipFile(curElement)) {
                        Enumeration<? extends ZipEntry> entries = jar.entries();
                        while (entries.hasMoreElements()) {
                            ZipEntry entry = entries.nextElement();
                            if (entry.getName().endsWith(".jar")) {
                                try (InputStream in = jar.getInputStream(entry)) {
                                    File childJar = new File(classpathDir,
                                            Util.randomFileName() + ".jar");
                                    Files.copy(in, childJar.toPath());
                                    pathsUnpacked.add(childJar.getAbsoluteFile().toURI().toURL().toExternalForm());
                                    childJar.deleteOnExit();
                                }
                            }
                        }
                    }
                }
            }
            classpathSimpleTmp = createClasspathJar(classpathDir, "classpath-simple.jar", pathsSimple);
            classpathUnpackedTmp = createClasspathJar(classpathDir, "classpath-unpacked.jar", pathsUnpacked);
        } catch (Throwable t) {
           t.printStackTrace();
        }
        classpathSimpleArgs = Collections.unmodifiableList(classpathSimpleTmp);
        classpathUnpackedArgs = Collections.unmodifiableList(classpathUnpackedTmp);
    }

    private static List<String> createClasspathJar(File dir, String jarName,
                                                   List<String> manifestClasspath) throws IOException {
        List<String> classpathArgs = new ArrayList<String>();
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        manifest.getMainAttributes().put(Attributes.Name.CLASS_PATH,
                StringUtils.join(manifestClasspath, ' '));
        File classpathJar = new File(dir, jarName);
        classpathJar.deleteOnExit();
        try (JarOutputStream stream = new JarOutputStream(
                new FileOutputStream(classpathJar), manifest)) {}
        classpathArgs.add("-classpath");
        classpathArgs.add(classpathJar.getCanonicalPath());
        return classpathArgs;
    }

    private String launchProcess() {
        final AtomicBoolean ready = new AtomicBoolean();
        final AtomicReference<String> logPrefix = new AtomicReference<String>("");
        new Thread(new Runnable() {
            public void run() {
                List<String> myArgs = new ArrayList<String>();
                myArgs.add(JAVA_BIN);
                myArgs.addAll(classpathArgs.get());
                myArgs.add("-Djava.rmi.server.hostname="+Constants.RMI_HOST);
                myArgs.add("-Dglass.platform=Monocle");
                myArgs.add("-Dmonocle.platform=Headless");
                if (!System.getProperty("os.name").contains("Windows")) {
                    myArgs.add("-Dprism.order=sw");
                }

                myArgs.add(SpongeBrowserDriverServer.class.getName());
                myArgs.add(getParentHost());

                try {
                    new ProcessExecutor()
                            .addListener(new ProcessListener() {
                                @Override
                                public void afterStart(Process proc, ProcessExecutor executor) {
                                    process.set(proc);
                                }
                            })
                            .redirectOutput(new LogOutputStream() {
                                boolean done = false;

                                protected void processLine(String line) {
                                    if (!StringUtils.isEmpty(line)) {
                                        if (!done) {
                                            synchronized (ready) {
                                                if (line.startsWith(Constants.RMI_PORTS_MESSAGE)) {
                                                    String[] parts = line.substring(Constants.RMI_PORTS_MESSAGE.length()).split("/");
                                                    portGroup.set(
                                                            new PortGroup(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2])));
                                                    logPrefix.set(new StringBuilder()
                                                            .append("[Instance ")
                                                            .append(sessionIdCounter.incrementAndGet())
                                                            .append("][Port ")
                                                            .append(portGroup.get().getChild())
                                                            .append("]")
                                                            .toString());
                                                    ready.set(true);
                                                    ready.notifyAll();
                                                    done = true;
                                                } else {
                                                    log.debug(logPrefix.get()+" "+line);
                                                }
                                            }
                                        } else {
                                            log.debug(logPrefix.get()+" "+line);
                                        }
                                    }
                                }
                            })
                            .redirectError(new LogOutputStream() {
                                protected void processLine(String line) {
                                    log.debug(logPrefix.get()+" "+line);
                                }
                            })
                            .destroyOnExit()
                            .command(myArgs).execute();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                synchronized (ready) {
                    ready.set(true);
                    ready.notifyAll();
                }
            }
        }).start();
        synchronized (ready) {
            while (!ready.get()) {
                try {
                    ready.wait();
                    break;
                } catch (InterruptedException e) {}
            }
        }
        return logPrefix.get();
    }

    private void endProcess() {
        if (processEnded.compareAndSet(false, true)) {
            runningInstances.decrementAndGet();
            lock.getExpired().set(true);
            final Process proc = process.get();
            if (proc != null) {
                while (proc.isAlive()) {
                    try {
                        PidProcess pidProcess = Processes.newPidProcess(proc);
                        try {
                            if (!pidProcess.destroyGracefully().waitFor(10, TimeUnit.SECONDS)) {
                                throw new RuntimeException();
                            }
                        } catch (Throwable t1) {
                            if (!pidProcess.destroyForcefully().waitFor(10, TimeUnit.SECONDS)) {
                                throw new RuntimeException();
                            }
                        }
                    } catch (Throwable t2) {
                        try {
                            proc.destroyForcibly().waitFor(10, TimeUnit.SECONDS);
                        } catch (Throwable t3) {}
                    }
                }
            }
            try {
                heartbeatThread.interrupt();
                heartbeatThread.join();
            } catch (Exception e) {}

            synchronized (locks) {
                locks.remove(lock);
            }
        }
    }

    /**
     *
     */
    public void quit() {
        try {
            synchronized (lock.validated()) {
                remote.quit();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            endProcess();
        }
    }

    /**
     * @param sessionId
     * @param viewPortWidth
     * @param viewPortHeight
     * @param userAgent
     * @param url
     * @param loadId
     * @param combinedJs
     */
    public synchronized void loadPage(String sessionId, Integer viewPortWidth, Integer viewPortHeight,
                                        String userAgent, String url, String loadId, String combinedJs) {
        if (remote == null) {
            log.error("FATAL: Remote RMI server was not created");
            return;
        }

        pageCache = new PageCache(sessionId, viewPortWidth, viewPortHeight, userAgent, url, loadId, combinedJs);
        try {
            remote.loadPage(sessionId, viewPortWidth, viewPortHeight, userAgent, url, loadId, combinedJs);
        } catch (RemoteException e) {
            log.error("Error sending command to browser process: "+e.getMessage());
            relaunch(false);
        }
    }

    /**
     * @param script
     */
    public synchronized void executeJavaScript(String script) {
        if (remote == null) {
            log.error("FATAL: Remote RMI server was not created");
            return;
        }

        try {
            remote.executeJS(script);
        } catch (RemoteException e) {
            log.error("Error sending command to browser process: "+e.getMessage());
            relaunch(true);
        }
    }

    public String getRenderedHtml() {
        if (remote == null) {
            log.error("FATAL: Remote RMI server was not created");
            return null;
        }

        try {
            return remote.getRenderedHtml();
        } catch (RemoteException e) {
            log.error("Error sending command to browser process: "+e.getMessage());
            relaunch(true);
        }

        return null;
    }

    public String getOriginalHtml() {
        if (remote == null) {
            log.error("FATAL: Remote RMI server was not created");
            return null;
        }

        try {
            return remote.getOriginalHtml();
        } catch (RemoteException e) {
            log.error("Error sending command to browser process: "+e.getMessage());
            relaunch(true);
        }

        return null;
    }

    public String getLoadedUrl() {
        if (remote == null) {
            log.error("FATAL: Remote RMI server was not created");
            return null;
        }

        try {
            return remote.getLoadedUrl();
        } catch (RemoteException e) {
            log.error("Error sending command to browser process: "+e.getMessage());
            relaunch(true);
        }

        return null;
    }

    /**
     * @return
     */
    public int getNumberOfActiveJobs() {
        return this.runningInstances.get();
    }

    /**
     * @return
     */
    public int incrementAndGetNumberOfActiveJobs() {
        return this.runningInstances.incrementAndGet();
    }

    /**
     *
     */
    public void kill() {
        endProcess();
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            super.finalize();
        } catch (Throwable t) {}
    }

    /**
     * @return
     */
    public String getParentHost() {
        return parentHost;
    }

    /**
     * @param parentHost
     */
    public void setParentHost(String parentHost) {
        this.parentHost = parentHost;
    }
}
