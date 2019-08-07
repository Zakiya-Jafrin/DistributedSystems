package RMTwo.servers;

import Client.User;
import Client.Login;
import RMTwo.helper.City;
import RMTwo.helper.Constants;
import RMTwo.remoteInvocation.EventManagement;
import UDP.UDPClient;
import UDP.UDPMulticastClient;
import UDP.UDPMulticastServer;
import UDP.UDPServer;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.logging.Logger;

public class RMTwo {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    public static EventManagement MTLObject;
    public static EventManagement OTWObject;
    public static EventManagement TORObject;

    @SuppressWarnings("Duplicates")
    public static void main(String args[]) {
        EventManagement eventObj;
        try {
            eventObj = new EventManagement("MTL");
            MTLObject = eventObj;
            System.out.println("Montreal Server ready and waiting ...");
//            setupLogging();
//            sendHeartbeats();
            eventObj = new EventManagement("OTW");
            OTWObject = eventObj;
            System.out.println("Ottawa Server ready and waiting ...");
//            setupLogging();
            eventObj = new EventManagement("TOR");
            TORObject = eventObj;
            System.out.println("Toronto Server ready and waiting ...");
//            setupLogging();
        } catch (Exception e) {
            System.out.print(e);
        }
//        receive();
        String request = UDPMulticastServer.ReceiveUDPMessage(Constants.PORT_SE_TO_RM);
        System.out.println(request);
//        String events = "";
        while (!request.equals("")) {
            String events = "";
            String[] request_parts = request.split(" ");
            User user = new User();
            Login.validateUser(request_parts[1],user);
            EventManagement eventManagement;
            City code = user.getCity();
            String[] result;
//
            switch (code) {
                case MTL:
                    eventManagement = MTLObject;
                    break;
                case OTW:
                    eventManagement = OTWObject;
                    break;
                case TOR:
                    eventManagement = TORObject;
                    break;
                default:
                    eventManagement = MTLObject;
                    break;
            }
            switch (request_parts[2]) {
                case Constants.OP_ADD_EVENT:
                    if (eventManagement.addEvent(user.toString(), request_parts[3], request_parts[4],
                            Integer.valueOf(request_parts[5]))) {
                        UDPMulticastClient.SendUDPMessage(request_parts[0] + ",RM3,true," + "'Event " + request_parts[3] + " added successfully.'", Constants.IP_RM3,  Constants.PORT_RMs);
                    } else {
                        UDPMulticastClient.SendUDPMessage(request_parts[0] + ",RM3,false," + "'Error adding event " + request_parts[3] + "..'", Constants.IP_RM3,  Constants.PORT_RMs);
                    }
                    break;
                case Constants.OP_REMOVE_EVENT:
                    if (eventManagement.removeEvent(user.toString(), request_parts[3], request_parts[4])) {
                        UDPMulticastClient.SendUDPMessage(
                                request_parts[0] + ",RM3,true," + "'Event " + request_parts[3] + " removed successfully'", Constants.IP_RM3,  Constants.PORT_RMs);
                    } else {
                        UDPMulticastClient.SendUDPMessage(request_parts[0] + ",RM3,false," + "'Error removing event " + request_parts[3] + "'", Constants.IP_RM3,  Constants.PORT_RMs);
                    }
                    break;
                case Constants.OP_LIST_EVENT_AVAILABILITY:
                    result = eventManagement.listEventAvailability(user.toString(), request_parts[3]);
                    for (int i = 0; i < result.length; i++) {
                        events += "'" + (result[i]) + "'";
                        if (i < result.length - 1)
                            events += ",";
                    }
                    UDPMulticastClient.SendUDPMessage(request_parts[0] + ",RM3,true," + events, Constants.IP_RM3,  Constants.PORT_RMs);
                    break;
                case Constants.OP_BOOK_EVENT:
                    result = (eventManagement.bookEvent(user.toString(), request_parts[3], request_parts[4]));
                    if (Boolean.valueOf(result[0]))
                        UDPMulticastClient.SendUDPMessage(request_parts[0] + ",RM3,true," + "'Event (" + request_parts[3] + ") booked successfully'", Constants.IP_RM3,  Constants.PORT_RMs);
                    else
                        UDPMulticastClient.SendUDPMessage(request_parts[0] + ",RM3,false," + result[1], Constants.IP_RM3,  Constants.PORT_RMs);
                    break;
                case Constants.OP_CANCEL_EVENT:
                    result = (eventManagement.cancelEvent(user.toString(), request_parts[3], request_parts[4]));
                    if (Boolean.valueOf(result[0]))
                        UDPMulticastClient.SendUDPMessage(
                                request_parts[0] + ",RM3,true," + "Event (" + request_parts[3] + ") successfully cancelled", Constants.IP_RM3,  Constants.PORT_RMs);
                    else
                        UDPMulticastClient.SendUDPMessage(request_parts[0] + ",RM3,false," + result[1], Constants.IP_RM3,  Constants.PORT_RMs);
                    break;
                case Constants.OP_SWAP_EVENT:
                    result = (eventManagement.swapEvent(user.toString(), request_parts[3], request_parts[4], request_parts[5],
                            request_parts[6]));
                    if (Boolean.valueOf(result[0]))
                        UDPMulticastClient.SendUDPMessage(request_parts[0] + ",RM3,true," + "Event (" + request_parts[3] + ") swapped successfully", Constants.IP_RM3,  Constants.PORT_RMs);
                    else
                        UDPMulticastClient.SendUDPMessage(request_parts[0] + ",RM3,false," + result[1], Constants.IP_RM3,  Constants.PORT_RMs);
                    break;
                case Constants.OP_GET_BOOKING_SCHEDULE:
                    String[] schedule = eventManagement.getBookingSchedule(user.toString());
                    for (String event : schedule)
                        events += "'" + event + "',";

                    UDPMulticastClient.SendUDPMessage(request_parts[0] + ",RM3,true," + events, Constants.IP_RM3,  Constants.PORT_RMs);
                    break;
            }
            request = UDPMulticastServer.ReceiveUDPMessage(Constants.PORT_SE_TO_RM);
        }

    }
}

