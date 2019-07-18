package servers;

import EventManagementIDLApp.EventManagementIDL;
import EventManagementIDLApp.EventManagementIDLHelper;
import EventManagementIDLApp.EventManagementIDLPOA;
import helper.City;
import helper.Constants;
import log.MyLogger;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import remoteInvocation.EventManagement;

import java.io.File;
import java.io.IOException;

public class TOR_Server {
    public static void main(String args[]) {
//        EventManagementIDLPOA stub = new EventManagement("TOR");
        try {
            setupLogging();
            ORB orb = ORB.init(args, null);

            POA rootPOA = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootPOA.the_POAManager().activate();

            EventManagement eventObj = new EventManagement("TOR");
//            EventManagement eventObj = new EventManagement();
            eventObj.setORB(orb);

            org.omg.CORBA.Object ref = rootPOA.servant_to_reference(eventObj);

            EventManagementIDL href = EventManagementIDLHelper.narrow(ref);

            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");

            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
            NameComponent path[] = ncRef.to_name(City.TOR.toString());
            ncRef.rebind(path, href);

            System.out.println("TOR Server ready and waiting ...");

            new Thread(() -> {
//                ((EventManagement) stub).UDPServer();
                ((EventManagement) eventObj).UDPServer();
            }).start();

            // wait for invocations from clients
            for (;;) {
                orb.run();
            }
        }

        catch (Exception e) {
            System.err.println("ERROR: " + e);
            e.printStackTrace(System.out);
        }

        System.out.println("HelloServer Exiting ...");

    }


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
