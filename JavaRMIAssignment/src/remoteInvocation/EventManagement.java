package remoteInvocation;

import helper.City;
import helper.Constants;
import helper.Utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.logging.Logger;

public class EventManagement extends UnicastRemoteObject implements InterfaceEventManagement{

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private City City;

    // in-memory database
    private HashMap<String, HashMap<String, HashMap<String, Object>>> cityDatabase;

    protected EventManagement() throws RemoteException{
        super();
    }

    public EventManagement(String cityName) throws RemoteException {
        this.City = City.valueOf(cityName);
        cityDatabase = new HashMap<>();
    }

    public boolean addEvent(String managerID, String eventID, String eventType, int bookingCapacity) throws RemoteException{
        boolean status = false;
        String msg = Constants.EMPTYSTRING;
        if (cityDatabase.containsKey(eventType)) {
            HashMap<String, HashMap<String, Object>> events = cityDatabase.get(eventType);

            if (events.containsKey(eventID)) {
                events.get(eventID).put(Constants.CAPACITY,bookingCapacity);
                status = false;
                msg = "Event already exists for " + eventType + " eventType. Capacity Updated";
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

        LOGGER.info(String.format(Constants.LOG_MSG, Constants.OP_ADD_EVENT,
                Arrays.asList(managerID, eventID, eventType, bookingCapacity), status, msg));

        return status;
    }

    public boolean removeEvent(String managerID, String eventID, String eventType) throws RemoteException{
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

        LOGGER.info(String.format(Constants.LOG_MSG, Constants.OP_REMOVE_EVENT,
                Arrays.asList(managerID, eventID, eventType), status, msg));

        return status;
    }


    public HashMap<String, Integer> listEventAvailability(String managerId, String eventType) throws RemoteException {

        HashMap<String, Integer> result = new HashMap<>();
        result.putAll(listEventAvailabilityForThisServer(eventType));

        // inquire different Citys
        for (City c : City.values()) {
            if (c != this.City) {
                result.putAll((HashMap<String, Integer>) Utils
                        .byteArrayToObject(udpCommunication(c, eventType, Constants.OP_LIST_EVENT_AVAILABILITY)));
            }
        }

        LOGGER.info(String.format(Constants.LOG_MSG, Constants.OP_LIST_EVENT_AVAILABILITY,
                Arrays.asList(managerId, eventType), result != null, result));

        return result;
    }

    private HashMap<String, Integer> listEventAvailabilityForThisServer(String eventType) {
        HashMap<String, Integer> result = new HashMap<>();
        // get courses from the current City
        if (cityDatabase.containsKey(eventType)) {
            cityDatabase.get(eventType)
                    .forEach((event, eventDetails) -> result.put(event, (Integer) eventDetails.get(Constants.CAPACITY)
                            - (Integer) eventDetails.get(Constants.CUSTOMERS_ENROLLED)));
        }

        return result;
    }


    @SuppressWarnings("Duplicates")
    public SimpleEntry<Boolean, String> bookEvent(String customerId, String eventId, String eventType)
            throws RemoteException {

        boolean status = true;
        String msg = null;
        SimpleEntry<Boolean, String> result = null;

        HashMap<String, ArrayList<String>> customerSchedule = getBookingSchedule(customerId);

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

//            if (outOfCityEvents.size() >= Constants.MAX_CROSS_EVENTS) {
//                status = false;
//                msg = customerId + " is already booked in " + Constants.MAX_CROSS_EVENTS + " out-of-City events.";
//            } else {
//                // enquire respective City
//                for (City c : City.values()) {
//                    if (c == eventCity) {
//                        HashMap<String, String> data = new HashMap<>();
//                        data.put(Constants.CUSTOMER_ID, customerId);
//                        data.put(Constants.EVENT_ID, eventId);
//                        data.put(Constants.EVENT_TYPE, eventType);
//
//                        result = (SimpleEntry<Boolean, String>) Utils
//                                .byteArrayToObject(udpCommunication(eventCity, data, Constants.OP_BOOK_EVENT));
//                    }
//                }
//            }

            if (outOfCityEvents.size() >= Constants.MAX_CROSS_EVENTS ) {
                for(String monthOfEvent: outOfCityEvents){
                    if((Integer.parseInt(monthOfEvent.substring(7,8)) - (Integer.parseInt(eventId.substring(7,8)))==0)){
                        status = false;
                        msg = customerId + " has already booked " + Constants.MAX_CROSS_EVENTS + " out-of-City events in a month.";
                    }else{
//                        msg =" Booking Successful.";
                        for (City c : City.values()) {
                            if (c == eventCity) {
                                HashMap<String, String> data = new HashMap<>();
                                data.put(Constants.CUSTOMER_ID, customerId);
                                data.put(Constants.EVENT_ID, eventId);
                                data.put(Constants.EVENT_TYPE, eventType);

                                result = (SimpleEntry<Boolean, String>) Utils
                                        .byteArrayToObject(udpCommunication(eventCity, data, Constants.OP_BOOK_EVENT));
                            }
                        }
                        break;
                    }
                }
            } else {
                // enquire respective City
                for (City c : City.values()) {
                    if (c == eventCity) {
                        HashMap<String, String> data = new HashMap<>();
                        data.put(Constants.CUSTOMER_ID, customerId);
                        data.put(Constants.EVENT_ID, eventId);
                        data.put(Constants.EVENT_TYPE, eventType);

                        result = (SimpleEntry<Boolean, String>) Utils
                                .byteArrayToObject(udpCommunication(eventCity, data, Constants.OP_BOOK_EVENT));
                    }
                }
            }
        }
        if (result == null)
            result = new SimpleEntry<Boolean, String>(status, msg);

        LOGGER.info(String.format(Constants.LOG_MSG, Constants.OP_BOOK_EVENT,
                Arrays.asList(customerId, eventId, eventType), result.getKey(), result.getValue()));

        return result;
    }


    private SimpleEntry<Boolean, String> enrollmentForThisCity(String customerId, String eventId, String eventType) {
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

        return new SimpleEntry<Boolean, String>(status, msg);
    }

    public HashMap<String, ArrayList<String>> getBookingSchedule(String customerId) throws RemoteException {
        HashMap<String, ArrayList<String>> schedule = new HashMap<>();
        schedule.putAll(getEventScheduleThisServer(customerId));

        // inquire different Citys
        for (City c : City.values()) {
            if (c != this.City) {

                HashMap<String, ArrayList<String>> citySchedule = (HashMap<String, ArrayList<String>>) Utils
                        .byteArrayToObject(udpCommunication(c, customerId, Constants.OP_GET_BOOKING_SCHEDULE));

                for (String eventType : citySchedule.keySet()) {
                    if (schedule.containsKey(eventType)) {
                        schedule.get(eventType).addAll(citySchedule.get(eventType));
                    } else {
                        schedule.put(eventType, citySchedule.get(eventType));
                    }
                }
            }
        }
        LOGGER.info(String.format(Constants.LOG_MSG, Constants.OP_GET_BOOKING_SCHEDULE, Arrays.asList(customerId),
                schedule != null, schedule));
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

    public SimpleEntry<Boolean, String> cancelEvent(String customerId, String eventId) throws RemoteException {

        City eventCity = City.valueOf(eventId.substring(0, 3).toUpperCase());
        SimpleEntry<Boolean, String> result;
        if (this.City == eventCity) {
            result = dropEventOnThisServer(customerId, eventId);
        } else {
            HashMap<String, String> data = new HashMap<>();
            data.put(Constants.CUSTOMER_ID, customerId);
            data.put(Constants.EVENT_ID, eventId);
            result = (SimpleEntry<Boolean, String>) Utils
                    .byteArrayToObject(udpCommunication(eventCity, data, Constants.OP_CANCEL_EVENT));
        }

        LOGGER.info(String.format(Constants.LOG_MSG, Constants.OP_CANCEL_EVENT, Arrays.asList(customerId, eventId),
                result.getKey(), result.getValue()));
        return result;
    }

    private SimpleEntry<Boolean, String> dropEventOnThisServer(String customerId, String eventId) {
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
            return new SimpleEntry<Boolean, String>(true, "Event Cancelled.");
        } else {
            return new SimpleEntry<Boolean, String>(false, temp.get(false));
        }
    }


    private byte[] udpCommunication(City serverCity, Object info, String method) {

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

    public void UDPServer() {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(City.getUdpPort());
            byte[] buffer = new byte[1000];// to stored the received data from the client.
            LOGGER.info(this.City + " UDP Server Started............");
            // non-terminating loop as the server is always in listening mode.
            while (true) {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                // Server waits for the request to come
                socket.receive(request); // request received

                byte[] response = processUDPRequest(request.getData());

                DatagramPacket reply = new DatagramPacket(response, response.length, request.getAddress(),
                        request.getPort());// reply packet ready
                socket.send(reply);// reply sent
            }
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
    }


    private byte[] processUDPRequest(byte[] data) {

        byte[] response = null;
        HashMap<String, Object> request = (HashMap<String, Object>) Utils.byteArrayToObject(data);

        for (String key : request.keySet()) {

            LOGGER.info("Received UDP Socket call for method[" + key + "] with parameters[" + request.get(key) + "]");
            switch (key) {
                case Constants.OP_LIST_EVENT_AVAILABILITY:
                    String eventType = (String) request.get(key);
                    response = Utils.objectToByteArray(listEventAvailabilityForThisServer(eventType));
                    break;
                case Constants.OP_BOOK_EVENT:
                    HashMap<String, String> info = (HashMap<String, String>) request.get(key);
                    response = Utils.objectToByteArray(enrollmentForThisCity(info.get(Constants.CUSTOMER_ID),
                            info.get(Constants.EVENT_ID), info.get(Constants.EVENT_TYPE)));
                    break;
                case Constants.OP_GET_BOOKING_SCHEDULE:
                    String customerId = (String) request.get(key);
                    response = Utils.objectToByteArray(getEventScheduleThisServer(customerId));
                    break;
                case Constants.OP_CANCEL_EVENT:
                    info = (HashMap<String, String>) request.get(key);
                    response = Utils.objectToByteArray(
                            dropEventOnThisServer(info.get(Constants.CUSTOMER_ID), info.get(Constants.EVENT_ID)));
                    break;
            }
        }

        return response;
    }

}