//
//            if(request.contains(Constants.OP_ADD_EVENT) && request.contains("MTLE100519"))
//                request = "100000003 MTLM1234 " + Constants.OP_LIST_EVENT_AVAILABILITY + " A";
//            else if(request.contains(Constants.OP_LIST_EVENT_AVAILABILITY))
//                request = "100000002 MTLM1234 " + Constants.OP_REMOVE_EVENT + " MTLE110519 S";
//            else if(request.contains(Constants.OP_REMOVE_EVENT))
//                request = "100000004 MTLM1234 " + Constants.OP_BOOK_EVENT + " MTLE100519 S";
//            else if(request.contains(Constants.OP_BOOK_EVENT))
//                request = "100000007 MTLM1234 " + Constants.OP_GET_BOOKING_SCHEDULE + " A";
//            else if(request.contains(Constants.OP_GET_BOOKING_SCHEDULE))
//                request = "100000001 MTLM1234 " + Constants.OP_ADD_EVENT + " MTLE110619 S 2";
//            else if(request.contains(Constants.OP_ADD_EVENT) && request.contains("MTLE110619"))
//                request = "100000006 MTLM1234 " + Constants.OP_SWAP_EVENT + " MTLE100519 S MTLE110619 S";
//            else if(request.contains(Constants.OP_SWAP_EVENT))
//                request = "100000005 MTLM1234 " + Constants.OP_CANCEL_EVENT + " MTLE100519 S";
//            else if(request.contains(Constants.OP_CANCEL_EVENT))
//                request = "";


