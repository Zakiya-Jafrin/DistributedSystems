package FE;

import IDL.EventManagementIDL;
import IDL.EventManagementIDLHelper;
import RMTwo.helper.City;
import RMTwo.helper.Constants;
import RMTwo.log.MyLogger;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import java.io.File;
import java.io.IOException;

public class FrontEnd {
    public static void main(String args[]) {
        try {
            setupLogging();
            ORB orb = ORB.init(args, null);

            POA rootPOA = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootPOA.the_POAManager().activate();

//            FrontEndImplementation eventObj = new FrontEndImplementation();
            FrontEndImplementation eventObj = new FrontEndImplementation("FRONTEND");
            eventObj.setORB(orb);

            org.omg.CORBA.Object ref = rootPOA.servant_to_reference(eventObj);

            EventManagementIDL href = EventManagementIDLHelper.narrow(ref);

            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");

            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
            NameComponent path[] = ncRef.to_name(City.FRONTEND.toString());
            ncRef.rebind(path, href);

            System.out.println("FrontEnd Server ready and waiting ...");

//            new Thread(() -> {
//                ((FrontEndImplementation) eventObj).;
//            }).start();

            // wait for invocations from clients
            for (;;) {
                orb.run();
            }
        }
        catch (Exception e) {
            System.err.println("ERROR: " + e);
            e.printStackTrace(System.out);
        }
    }


    private static void setupLogging() throws IOException {
        File files = new File(Constants.SERVER_LOG_DIRECTORY);
        if (!files.exists())
            files.mkdirs();
        files = new File(Constants.SERVER_LOG_DIRECTORY + "FrontEnd.log");
        if (!files.exists())
            files.createNewFile();
        MyLogger.setup(files.getAbsolutePath());
    }

}
