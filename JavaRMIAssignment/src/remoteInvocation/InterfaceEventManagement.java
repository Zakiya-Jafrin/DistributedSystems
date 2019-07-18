package remoteInvocation;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;

public interface InterfaceEventManagement extends Remote {
    boolean addEvent(String managerID, String eventID, String eventType, int bookingCapacity) throws RemoteException;
    boolean removeEvent(String managerID, String eventID, String eventType) throws RemoteException;
    HashMap<String, Integer> listEventAvailability(String managerID, String eventType) throws RemoteException;

    SimpleEntry<Boolean, String> bookEvent(String customerId, String eventId, String eventType) throws RemoteException;
    HashMap<String, ArrayList<String>> getBookingSchedule(String customerId) throws RemoteException;
    SimpleEntry<Boolean, String> cancelEvent(String customerId, String eventId) throws RemoteException;
//    SimpleEntry<Boolean, String> swapEvent(String customerId, String eventId) throws RemoteException;
}
