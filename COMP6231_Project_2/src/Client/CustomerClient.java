package Client;

import IDL.EventManagementIDL;
import IDL.EventManagementIDLHelper;
import RMTwo.helper.Constants;
import RMTwo.helper.EventType;
import RMTwo.helper.Utils;
import RMTwo.log.MyLogger;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Logger;

public class CustomerClient implements Runnable{
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    User user;
    Scanner input;
    EventManagementIDL eventObj;

    public CustomerClient(User user) {
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

            eventObj = EventManagementIDLHelper.narrow(ncRef.resolve_str("FRONTEND"));
//            eventObj = EventManagementIDLHelper.narrow(ncRef.resolve_str(user.getCity().toString()));
            performOperations();
        } catch (Exception e) {
            System.out.println("Customer Client exception: " + e);
            e.printStackTrace();
        }
    }

    private void performOperations() {

        int userSelection = displayMenu();
        String eventId, eventType, newEventId, newEventType;
        EventType ET, newET;
        AbstractMap.SimpleEntry<Boolean, String> result;
        String[] eventMap ;
        boolean resultMap;
        while (userSelection != 5) {

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
                    eventMap = eventObj.bookEvent(user.toString(), eventId, ET.toString());

                    LOGGER.info(String.format(Constants.LOG_MSG, "bookEvent", Arrays.asList(user, eventId, ET),
                            eventMap[0], eventMap[1]));
                    if (Boolean.parseBoolean(eventMap[0]))
                        System.out.println("SUCCESS - " + eventMap[1]);
                    else
                        System.out.println("FAILURE - " + eventMap[1]);

                    break;

                case 2:
                    eventMap = eventObj.getBookingSchedule(user.toString());

                    LOGGER.info(String.format(Constants.LOG_MSG, "getBookingSchedule", Arrays.asList(user),
                            eventMap != null, eventMap));
                    if (eventMap != null)
                        for (int i = 0; i < eventMap.length; i++) {
                            System.out.println(eventMap[i]);
                        }
//                        System.out.println(eventMap);
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
                    System.out.print("Enter EventType(Seminar|Conference|TradeShow): ");
                    eventType = input.next();
                    result = Utils.validateEventType(eventType.trim());
                    if (!result.getKey()) {
                        System.out.println(result.getValue());
                        break;
                    }
                    eventMap = eventObj.cancelEvent(user.toString(), eventId, eventType);
//                    result = eventObj.cancelEvent(user.toString(), eventId);
                    //TODO check eventtype

                    LOGGER.info(String.format(Constants.LOG_MSG, "cancelEvent", Arrays.asList(user, eventId),
                            eventMap[0], eventMap[1]));
                    if (Boolean.parseBoolean(eventMap[0]))
                        System.out.println("SUCCESS -" + eventMap[1]);
                    else
                        System.out.println("FAILURE - " + eventMap[1]);

                    break;
                case 4:
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
                    eventMap = eventObj.swapEvent(user.toString(), newEventId, newET.toString(), eventId, ET.toString());

                    LOGGER.info(String.format(Constants.LOG_MSG, "swapEvent", Arrays.asList(user,newEventId,newET, eventId, ET),
                            eventMap[0], eventMap[1]));
                    if (Boolean.parseBoolean(eventMap[0]))
                        System.out.println("SUCCESS swap-" + eventMap[1]);
                    else
                        System.out.println("FAILURE swap- " + eventMap[1]);

                    break;

                case 5:
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
        System.out.println("|4| Swap Event.");
        System.out.println("|5| Quit.");
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
