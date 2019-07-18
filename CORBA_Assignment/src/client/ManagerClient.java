package client;

import EventManagementIDLApp.EventManagementIDL;
import EventManagementIDLApp.EventManagementIDLHelper;
import helper.Constants;
import helper.EventType;
import helper.Role;
import helper.Utils;
import log.MyLogger;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import remoteInvocation.EventManagement;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class ManagerClient implements Runnable{
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    User user;
    Scanner input;
    EventManagementIDL eventObj;

    public ManagerClient(User user) {
        this.user = user;
        input = new Scanner(System.in);
    }

    @Override
    public void run() {
        try {
            setupLogging();
            ORB orb = ORB.init(new String[0],null);
            // -ORBInitialPort 1050 -ORBInitialHost localhost
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            eventObj = EventManagementIDLHelper.narrow(ncRef.resolve_str(user.getCity().toString()));
            performOperations();
        } catch (Exception e) {
            System.out.println("Manager Client exception: " + e);
            e.printStackTrace();
        }
    }

    private void performOperations(){

        int userSelection = displayMenu();
        String customerId, eventId, eventType, newEventId, newEventType;
        EventType ET, newET;
        int eventCapacity = 0;
        AbstractMap.SimpleEntry<Boolean, String> result;
        String[] eventMap;
//        HashMap<String, Integer> eventMap;
        HashMap<String, ArrayList<String>> eventList = null;
        boolean status;
        boolean resultMap;

        while (userSelection != 8) {
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

                    status = eventObj.addEvent(user.toString(), eventId, ET.toString(), eventCapacity);
                    LOGGER.info(String.format(Constants.LOG_MSG, "addEvent",
                            Arrays.asList(user, eventId, ET, eventCapacity), status, Constants.EMPTYSTRING));
                    if (status)
                        System.out.println("SUCCESS - Event Added Successfully");
                    else
                        System.out.println("CAPACITY UPDATED = " + eventId + " is already offered in " + ET + " EventType.");
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

                    status = eventObj.removeEvent(user.toString(), eventId, ET.toString());
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

                    eventMap = eventObj.listEventAvailability(user.toString(), ET.toString());
                    StringBuilder sb = new StringBuilder();
                    sb.append(ET).append(" - ");
                    for (String element: eventMap) {
                        String event = (element.substring(0,element.length() - 1))+" ";
                        String eventC = element.substring(element.length() - 1);
                        sb.append(event);
                        sb.append(eventC);
                        sb.append(",");
                    }
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

                    eventMap = eventObj.bookEvent(customerId, eventId, ET.toString());

                    LOGGER.info(String.format(Constants.LOG_MSG, "bookEvent", Arrays.asList(customerId, eventId, ET),
                            eventMap[0], eventMap[1]));
                    if (Boolean.parseBoolean(eventMap[0]))
                        System.out.println("SUCCESS - " + eventMap[1]);
                    else
                        System.out.println("FAILURE - " + eventMap[1]);
                    break;

                case 5:
                    System.out.print("Enter the Customer ID: ");
                    customerId = input.next().toUpperCase();
                    result = Utils.validateUser(customerId.trim(), Role.CUSTOMER, this.user.getCity());
                    if (!result.getKey()) {
                        System.out.println(result.getValue());
                        break;
                    }
                    eventMap = eventObj.getBookingSchedule(customerId);

                    LOGGER.info(String.format(Constants.LOG_MSG, "getBookingSchedule", Arrays.asList(customerId),
                            eventMap != null, eventMap));
                    if (eventMap != null)
                        for (int i = 0; i < eventMap.length; i++) {
                            System.out.println(eventMap[i]);
                        }
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
                    eventMap = eventObj.cancelEvent(customerId, eventId);
//                    result = eventObj.cancelEvent(user.toString(), eventId);

                    LOGGER.info(String.format(Constants.LOG_MSG, "cancelEvent", Arrays.asList(customerId, eventId),
                            eventMap[0], eventMap[1]));
                    if (Boolean.parseBoolean(eventMap[0]))
                        System.out.println("SUCCESS -" + eventMap[1]);
                    else
                        System.out.println("FAILURE - " + eventMap[1]);

                    break;

                case 7:
                    System.out.print("Enter the Customer ID: ");
                    customerId = input.next().toUpperCase();
                    result = Utils.validateUser(customerId.trim(), Role.CUSTOMER, this.user.getCity());
                    if (!result.getKey()) {
                        System.out.println(result.getValue());
                        break;
                    }
                    System.out.print("Enter the New Event ID: ");
                    newEventId = input.next().toUpperCase();
                    result = Utils.validateEvent(newEventId.trim());
                    if (!result.getKey()) {
                        System.out.println(result.getValue());
                        break;
                    }
                    System.out.print("Enter New EventType(Seminar|Conference|TradeShow): ");
                    newEventType = input.next();
                    result = Utils.validateEventType(newEventType.trim());
                    if (!result.getKey()) {
                        System.out.println(result.getValue());
                        break;
                    } else {
                        newET = EventType.valueOf(newEventType.toUpperCase());
                    }
                    System.out.print("Enter the Old Event ID: ");
                    eventId = input.next().toUpperCase();
                    result = Utils.validateEvent(eventId.trim());
                    if (!result.getKey()) {
                        System.out.println(result.getValue());
                        break;
                    }
                    System.out.print("Enter Old EventType(Seminar|Conference|TradeShow): ");
                    eventType = input.next();
                    result = Utils.validateEventType(eventType.trim());
                    if (!result.getKey()) {
                        System.out.println(result.getValue());
                        break;
                    } else {
                        ET = EventType.valueOf(eventType.toUpperCase());
                    }
                    eventMap = eventObj.swapEvent(customerId, newEventId, newET.toString(), eventId, ET.toString());

                    LOGGER.info(String.format(Constants.LOG_MSG, "swapEvent", Arrays.asList(customerId,newEventId,newET, eventId, ET),
                            eventMap[0], eventMap[1]));
                    if (Boolean.parseBoolean(eventMap[0]))
                        System.out.println("SUCCESS swap -" + eventMap[1]);
                    else
                        System.out.println("FAILURE swap - " + eventMap[1]);

                    break;
                case 8:
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
        System.out.println("|7| Swap Event.");
        System.out.println("|8| Quit.");
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
