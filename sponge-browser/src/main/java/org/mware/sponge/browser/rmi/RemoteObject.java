package org.mware.sponge.browser.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

class RemoteObject extends UnicastRemoteObject {

    /**
     * @throws RemoteException
     */
    protected RemoteObject() throws RemoteException {
        super(SpongeBrowserDriverServer.childPort(),
                SpongeBrowserDriverServer.socketFactory(), SpongeBrowserDriverServer.socketFactory());
    }
}
