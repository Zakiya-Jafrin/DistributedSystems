package RMTwo.remoteInvocation;

import IDL.EventManagementIDLPOA;
import RMTwo.servers.RMTwo;
import com.sun.xml.internal.bind.v2.TODO;
import RMTwo.helper.City;
import RMTwo.helper.Constants;
import RMTwo.helper.EventType;
import RMTwo.helper.Utils;
import org.omg.CORBA.ORB;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.logging.Logger;

@SuppressWarnings("Duplicates")
public class EventManagement {

    private ORB orb;
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private City City;
    private RMTwo rm;
    private int n;

    // in-memory database
    public static HashMap<String, HashMap<String, HashMap<String, Object>>> cityDatabase;

    public EventManagement(){
        super();
    }
    EventManagement eventManagement;

    public EventManagement(String cityName){
        this.City = City.valueOf(cityName);
        cityDatabase = new HashMap<>();
//        cityDatabase.put("SEMINAR", new HashMap<String, HashMap<String, Object>>());
//        cityDatabase.put("CONFERENCE", new HashMap<String, HashMap<String, Object>>());
//        cityDatabase.put("TRADESHOW", new HashMap<String, HashMap<String, Object>>());
    }

    public boolean addEvent(String managerID, String eventID, String eventType, int bookingCapacity){
        boolean status = false;
        String msg = Constants.EMPTYSTRING;
        if (cityDatabase.containsKey(eventType)) {
            HashMap<String, HashMap<String, Object>> events = cityDatabase.get(eventType);

            if (events.containsKey(eventID)) {
                events.get(eventID).put(Constants.CAPACITY,bookingCapacity);
                status = false;
                msg = "Event already exists for " + eventType + " eventType.";
            } else {
                synchronized (this) {
                    HashMap<String, Object> eventDetails = new HashMap<>();
                    eventDetails.put(Constants.CAPACITY, bookingCapacity);
                    eventDetails.put(Constants.CUSTOMERS_ENROLLED, 0);
                    eventDetails.put(Constants.CUSTOMER_IDS, new HashSet<String>());
                    events.put(eventID, eventDetails);
                }
                status = true;
                msg = eventID + " Added.";
            }

        } else {
            // eventType doesn't exists
            HashMap<String, Object> eventDetails = new HashMap<>();
            eventDetails.put(Constants.CAPACITY, bookingCapacity);
            eventDetails.put(Constants.CUSTOMERS_ENROLLED, 0);
            eventDetails.put(Constants.CUSTOMER_IDS, new HashSet<String>());
            HashMap<String, HashMap<String, Object>> events = new HashMap<>();
            events.put(eventID, eventDetails);

            // synchronizing the write operation to the in-memory database
            synchronized (this) {
                this.cityDatabase.put(eventType, events);
            }
            status = true;
            msg = eventID + " Added.";
        }
//
//        LOGGER.info(String.format(Constants.LOG_MSG, Constants.OP_ADD_EVENT,
//                Arrays.asList(managerID, eventID, eventType, bookingCapacity), status, msg));

        return status;
    }

    public boolean removeEvent(String managerID, String eventID, String eventType){
        boolean status = false;
        String msg = Constants.EMPTYSTRING;
        if (cityDatabase.containsKey(eventType)) {
            HashMap<String, HashMap<String, Object>> events = cityDatabase.get(eventType);

            if (events.containsKey(eventID)) {
                synchronized (this) {
                    events.remove(eventID);
                }
                status = true;
                msg = eventID + " removed";
            } else {
                status = false;
                msg = eventType + " eventType doesn't have this event yet.";
            }
        } else {
            status = false;
            msg = eventType + " eventType doesn't have any event yet.";
        }

//        LOGGER.info(String.format(Constants.LOG_MSG, Constants.OP_REMOVE_EVENT,
//                Arrays.asList(managerID, eventID, eventType), status, msg));

        return status;
    }

