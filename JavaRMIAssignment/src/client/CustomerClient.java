package client;

import helper.Constants;
import helper.EventType;
import helper.Utils;
import log.MyLogger;
import remoteInvocation.EventManagement;
import remoteInvocation.InterfaceEventManagement;

import java.io.File;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.logging.Logger;

public class CustomerClient implements Runnable{

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    User user;
    Scanner input;
    InterfaceEventManagement stub;

    public CustomerClient(User user) {
        this.user = user;
        input = new Scanner(System.in);
    }

    @Override
    public void run() {
        Registry registry;
        int serverPort = Utils.decideServerPort(user.getCity().toString());
        try {
            setupLogging();
            LOGGER.info("Customer LOGIN(" + user + ")");
            registry = LocateRegistry.getRegistry(serverPort);
            stub = (InterfaceEventManagement) registry.lookup(user.getCity().toString());
//            registry = LocateRegistry.getRegistry(null);
//            stub = (EventManagement) registry.lookup(user.getCity().toString());
            performOperations();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NotBoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void performOperations() throws RemoteException {

        int userSelection = displayMenu();
        String eventId, eventType;
        EventType ET;
        AbstractMap.SimpleEntry<Boolean, String> result;
        while (userSelection != 4) {

            switch (userSelection) {
                case 1:
                    System.out.print("Enter the Event ID: ");
                    eventId = input.next().toUpperCase();
                    result = Utils.validateEvent(eventId.trim());
                    if (!result.getKey()) {
                        System.out.println(result.getValue());
                        break;
                    }
                    System.out.print("Enter EventType(Seminar|Conference|TradeShow): ");
                    eventType = input.next();
                    result = Utils.validateEventType(eventType.trim());
                    if (!result.getKey()) {
                        System.out.println(result.getValue());
                        break;
                    } else {
                        ET = EventType.valueOf(eventType.toUpperCase());
                    }
                    result = stub.bookEvent(user.toString(), eventId, ET.toString());

                    LOGGER.info(String.format(Constants.LOG_MSG, "bookEvent", Arrays.asList(user, eventId, ET),
                            result.getKey(), result.getValue()));
                    if (result.getKey())
                        System.out.println("SUCCESS - '" + eventId +"' Booking Successful");
                    else
                        System.out.println("FAILURE - " + result.getValue());

                    break;

                case 2:
                    HashMap<String, ArrayList<String>> eventList = stub.getBookingSchedule(user.toString());

                    LOGGER.info(String.format(Constants.LOG_MSG, "getBookingSchedule", Arrays.asList(user),
                            eventList != null, eventList));
                    if (eventList != null)
                        System.out.println(eventList);
                    else
                        System.out
                                .println("There was some problem in getting the booking schedule. Please try again later.");

                    break;
                case 3:
                    System.out.print("Enter the Event ID to cancel: ");
                    eventId = input.next().toUpperCase();
                    result = Utils.validateEvent(eventId.trim());
                    if (!result.getKey()) {
                        System.out.println(result.getValue());
                        break;
                    }
                    result = stub.cancelEvent(user.toString(), eventId);

                    LOGGER.info(String.format(Constants.LOG_MSG, "cancelEvent", Arrays.asList(user, eventId),
                            result.getKey(), result.getValue()));
                    if (result.getKey())
                        System.out.println("SUCCESS -" + result.getValue());
                    else
                        System.out.println("FAILURE - " + result.getValue());

                    break;

                case 4:
                    break;
                default:
                    System.out.println("Please select a valid operation.");
                    break;
            }

            System.out.println("\n\n");
            userSelection = displayMenu();
        }
        System.out.println("HAVE A NICE DAY!");
    }

    /**
     * Display menu to the Customer
     *
     * @return
     */
    private int displayMenu() {
        System.out.println("--------------------------------");
        System.out.println("|	Available Operations 	|");
        System.out.println("--------------------------------");
        System.out.println("|1| Book an Event.");
        System.out.println("|2| Get Booking Schedule.");
        System.out.println("|3| Cancel an Event.");
        System.out.println("|4| Quit.");
        System.out.print("Input your operation number : ");
        return input.nextInt();
    }

    /**
     * Configures the logger
     *
     * @throws IOException
     */
    private void setupLogging() throws IOException {
        File files = new File(Constants.CUSTOMER_LOG_DIRECTORY);
        if (!files.exists())
            files.mkdirs();
        files = new File(Constants.CUSTOMER_LOG_DIRECTORY + user + ".log");
        if (!files.exists())
            files.createNewFile();
        MyLogger.setup(files.getAbsolutePath());
    }
}
