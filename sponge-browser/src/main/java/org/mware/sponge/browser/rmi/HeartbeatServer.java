package org.mware.sponge.browser.rmi;

import java.rmi.RemoteException;
import java.util.concurrent.Executors;

public class HeartbeatServer extends RemoteObject implements HeartbeatRemote {

    private volatile long lastHeartbeat;

    /**
     * @throws RemoteException
     */
    public HeartbeatServer() throws RemoteException {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            public void run() {
                try {
                    // give 60 seconds for initial heartbeat
                    Thread.sleep(60000);
                } catch (InterruptedException e) {}

                while (true) {
                    if (System.currentTimeMillis() - lastHeartbeat > 60000) {
                        // no heartbeat received in the last 60 seconds
                        System.exit(1);
                    }

                    try {
                        // sleep a bit to avoid busy loop
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {}
                }
            }
        });
    }

    /**
     * @throws RemoteException
     */
    public void heartbeat() throws RemoteException {
        lastHeartbeat = System.currentTimeMillis();
    }
}
