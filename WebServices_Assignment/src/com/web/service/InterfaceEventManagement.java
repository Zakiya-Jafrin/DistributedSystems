package com.web.service;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import java.util.ArrayList;
import java.util.HashMap;

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface InterfaceEventManagement {
    boolean addEvent(String managerID, String eventID, String eventType, int bookingCapacity);
    boolean removeEvent(String managerID, String eventID, String eventType);
//    HashMap<String, Integer> listEventAvailability(String managerID, String eventType) ;
    String[] listEventAvailability(String managerID, String eventType) ;

    String[] getBookingSchedule(String customerId);
//    HashMap<String, ArrayList<String>> getBookingSchedule(String customerId);

    String[] bookEvent (String customerId, String eventId, String eventType);
    String[] cancelEvent (String customerId, String eventId);
    String[] swapEvent (String customerId, String newEventId, String newEventType, String oldEventId, String oldEventType);

}