//     if (eventManagement.addEvent("MTLM1234", "MTLE100519", "SEMINAR", 2)) {
//                sendMessageBackToFrontend(request_parts[0] + ",true," + "'Event " + request_parts[3] + " added successfully.'"+ 4321);
////                System.out.println(request_parts[0] + ",true," + "'Event " + request_parts[3] + " added successfully.'"+ 4321);
////                        UDPMulticastServer.sendUDPMessage(request_parts[0] + ",true," + "'Event " + request_parts[3] + " added successfully.'", 4321);
//            }
//
//            switch (request_parts[2]) {
//                case Constants.OP_ADD_EVENT:
//                    if (eventManagement.addEvent(user.toString(), request_parts[3], request_parts[4], Integer.valueOf(request_parts[5]))) {
//                        UDPMulticastClient.SendUDPMessage(request_parts[0] + ",RM3,true," + "'Event " + request_parts[3] + " added successfully.'", Constants.IP_RM3, Constants.PORT_RMs);
////                        UDPMulticastServer.sendUDPMessage(request_parts[0] + ",true," + "'Event " + request_parts[3] + " added successfully.'", 4321);
//                    } else {
////                        System.out.println(request_parts[0] + ",false," + "'Error adding event " + request_parts[3] + "..'"+ 4321);
//                        UDPMulticastClient.SendUDPMessage(request_parts[0] + ",RM3,false," + "'Error adding event " + request_parts[3] + "..'", Constants.IP_RM3, Constants.PORT_RMs);
//                    }
//                    break;
//                case Constants.OP_REMOVE_EVENT:
//                    if (eventManagement.removeEvent(user.toString(), request_parts[3], request_parts[4])) {
//                        UDPMulticastClient.SendUDPMessage(request_parts[0] + ",RM3,true," + "'Event " + request_parts[3] + " removed successfully'", Constants.IP_RM3, Constants.PORT_RMs);
////                        System.out.println(request_parts[0] + ",true," + "'Event " + request_parts[3] + " removed successfully'"+4321);
//                    } else {
////                        System.out.println(request_parts[0] + ",false," + "'Error removing event " + request_parts[3] + "'"+4321);
//                        UDPMulticastClient.SendUDPMessage(request_parts[0] + ",true," + "'Event " + request_parts[3] + " removed successfully'", Constants.IP_RM3, Constants.PORT_RMs);
//                    }
//                    break;
//                case Constants.OP_LIST_EVENT_AVAILABILITY:
//                    result = eventManagement.listEventAvailability(user.toString(), request_parts[3]);
//                    for (int i = 0; i < result.length; i++) {
//                        events += "' " + (result[i]) + "'";
//                        if (i < result.length - 1)
//                            events += ",";
//                    }
////                    UDPMulticastServer.sendUDPMessage(request_parts[0] + ",true," + events,4321);
//                    break;
//                case Constants.OP_BOOK_EVENT:
//                    result = (eventManagement.bookEvent(user.toString(), request_parts[3], request_parts[4]));
//                    if (Boolean.valueOf(result[0]))
//                        System.out.println(request_parts[0] + ",true," + "'Event (" + request_parts[3] + ") booked successfully'"+4321);
//                    else
////                        System.out.println(request_parts[0] + ",false," + result[1]+4321);
//                    break;
//                case Constants.OP_CANCEL_EVENT:
//                    result = (eventManagement.cancelEvent(user.toString(), request_parts[3], request_parts[4]));
//                    if (Boolean.valueOf(result[0]))
////                        UDPMulticastServer.sendUDPMessage(
////                                request_parts[0] + ",true," + "Event (" + request_parts[3] + ") successfully cancelled",4321);
////                    else
////                        UDPMulticastServer.sendUDPMessage(request_parts[0] + ",false," + result[1],4321);
//                    break;
//                case Constants.OP_SWAP_EVENT:
//                    result = (eventManagement.swapEvent(user.toString(), request_parts[3], request_parts[4], request_parts[5],
//                            request_parts[6]));
//                    if (Boolean.valueOf(result[0]))
////                        UDPMulticastServer.sendUDPMessage(request_parts[0] + ",true," + "Event (" + request_parts[3] + ") swapped successfully",4321);
////                    else
////                        UDPMulticastServer.sendUDPMessage(request_parts[0] + ",false," + result[1],4321);
//                    break;
//                case Constants.OP_GET_BOOKING_SCHEDULE:
//                    String[] schedule = eventManagement.getBookingSchedule(user.toString());
//                    for (String event : schedule)
//                        events += "'" + event + "',";
//
////                    UDPMulticastServer.sendUDPMessage(request_parts[0] + ",true," + events,4321);
//                    break;
//            }

//
//    public static void clientMultiCast(String message) {
//        DatagramSocket aSocket = null;
//        try {
//            aSocket = new DatagramSocket();
//            byte[] m = message.getBytes();
//            InetAddress aHost = InetAddress.getByName("230.1.1.5");
//
//            DatagramPacket request = new DatagramPacket(m, m.length, aHost, 1313);
//            aSocket.send(request);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    private static String receive() {
//        MulticastSocket aSocket = null;
//        String sentence ="";
//        try {
//
//            aSocket = new MulticastSocket(1313);
//            aSocket.joinGroup(InetAddress.getByName("230.1.1.5"));
//            byte[] buffer = new byte[1000];
//
//            while (true) {
//                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
//                aSocket.receive(request);
//
//               sentence = new String( request.getData(), 0,
//                        request.getLength() );
//                System.out.print(sentence);
////                DatagramPacket reply = new DatagramPacket(request.getData(), request.getLength(), request.getAddress(),
////                        request.getPort());
////                aSocket.send(reply);
//            }
//
//        } catch (SocketException e) {
//            System.out.println("Socket: " + e.getMessage());
//        } catch (IOException e) {
//            System.out.println("IO: " + e.getMessage());
//        } finally {
//            if (aSocket != null)
//                aSocket.close();
//        }
//        return sentence;
//    }
//
//
//    public static void sendMessageBackToFrontend(String message) {
//        System.out.println(message);
//        DatagramSocket aSocket = null;
//        try {
//            aSocket = new DatagramSocket();
//            byte[] m = message.getBytes();
//            InetAddress aHost = InetAddress.getByName("localhost");
//
//            DatagramPacket request = new DatagramPacket(m, m.length, aHost, Constants.PORT_FE);
//            aSocket.send(request);
//            aSocket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }

