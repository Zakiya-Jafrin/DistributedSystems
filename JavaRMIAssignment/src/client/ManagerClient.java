package client;

import helper.Constants;
import helper.EventType;
import helper.Role;
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

public class ManagerClient implements Runnable{

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    User user;
    Scanner input;
    InterfaceEventManagement stub;

    public ManagerClient(User user) {
        this.user = user;
        input = new Scanner(System.in);
    }

    @Override
    public void run() {
        Registry registry;
        int serverPort = Utils.decideServerPort(user.getCity().toString());
        try {
            setupLogging();
            LOGGER.info("MANAGER LOGIN(" + user + ")");
            registry = LocateRegistry.getRegistry(serverPort);
            stub = (InterfaceEventManagement) registry.lookup(user.getCity().toString());
            performOperations();
        } catch (RemoteException e) {
            LOGGER.severe("RemoteException Exception : " + e.getMessage());
            e.printStackTrace();
        } catch (NotBoundException e) {
            LOGGER.severe("NotBoundException Exception : " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            LOGGER.severe("IO Exception : " + e.getMessage());
            e.printStackTrace();
        }

    }

    private void performOperations() throws RemoteException {

        int userSelection = displayMenu();
        String customerId, eventId, eventType;
        EventType ET;
        int eventCapacity = 0;
        AbstractMap.SimpleEntry<Boolean, String> result;
        HashMap<String, Integer> eventMap;
        HashMap<String, ArrayList<String>> eventList;
        boolean status;

		/*
		 * Executes the loop until the manager quits the application i.e. presses 7
		 *
		 */
        while (userSelection != 7) {
            switch (userSelection) {
                case 1:
                    System.out.print("Enter the event ID : ");
                    eventId = input.next().toUpperCase();
                    result = Utils.validateEvent(eventId.trim(), this.user.getCity());
                    if (!result.getKey()) {
                        System.out.println(result.getValue());
                        break;
                    }

                    System.out.print("Event Capacity : ");
                    eventCapacity = input.nextInt();
                    if (eventCapacity < 1) {
                        System.out.println("Event Capacity needs to be atleast 1.");
                        break;
                    }

                    System.out.print("Enter the EventType for the event(Seminar|Conference|TradeShow) : ");
                    eventType = input.next();
                    result = Utils.validateEventType(eventType.trim());
                    if (!result.getKey()) {
                        System.out.println(result.getValue());
                        break;
                    } else {
                        ET = EventType.valueOf(eventType.toUpperCase());
                    }

                    status = stub.addEvent(user.toString(), eventId, ET.toString(), eventCapacity);
                    LOGGER.info(String.format(Constants.LOG_MSG, "addEvent",
                            Arrays.asList(user, eventId, ET, eventCapacity), status, Constants.EMPTYSTRING));
                    if (status)
                        System.out.println("SUCCESS - Event Added Successfully");
                    else
                        System.out.println("FAILURE = " + eventId + " is already offered in " + ET + " EventType.");
                    break;

                case 2:
                    System.out.print("Enter the event ID : ");
                    eventId = input.next().toUpperCase();
                    result = Utils.validateEvent(eventId.trim(), this.user.getCity());
                    if (!result.getKey()) {
                        System.out.println(result.getValue());
                        break;
                    }

                    System.out.print("Enter the EventType for the event(Seminar|Conference|TradeShow) : ");
                    eventType = input.next();
                    result = Utils.validateEventType(eventType.trim());
                    if (!result.getKey()) {
                        System.out.println(result.getValue());
                        break;
                    } else {
                        ET = EventType.valueOf(eventType.toUpperCase());
                    }

                    status = stub.removeEvent(user.toString(), eventId, ET.toString());
                    LOGGER.info(String.format(Constants.LOG_MSG, "removeEvent", Arrays.asList(user, eventId, ET), status,
                            Constants.EMPTYSTRING));
                    if (status)
                        System.out.println("SUCCESS - " + eventId + " removed successfully for " + ET + " EventType.");
                    else
                        System.out.println("FAILURE - " + eventId + " is not offered in  " + ET + " EventType.");
                    break;

                case 3:
                    System.out.print("Enter the EventType for event(Seminar|Conference|TradeShow) : ");
                    eventType = input.next();
                    result = Utils.validateEventType(eventType.trim());
                    if (!result.getKey()) {
                        System.out.println(result.getValue());
                        break;
                    } else {
                        ET = EventType.valueOf(eventType.toUpperCase());
                    }

                    eventMap = stub.listEventAvailability(user.toString(), ET.toString());
                    StringBuilder sb = new StringBuilder();
                    sb.append(ET).append(" - ");
                    eventMap.forEach((k, v) -> sb.append(k).append(" ").append(v).append(", "));
                    if (eventMap.size() > 0)
                        sb.replace(sb.length() - 2, sb.length() - 1, ".");

                    LOGGER.info(String.format(Constants.LOG_MSG, "listEventAvailability", Arrays.asList(user, ET),
                            eventMap != null, eventMap));
                    if (eventMap != null)
                        System.out.println(sb);
                    else
                        System.out.println("There was some problem in getting the event schedule. Please try again later.");

                    break;
                case 4:
                    System.out.print("Enter the Customer ID: ");
                    customerId = input.next().toUpperCase();
                    result = Utils.validateUser(customerId.trim(), Role.CUSTOMER, this.user.getCity());
                    if (!result.getKey()) {
                        System.out.println(result.getValue());
                        break;
                    }
                    System.out.print("Enter the Event ID: ");
                    eventId = input.next().toUpperCase();
                    result = Utils.validateEvent(eventId.trim());
                    if (!result.getKey()) {
                        System.out.println(result.getValue());
                        break;
                    }
                    System.out.print("Enter EventType(Seminar|Conference|TradeShow) : ");
                    eventType = input.next();
                    result = Utils.validateEventType(eventType.trim());
                    if (!result.getKey()) {
                        System.out.println(result.getValue());
                        break;
                    } else {
                        ET = EventType.valueOf(eventType.toUpperCase());
                    }

                    result = stub.bookEvent(customerId, eventId, ET.toString());

                    LOGGER.info(String.format(Constants.LOG_MSG, "bookEvent", Arrays.asList(customerId, eventId, ET),
                            result.getKey(), result.getValue()));
                    if (result.getKey())
                        System.out.println("SUCCESS - " + customerId + " successfully booked in " + eventId + ".");
                    else
                        System.out.println("FAILURE - " + result.getValue());

                    break;

                case 5:
                    System.out.print("Enter the Customer ID: ");
                    customerId = input.next().toUpperCase();
                    result = Utils.validateUser(customerId.trim(), Role.CUSTOMER, this.user.getCity());
                    if (!result.getKey()) {
                        System.out.println(result.getValue());
                        break;
                    }

                    eventList = stub.getBookingSchedule(customerId);

                    LOGGER.info(String.format(Constants.LOG_MSG, "getBookingSchedule", Arrays.asList(customerId),
                            eventList != null, eventList));
                    if (eventList != null)
                        System.out.println(eventList);
                    else
                        System.out.println("There was some problem in getting the event schedule. Please try again later.");

                    break;
                case 6:
                    System.out.print("Enter the Customer ID: ");
                    customerId = input.next().toUpperCase();
                    result = Utils.validateUser(customerId.trim(), Role.CUSTOMER, this.user.getCity());
                    if (!result.getKey()) {
                        System.out.println(result.getValue());
                        break;
                    }
                    System.out.print("Enter the Event ID to cancel: ");
                    eventId = input.next().toUpperCase();
                    result = Utils.validateEvent(eventId.trim(), null);
                    if (!result.getKey()) {
                        System.out.println(result.getValue());
                        break;
                    }
                    result = stub.cancelEvent(customerId, eventId);

                    LOGGER.info(String.format(Constants.LOG_MSG, "cancelEvent", Arrays.asList(customerId, eventId),
                            result.getKey(), result.getValue()));
                    if (result.getKey())
                        System.out.println("SUCCESS - Event successfully cancelled for " + customerId + ".");
                    else
                        System.out.println("FAILURE - " + result.getValue());

                    break;
                case 7:
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
     * Display menu to the logged in Manager
     *
     * @return
     */
    private int displayMenu() {
        System.out.println("--------------------------------");
        System.out.println("|	Available Operations 	|");
        System.out.println("--------------------------------");
        System.out.println("|1| Add an Event.");
        System.out.println("|2| Remove an Event.");
        System.out.println("|3| List Event Availability.");
        System.out.println("|4| Book an Event.");
        System.out.println("|5| Get Booking Schedule.");
        System.out.println("|6| Cancel an Event.");
        System.out.println("|7| Quit.");
        System.out.print("Input your operation number : ");

        return input.nextInt();
    }

    /**
     * Configures the logger
     *
     * @throws IOException
     */
    private void setupLogging() throws IOException {
        File files = new File(Constants.MANAGER_LOG_DIRECTORY);
        if (!files.exists())
            files.mkdirs();
        files = new File(Constants.MANAGER_LOG_DIRECTORY + user + ".log");
        if (!files.exists())
            files.createNewFile();
        MyLogger.setup(files.getAbsolutePath());
    }
}
