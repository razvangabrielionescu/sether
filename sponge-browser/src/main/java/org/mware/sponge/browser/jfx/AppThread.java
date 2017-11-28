package org.mware.sponge.browser.jfx;

import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class AppThread {
    private static final Logger log = LoggerFactory.getLogger(AppThread.class);

    private static final Random rand = new Random();

    static interface Sync<T> {
        T perform();
    }

    private static class Runner<T> implements Runnable {
        private final Sync<T> action;
        private final AtomicBoolean done;
        private final AtomicReference<T> returned;
        private final AtomicBoolean cancel;
        private final AtomicReference<Throwable> failure;

        public Runner(Sync<T> action) {
            this.action = action;
            this.done = new AtomicBoolean();
            this.returned = new AtomicReference<T>();
            this.cancel = new AtomicBoolean();
            this.failure = new AtomicReference();
        }

        public Runner(Runner other) {
            this.action = other.action;
            this.done = other.done;
            this.returned = other.returned;
            this.cancel = other.cancel;
            this.failure = other.failure;
        }

        public void run() {
            if (!cancel.get()) {
                T result = null;
                try {
                    result = action.perform();
                } catch (Throwable t) {
                    failure.set(t);
                } finally {
                    synchronized (done) {
                        returned.set(result);
                        done.set(true);
                        done.notifyAll();
                    }
                }
            }
        }
    }

    private static void pause() {
        AppThread.exec(new Sync<Object>() {
            public Object perform() {
                try {
                    Thread.sleep(30 + rand.nextInt(40));
                } catch (Throwable t) {}
                return null;
            }
        });
    }


    static <T> T exec(final Sync<T> action) {
        return exec(false, 0, action);
    }

    static <T> T exec(final boolean pauseAfterExec, final Sync<T> action) {
        return exec(pauseAfterExec, 0, action);
    }

    static <T> T exec(final long timeout, final Sync<T> action) {
        return exec(false, timeout, action);
    }

    static <T> T exec(final boolean pauseAfterExec, final long timeout,
                      final Sync<T> action) {
        try {
            if (Platform.isFxApplicationThread()) {
                return action.perform();
            }
            final Runner<T> runner = new Runner<T>(action);
            synchronized (runner.done) {
                Platform.runLater(runner);
            }
            synchronized (runner.done) {
                if (!runner.done.get()) {
                    try {
                        runner.done.wait(timeout);
                    } catch (InterruptedException e) {
                        log.error(e.getMessage());
                    }
                    if (!runner.done.get()) {
                        runner.cancel.set(true);
                        throw new WebUiBrowserException(new StringBuilder()
                                .append("Timeout of ").append(timeout).append("ms reached.").toString());
                    }
                }
                return runner.returned.get();
            }
        } finally {
            if (pauseAfterExec) {
                pause();
            }
        }
    }
}
