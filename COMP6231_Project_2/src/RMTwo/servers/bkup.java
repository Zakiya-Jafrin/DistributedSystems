//package RMTwo.servers;
//
//import Client.Login;
//import Client.User;
//import RMTwo.helper.City;
//import RMTwo.helper.Constants;
//import RMTwo.remoteInvocation.EventManagement;
//import UDP.UDPMulticastClient;
//import UDP.UDPMulticastServer;
//
//import java.util.logging.Logger;
//
//public class bkup {
//    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
//    public static EventManagement MTLObject;
//    public static EventManagement OTWObject;
//    public static EventManagement TORObject;
//
//    @SuppressWarnings("Duplicates")
//    public static void main(String args[]) {
//        EventManagement eventObj;
//        try {
//            eventObj = new EventManagement("MTL");
//            MTLObject = eventObj;
//            System.out.println("Montreal Server ready and waiting ...");
////            setupLogging();
////            sendHeartbeats();
//            eventObj = new EventManagement("OTW");
//            OTWObject = eventObj;
//            System.out.println("Ottawa Server ready and waiting ...");
////            setupLogging();
//            eventObj = new EventManagement("TOR");
//            TORObject = eventObj;
//            System.out.println("Toronto Server ready and waiting ...");
////            setupLogging();
//        } catch (Exception e) {
//            System.out.print(e);
//        }
//        String request = UDPMulticastServer.ReceiveUDPMessage(Constants.PORT_SEQUENCER);
//        System.out.println(request);
//        String events = "";
//        while (!request.equals("")) {
//            String[] request_parts = request.split(" ");
//            User user = new User();
//            Login.validateUser(request_parts[1],user);
//            EventManagement eventManagement;
//            City code = user.getCity();
//            String[] result;
////
//            switch (code) {
//                case MTL:
//                    eventManagement = MTLObject;
//                    break;
//                case OTW:
//                    eventManagement = OTWObject;
//                    break;
//                case TOR:
//                    eventManagement = TORObject;
//                    break;
//                default:
//                    eventManagement = MTLObject;
//                    break;
//            }
//            switch (request_parts[2]) {
//                case Constants.OP_ADD_EVENT:
//                    if (eventManagement.addEvent(user.toString(), request_parts[3], request_parts[4],
//                            Integer.valueOf(request_parts[5]))) {
//                        UDPMulticastClient.SendUDPMessage(request_parts[0] + ",RM4,true," + "'Event " + request_parts[3] + " added successfully.'", Constants.IP_RM4,  Constants.PORT_RMs);
//                    } else {
//                        UDPMulticastClient.SendUDPMessage(request_parts[0] + ",RM4,false," + "'Error adding event " + request_parts[3] + "..'", Constants.IP_RM4,  Constants.PORT_RMs);
//                    }
//                    break;
//                case Constants.OP_REMOVE_EVENT:
//                    if (eventManagement.removeEvent(user.toString(), request_parts[3], request_parts[4])) {
//                        UDPMulticastClient.SendUDPMessage(
//                                request_parts[0] + ",RM4,true," + "'Event " + request_parts[3] + " removed successfully'", Constants.IP_RM4,  Constants.PORT_RMs);
//                    } else {
//                        UDPMulticastClient.SendUDPMessage(request_parts[0] + ",RM4,false," + "'Error removing event " + request_parts[3] + "'", Constants.IP_RM4,  Constants.PORT_RMs);
//                    }
//                    break;
//                case Constants.OP_LIST_EVENT_AVAILABILITY:
//                    result = eventManagement.listEventAvailability(user.toString(), request_parts[3]);
//                    for (int i = 0; i < result.length; i++) {
//                        events += "' " + (result[i]) + "'";
//                        if (i < result.length - 1)
//                            events += ",";
//                    }
//                    UDPMulticastClient.SendUDPMessage(request_parts[0] + ",RM4,true," + events, Constants.IP_RM4,  Constants.PORT_RMs);
//                    break;
//                case Constants.OP_BOOK_EVENT:
//                    result = (eventManagement.bookEvent(user.toString(), request_parts[3], request_parts[4]));
//                    if (Boolean.valueOf(result[0]))
//                        UDPMulticastClient.SendUDPMessage(request_parts[0] + ",RM4,true," + "'Event (" + request_parts[3] + ") booked successfully'", Constants.IP_RM4,  Constants.PORT_RMs);
//                    else
//                        UDPMulticastClient.SendUDPMessage(request_parts[0] + ",RM4,false," + result[1], Constants.IP_RM4,  Constants.PORT_RMs);
//                    break;
//                case Constants.OP_CANCEL_EVENT:
//                    result = (eventManagement.cancelEvent(user.toString(), request_parts[3], request_parts[4]));
//                    if (Boolean.valueOf(result[0]))
//                        UDPMulticastClient.SendUDPMessage(
//                                request_parts[0] + ",RM4,true," + "Event (" + request_parts[3] + ") successfully cancelled", Constants.IP_RM4,  Constants.PORT_RMs);
//                    else
//                        UDPMulticastClient.SendUDPMessage(request_parts[0] + ",RM4,false," + result[1], Constants.IP_RM4,  Constants.PORT_RMs);
//                    break;
//                case Constants.OP_SWAP_EVENT:
//                    result = (eventManagement.swapEvent(user.toString(), request_parts[3], request_parts[4], request_parts[5],
//                            request_parts[6]));
//                    if (Boolean.valueOf(result[0]))
//                        UDPMulticastClient.SendUDPMessage(request_parts[0] + ",RM4,true," + "Event (" + request_parts[3] + ") swapped successfully", Constants.IP_RM4,  Constants.PORT_RMs);
//                    else
//                        UDPMulticastClient.SendUDPMessage(request_parts[0] + ",RM4,false," + result[1], Constants.IP_RM4,  Constants.PORT_RMs);
//                    break;
//                case Constants.OP_GET_BOOKING_SCHEDULE:
//                    String[] schedule = eventManagement.getBookingSchedule(user.toString());
//                    for (String event : schedule)
//                        events += "'" + event + "',";
//
//                    UDPMulticastClient.SendUDPMessage(request_parts[0] + ",RM4,true," + events, Constants.IP_RM4,  Constants.PORT_RMs);
//                    break;
//            }
//            request = UDPMulticastServer.ReceiveUDPMessage(Constants.PORT_SEQUENCER);
//        }
//
//    }
//}