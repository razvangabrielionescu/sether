package org.mware.sponge.browser.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface HeartbeatRemote extends Remote {

    /**
     * @throws RemoteException
     */
    public void heartbeat() throws RemoteException;
}
