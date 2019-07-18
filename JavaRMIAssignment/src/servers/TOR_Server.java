package servers;

import helper.City;
import helper.Constants;
import log.MyLogger;
import remoteInvocation.EventManagement;
import remoteInvocation.InterfaceEventManagement;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Logger;

public class TOR_Server {
    public static void main(String[] args) throws RemoteException {

        InterfaceEventManagement stub = new EventManagement("TOR");
        try {
            setupLogging();
            Registry registry = LocateRegistry.createRegistry(2019);
            registry.bind(City.TOR.toString(), stub);
            // bind the remote object in the registry
//            Registry registry = LocateRegistry.getRegistry();
//            registry.rebind(City.TOR.toString(), stub);

        } catch (Exception e) {
            // TODO - catch only the specific exception
            e.printStackTrace();
        }

        // start the city UDP server for inter-city communication
        // the UDP server is started on a new thread
        new Thread(() -> {
            ((EventManagement) stub).UDPServer();
        }).start();

    }

    /**
     * Logging setup for COMP server
     *
     * @throws IOException
     */
    private static void setupLogging() throws IOException {
        File files = new File(Constants.SERVER_LOG_DIRECTORY);
        if (!files.exists())
            files.mkdirs();
        files = new File(Constants.SERVER_LOG_DIRECTORY + "TOR_Server.log");
        if (!files.exists())
            files.createNewFile();
        MyLogger.setup(files.getAbsolutePath());
    }
}
