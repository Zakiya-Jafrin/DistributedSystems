package com.web.client;

import com.web.service.InterfaceEventManagement;
import com.web.service.impl.EventManagement;
import helper.Constants;
import helper.EventType;
import helper.Utils;
import log.MyLogger;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.logging.Logger;

public class CustomerClient implements Runnable{
    static InterfaceEventManagement eventObj;
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    User user;
    Scanner input;

//    Service montrealService;
//    Service ottawaService;
//    Service torontoService;

    public CustomerClient(User user) {
        this.user = user;
        input = new Scanner(System.in);
    }

    @Override
    public void run() {
        try {
            setupLogging();
            LOGGER.info("Customer LOGIN(" + user + ")");

//            URL mtlURL = new URL("http://localhost:8080/montreal?wsdl");
//            QName mtlQName = new QName("http://impl.service.web.com/", "ImplementationService");
//            montrealService = Service.create(mtlURL, mtlQName);
//
//            URL otwURL = new URL("http://localhost:8081/ottawa?wsdl");
//            QName otwQName = new QName("http://impl.service.web.com/", "ImplementationService");
//            ottawaService = Service.create(otwURL, otwQName);
//
//            URL torURL = new URL("http://localhost:8082/toronto?wsdl");
//            QName torQName = new QName("http://impl.service.web.com/", "ImplementationService");
//            torontoService = Service.create(torURL, torQName);

            decideServerPort(user.getCity().toString());

            performOperations();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void performOperations() throws MalformedURLException{

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
                    eventMap = eventObj.cancelEvent(user.toString(), eventId);
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

    static InterfaceEventManagement decideServerPort(String cityName) {
//        eventObj = new EventManagement();
        try {
            URL mtlURL = new URL("http://localhost:8080/montreal?wsdl");
            QName mtlQName = new QName("http://impl.service.web.com/", "EventManagementService");
            Service montrealService = Service.create(mtlURL, mtlQName);

            URL otwURL = new URL("http://localhost:8081/ottawa?wsdl");
            QName otwQName = new QName("http://impl.service.web.com/", "EventManagementService");
            Service ottawaService = Service.create(otwURL, otwQName);

            URL torURL = new URL("http://localhost:8082/toronto?wsdl");
            QName torQName = new QName("http://impl.service.web.com/", "EventManagementService");
            Service torontoService = Service.create(torURL, torQName);

            if(cityName.equals("MTL")) {
                eventObj = montrealService.getPort(InterfaceEventManagement.class);
            }else if(cityName.equals("OTW")) {
                eventObj = ottawaService.getPort(InterfaceEventManagement.class);
            }else if(cityName.equals("TOR")) {
                eventObj = torontoService.getPort(InterfaceEventManagement.class);
            }else {
                System.out.println("This is an invalid request. Please check your username");
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        return eventObj;
    }
}