    public String[] listEventAvailability(String managerId, String eventType){

        ArrayList<String> result = new ArrayList<String>();
        n = 0;
        HashMap<String, Integer> resultMap = new HashMap<>();
        resultMap.putAll(listEventAvailabilityMap(managerId,eventType));

        try {
            for (String key: resultMap.keySet()){
                result.add(key + resultMap.get(key));
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }

//        LOGGER.info(String.format(Constants.LOG_MSG, Constants.OP_LIST_EVENT_AVAILABILITY,
//                Arrays.asList(managerId, eventType), result != null, result));

        return result.toArray(new String[result.size()]);
    }


    private HashMap<String, Integer> listEventAvailabilityMap(String managerId, String eventType){

        HashMap<String, Integer> result = new HashMap<>();
        result.putAll(listEventAvailabilityForThisServer(eventType));

        // inquire different Citys
        for (City c : City.values()) {
            if (c != this.City) {
//                HashMap<String, Object> dataObj = new HashMap<>();
//                dataObj.put(Constants.OP_BOOK_EVENT, eventType);
//                byte[] response = Utils.objectToByteArray(dataObj);
//                byte[] reply = processUDPRequest(response);
                result.putAll(listEventAvailabilityForThisServer(eventType));


                ///CITY VALYE KI ADD KORBO!!! LIKE C IS GIVEN IN UDPCOMMUNICATION>>AMI ONLY 1 TA PRAM USE KORCHI KNO!!!!!

//                result.putAll((HashMap<String, Integer>) Utils
//                        .byteArrayToObject(udpCommunication(c, eventType, Constants.OP_LIST_EVENT_AVAILABILITY)));
            }
        }
        return result;
    }

    private HashMap<String, Integer> listEventAvailabilityForThisServer(String eventType) {
        HashMap<String, Integer> result = new HashMap<>();
        if (cityDatabase.containsKey(eventType)) {
            cityDatabase.get(eventType)
                    .forEach((event, eventDetails) -> result.put(event, (Integer) eventDetails.get(Constants.CAPACITY)
                            - (Integer) eventDetails.get(Constants.CUSTOMERS_ENROLLED)));
        }

        return result;
    }


    public String[] bookEvent(String customerId, String eventId, String eventType) {
        String result[] = new String[2];
        AbstractMap.SimpleEntry<Boolean, String> bookEventMap = bookEventMap(customerId,eventId,eventType);
        result[0] = bookEventMap.getKey().toString();
        result[1] = bookEventMap.getValue();
        return result;
    }

    public String[] swapEvent(String customerId, String newEventId, String newEventType, String oldEventId, String oldEventType) {
        String result[] = new String[2];
        AbstractMap.SimpleEntry<Boolean, String> swapEventMap = swapEventMap(customerId,newEventId,newEventType,oldEventId,oldEventType);
        result[0] = swapEventMap.getKey().toString();
        result[1] = swapEventMap.getValue();
        return result;
    }

    private AbstractMap.SimpleEntry<Boolean, String> swapEventMap (String customerId, String newEventId, String newEventType,
                                                                   String oldEventId, String oldEventType){
        boolean status = true;
        boolean booking = true;
        boolean cancelling = true;
        String msg = null;
        AbstractMap.SimpleEntry<Boolean, String> result = null;
        AbstractMap.SimpleEntry<Boolean, String> resultBooking = null;
        AbstractMap.SimpleEntry<Boolean, String> resultCancelling = null;

        HashMap<String, ArrayList<String>> customerSchedule = getBookingScheduleMap(customerId);
        HashMap<String, HashMap<String, Integer>> listOfAllEvents = new HashMap<>();
        ArrayList<String> eventTypes = new ArrayList<String>();
        eventTypes.add("SEMINAR");
        eventTypes.add("CONFERENCE");
        eventTypes.add("TRADESHOW");

        List<String> listOfOldEvents = new ArrayList<>();

        customerSchedule.forEach((ET, events) -> {
            events.forEach((event) -> {
                City c = City.valueOf(event.substring(0, 3).toUpperCase());
                if (c == this.City)
                    listOfOldEvents.add(event);
                else
                    listOfOldEvents.add(event);
//
            });
        });

        HashMap<String, Integer> ev= new HashMap<>();
        for (String et: eventTypes) {
            ev = listEventAvailabilityMap("", et);
            listOfAllEvents.put(et,ev);
        }
;
        if(listOfOldEvents.contains(oldEventId)){
            if (listOfAllEvents.containsKey(newEventType)) {
                HashMap<String, Integer> events = listOfAllEvents.get(newEventType);
                if (events.containsKey(newEventId)) {

                    synchronized (this){
                        resultBooking = bookEventMap(customerId,newEventId,newEventType);
                        booking = resultBooking.getKey();
                    }
                    synchronized (this){
                        resultCancelling = cancelEventMap(customerId, oldEventId);
                        cancelling = resultCancelling.getKey();
                    }

                    if(!booking){
                        status = false;
                        msg = resultBooking.getValue();
                        bookEventMap(customerId,oldEventId,oldEventType); //hahahhahahha
                    }
                    else if(!cancelling) {
                        status = false;
                        msg = resultCancelling.getValue();
                        cancelEventMap(customerId, newEventId);
                    }
                    else{
                        status = true;
                        msg ="Swap of " + oldEventId + " with "+ newEventId + " is Done";
                    }

//                    City eventCity = City.valueOf(oldEventId.substring(0, 3).toUpperCase());
//                    HashMap<String, String> data = new HashMap<>();
//                    data.put(Constants.CUSTOMER_ID, customerId);
//                    data.put(Constants.NEW_EVENT_ID, newEventId);
//                    data.put(Constants.NEW_EVENT_TYPE, newEventType);
//                    data.put(Constants.EVENT_ID, oldEventId);
//                    data.put(Constants.EVENT_TYPE, oldEventType);
//
//                    result = (AbstractMap.SimpleEntry<Boolean, String>) Utils
//                            .byteArrayToObject(udpCommunication(eventCity, data, Constants.OP_SWAP_EVENT));
//                    return result;
                }else{
                    status = false;
                    msg = newEventId + " is not found in " + newEventType;
                }
            }else{
                status = false;
                msg = "No event is found in " + newEventType;
            }
        }else{
            status = false;
            msg = oldEventId + " is not found in " + oldEventType + " for customer " + customerId;
        }
        if (result == null)
            result = new AbstractMap.SimpleEntry<Boolean, String>(status, msg);

//        LOGGER.info(String.format(Constants.LOG_MSG, Constants.OP_SWAP_EVENT,
//                    Arrays.asList(customerId, newEventId, newEventType,oldEventId,oldEventType), result.getKey(), result.getValue()));

        return result;
    }

    @SuppressWarnings("Duplicates")
    private AbstractMap.SimpleEntry<Boolean, String> bookEventMap(String customerId, String eventId, String eventType){

        boolean status = true;
        String msg = null;
        AbstractMap.SimpleEntry<Boolean, String> result = null;

        HashMap<String, ArrayList<String>> customerSchedule = getBookingScheduleMap(customerId);

        List<String> CityEvents = new ArrayList<>();
        List<String> outOfCityEvents = new ArrayList<>();
        customerSchedule.forEach((ET, events) -> {
            events.forEach((event) -> {
                City c = City.valueOf(event.substring(0, 3).toUpperCase());
                if (c == this.City)
                    CityEvents.add(event);
                else
                    outOfCityEvents.add(event);
            });
        });
        City eventCity = City.valueOf(eventId.substring(0, 3).toUpperCase());
        // enroll in this City only
        if (City == eventCity) {

            // customer already booked this event
            if (CityEvents.contains(eventId)) {
                status = false;
                msg = customerId + " is already booked in " + eventId + ".";
            }
            if (status) {
                result = enrollmentForThisCity(customerId, eventId, eventType);
            }

        } else {
            if (outOfCityEvents.size() >= Constants.MAX_CROSS_EVENTS ) {
                for(String monthOfEvent: outOfCityEvents){
                    if((Integer.parseInt(monthOfEvent.substring(7,8)) - (Integer.parseInt(eventId.substring(7,8)))==0)){
                        status = false;
                        msg = customerId + " has already booked " + Constants.MAX_CROSS_EVENTS + " out-of-City events in a month.";
                    }else{
                        msg =" Booking Successful.";
                        for (City c : City.values()) {
                            if (c == eventCity) {
//
//                                HashMap<String, Object> dataObj = new HashMap<>();
//                                dataObj.put(Constants.OP_BOOK_EVENT, data);
//                                byte[] response = Utils.objectToByteArray(dataObj);
//                                byte[] reply = processUDPRequest(response);
                                result = enrollmentForThisCity(customerId,eventId,eventType);
//
                            }
                        }
                        break;
                    }
                }
            } else {
                // enquire respective City
                for (City c : City.values()) {
                    if (c == eventCity) {
                        result = enrollmentForThisCity(customerId,eventId,eventType);
                    }
                }
            }
        }
        if (result == null)
            result = new AbstractMap.SimpleEntry<Boolean, String>(status, msg);
        return result;
    }


    private AbstractMap.SimpleEntry<Boolean, String> enrollmentForThisCity(String customerId, String eventId, String eventType) {
        boolean status;
        String msg;
        if (cityDatabase.containsKey(eventType)) {
            HashMap<String, HashMap<String, Object>> events = cityDatabase.get(eventType);

            if (events.containsKey(eventId)) {
                HashMap<String, Object> eventDetails = events.get(eventId);


                if (((Integer) eventDetails.get(Constants.CAPACITY)
                        - (Integer) eventDetails.get(Constants.CUSTOMERS_ENROLLED)) > 0) {

                    synchronized (this) {
                        status = ((HashSet<String>) eventDetails.get(Constants.CUSTOMER_IDS)).add(customerId);
                        if (status) {
                            eventDetails.put(Constants.CUSTOMERS_ENROLLED,
                                    (Integer) eventDetails.get(Constants.CUSTOMERS_ENROLLED) + 1);
                            status = true;
                            msg = "Booking Successful.";
                        } else {
                            status = false;
                            msg = customerId + " is already booked in " + eventId + ".";
                        }
                    }
                } else {
                    status = false;
                    msg = eventId + " is full.";
                }
            } else {
                status = false;
                msg = eventId + " is not offered in " + eventType + " eventType.";
            }
        } else {
            status = false;
            msg = "No events available for " + eventType + " eventType.";
        }
        return new AbstractMap.SimpleEntry<Boolean, String>(status, msg);
    }

    public String[] getBookingSchedule(String customerId){
        ArrayList<String> result = new ArrayList<String>();
        HashMap<String, ArrayList<String>> bookingSchedule = getBookingScheduleMap(customerId);
        for (String key: bookingSchedule.keySet()){
            result.add(key + bookingSchedule.get(key));
        }
        return result.toArray(new String[result.size()]);
    }

    public HashMap<String, ArrayList<String>> getBookingScheduleMap(String customerId){
        HashMap<String, ArrayList<String>> schedule = new HashMap<>();
        schedule.putAll(getEventScheduleThisServer(customerId));

        // inquire different Citys
//        for (City c : City.values()) {
//            if (c != this.City) {
//                if(c.toString().equals( "MTL"))
//                    cityDatabase = RMTwo.MTLObject.cityDatabase;
//                if(c.toString().equals( "OTW"))
//                    cityDatabase = RMTwo.OTWObject.cityDatabase;
//                if(c.toString().equals( "TOR"))
//                    cityDatabase = RMTwo.TORObject.cityDatabase;


//                HashMap<String, ArrayList<String>> citySchedule = communication(c, customerId, Constants.OP_GET_BOOKING_SCHEDULE);

//                HashMap<String, Object> dataObj = new HashMap<>();
//                dataObj.put(Constants.OP_GET_BOOKING_SCHEDULE, customerId);
//                byte[] response = Utils.objectToByteArray(dataObj);
//                byte[] reply = processUDPRequest(response);
//
//                HashMap<String, ArrayList<String>> citySchedule = getEventScheduleThisServer(customerId);
//
//                for (String eventType : citySchedule.keySet()) {
//                    if (schedule.containsKey(eventType)) {
//                        schedule.get(eventType).addAll(citySchedule.get(eventType));
//                    } else {
//                        schedule.put(eventType, citySchedule.get(eventType));
//                    }
//                }
//            }
//        }

//        LOGGER.info(String.format(Constants.LOG_MSG, Constants.OP_GET_BOOKING_SCHEDULE, Arrays.asList(customerId),
//                schedule != null, schedule));
        return schedule;
    }

    private HashMap<String, ArrayList<String>> getEventScheduleThisServer(String customerId) {
        HashMap<String, ArrayList<String>> schedule = new HashMap<>();
        cityDatabase.forEach((eventType, events) -> {
            events.forEach((event, details) -> {
                if (((HashSet<String>) details.get(Constants.CUSTOMER_IDS)).contains(customerId)) {
                    if (schedule.containsKey(eventType)) {
                        schedule.get(eventType).add(event);
                    } else {
                        ArrayList<String> temp = new ArrayList<>();
                        temp.add(event);
                        schedule.put(eventType, temp);
                    }
                }
            });
        });
        return schedule;
    }

    public String[] cancelEvent(String customerId, String eventId, String eventType) {
        String result[] = new String[2];
        AbstractMap.SimpleEntry<Boolean, String> cancelEventMap = cancelEventMap(customerId,eventId);
        result[0] = cancelEventMap.getKey().toString();
        result[1] = cancelEventMap.getValue();
        return result;
    }

    private AbstractMap.SimpleEntry<Boolean, String> cancelEventMap(String customerId, String eventId){

        City eventCity = City.valueOf(eventId.substring(0, 3).toUpperCase());
        AbstractMap.SimpleEntry<Boolean, String> result;
        if (this.City == eventCity) {
            result = dropEventOnThisServer(customerId, eventId);
        } else {
//            if(eventCity.toString().equals( "MTL"))
//                cityDatabase = RMTwo.MTLObject.cityDatabase;
//            if(eventCity.toString().equals( "OTW"))
//                cityDatabase = RMTwo.OTWObject.cityDatabase;
//            if(eventCity.toString().equals( "TOR"))
//                cityDatabase = RMTwo.TORObject.cityDatabase;

//            HashMap<String, String> data = new HashMap<>();
//            data.put(Constants.CUSTOMER_ID, customerId);
//            data.put(Constants.EVENT_ID, eventId);
//
//
//            HashMap<String, Object> dataObj = new HashMap<>();
//            dataObj.put(Constants.OP_CANCEL_EVENT, data);
//            byte[] response = Utils.objectToByteArray(dataObj);
//            byte[] reply = processUDPRequest(response);
            result = dropEventOnThisServer(customerId,eventId);

//             udpCommunication(City serverCity, Object info, String method)

//            result = (AbstractMap.SimpleEntry<Boolean, String>) Utils
//                    .byteArrayToObject(udpCommunication(eventCity, data, Constants.OP_CANCEL_EVENT));
        }

//        LOGGER.info(String.format(Constants.LOG_MSG, Constants.OP_CANCEL_EVENT, Arrays.asList(customerId, eventId),
//                result.getKey(), result.getValue()));
        return result;
    }

    private AbstractMap.SimpleEntry<Boolean, String> dropEventOnThisServer(String customerId, String eventId) {
        final Map<Boolean, String> temp = new HashMap<>();
        if (cityDatabase.size() > 0) {
            cityDatabase.forEach((eventType, events) -> {
                if (events.containsKey(eventId)) {
                    events.forEach((event, eventDetails) -> {
                        synchronized (this) {
                            if (event.equals(eventId)) {
                                boolean status = ((HashSet<String>) eventDetails.get(Constants.CUSTOMER_IDS))
                                        .remove(customerId);
                                if (status) {
                                    eventDetails.put(Constants.CUSTOMERS_ENROLLED,
                                            ((Integer) eventDetails.get(Constants.CUSTOMERS_ENROLLED) - 1));
                                    temp.put(true, "success");
                                } else {
                                    temp.put(false, customerId + " isn't booked in " + eventId + ".");
                                }
                            }
                        }
                    });
                } else {
                    temp.put(false, eventId + " isn't offered by the City yet.");
                }
            });
        } else {
            temp.put(false, eventId + " isn't offered by the City yet.");
        }

        if (temp.containsKey(true)) {
            return new AbstractMap.SimpleEntry<Boolean, String>(true, "Event Cancelled.");
        } else {
            return new AbstractMap.SimpleEntry<Boolean, String>(false, temp.get(false));
        }
    }

    private byte[] udpCommunication(City serverCity, String info, String method) {

        LOGGER.info("Making UPD Socket Call to " + serverCity + " Server for method : " + method);

        // UDP SOCKET CALL AS CLIENT
        HashMap<String, Object> data = new HashMap<>();
        byte[] response = null;
        data.put(method, info);
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            byte[] message = Utils.objectToByteArray(data);
//            byte[] message = Utils.objectToByteArray(info);
            InetAddress remoteUdpHost = InetAddress.getByName("localhost");
            DatagramPacket request = new DatagramPacket(message, message.length, remoteUdpHost, serverCity.getUdpPort());
            socket.send(request);
            byte[] buffer = new byte[65556];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
            socket.receive(reply);
            response = reply.getData();
        } catch (SocketException e) {
            LOGGER.severe("SocketException: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            LOGGER.severe("IOException : " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (socket != null)
                socket.close();
        }

        return response;
    }

//    private  HashMap<String, ArrayList<String>> communication(City serverCity, Object info, String method) {
//        HashMap<String, Object> data = new HashMap<>();
//        data.put(method, info);
//        if(serverCity.toString().equals( "MTL")){
//            cityDatabase = RMTwo.MTLObject.cityDatabase;
//            processUDPRequest(method, info);
//        }
//        if(serverCity.toString().equals( "OTW")){
//            cityDatabase = RMTwo.OTWObject.cityDatabase;
//            processUDPRequest(method, info);
//        }
//        if(serverCity.toString().equals( "TOR")) {
//            cityDatabase = RMTwo.TORObject.cityDatabase;
//            processUDPRequest(method, info);
//        }
//
//
//    }



//        // UDP SOCKET CALL AS CLIENT
//        HashMap<String, Object> data = new HashMap<>();
//        byte[] response = null;
//        data.put(method, info);
//        DatagramSocket socket = null;
//        try {
//            socket = new DatagramSocket();
//            byte[] message = Utils.objectToByteArray(data);
////            byte[] message = Utils.objectToByteArray(info);
//            InetAddress remoteUdpHost = InetAddress.getByName("localhost");
//            DatagramPacket request = new DatagramPacket(message, message.length, remoteUdpHost, serverCity.getUdpPort());
//            socket.send(request);
//            byte[] buffer = new byte[65556];
//            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
//            socket.receive(reply);
//            response = reply.getData();
//        } catch (SocketException e) {
//            LOGGER.severe("SocketException: " + e.getMessage());
//            e.printStackTrace();
//        } catch (IOException e) {
//            LOGGER.severe("IOException : " + e.getMessage());
//            e.printStackTrace();
//        } finally {
//            if (socket != null)
//                socket.close();
//        }
//
//        return response;
//    }

//    public void UDPServer() {
//        DatagramSocket socket = null;
//        try {
//            socket = new DatagramSocket(City.getUdpPort());
//            byte[] buffer = new byte[1000];// to stored the received data from the client.
//            LOGGER.info(this.City + " UDP Server Started............");
//            // non-terminating loop as the server is always in listening mode.
//            while (true) {
//                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
//                // Server waits for the request to come
//                socket.receive(request); // request received
//
//                byte[] response = processUDPRequest(request.getData());
//
//                DatagramPacket reply = new DatagramPacket(response, response.length, request.getAddress(),
//                        request.getPort());// reply packet ready
//                socket.send(reply);// reply sent
//            }
//        } catch (SocketException e) {
//            LOGGER.severe("SocketException: " + e.getMessage());
//            e.printStackTrace();
//        } catch (IOException e) {
//            LOGGER.severe("IOException : " + e.getMessage());
//            e.printStackTrace();
//        } finally {
//            if (socket != null)
//                socket.close();
//        }
//    }

//    private  HashMap<String, Object> processUDPRequest(String method, Object info) {
//
////        byte[] response = null;
//        HashMap<String, Object> response ;
//
//            switch (method) {
//                case Constants.OP_LIST_EVENT_AVAILABILITY:
//                    String eventType = (String) request.get(key);
//                    LOGGER.info(listEventAvailabilityForThisServer(eventType).toString());
//                    response = Utils.objectToByteArray(listEventAvailabilityForThisServer(eventType));
//                    break;
//                case Constants.OP_BOOK_EVENT:
//                    HashMap<String, String> info = (HashMap<String, String>) request.get(key);
//                    response = Utils.objectToByteArray(enrollmentForThisCity(info.get(Constants.CUSTOMER_ID),
//                            info.get(Constants.EVENT_ID), info.get(Constants.EVENT_TYPE)));
////                    response = enrollmentForThisCity(info.get(Constants.CUSTOMER_ID),
////                            info.get(Constants.EVENT_ID), info.get(Constants.EVENT_TYPE));
//                    break;
//                case Constants.OP_GET_BOOKING_SCHEDULE:
//                    String customerId = (String) request.get(key);
//                    response = Utils.objectToByteArray(getEventScheduleThisServer(customerId));
//                    break;
//                case Constants.OP_CANCEL_EVENT:
//                    info = (HashMap<String, String>) request.get(key);
//                    response = Utils.objectToByteArray(
//                            dropEventOnThisServer(info.get(Constants.CUSTOMER_ID), info.get(Constants.EVENT_ID)));
//                    break;
//                case Constants.OP_SWAP_EVENT:
//                    info = (HashMap<String, String>) request.get(key);
//                    response = Utils.objectToByteArray(
//                            swapEventMap(info.get(Constants.CUSTOMER_ID), info.get(Constants.NEW_EVENT_ID), info.get(Constants.NEW_EVENT_TYPE),
//                                    info.get(Constants.EVENT_ID), info.get(Constants.EVENT_TYPE)));
//                    break;
//            }
//        }
//
//        return response;
//    }

    public void shutdown() {
        orb.shutdown(false);
    }
}
