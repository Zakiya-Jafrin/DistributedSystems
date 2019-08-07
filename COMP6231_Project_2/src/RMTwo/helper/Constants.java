//package RMTwo.helper;
//
//
//public class Constants {
//    public static final String MANAGER_LOG_DIRECTORY = "./src/logResult/manager/";
//    public static final String CUSTOMER_LOG_DIRECTORY = "./src/logResult/customer/";
//    public static final String SERVER_LOG_DIRECTORY = "./src/logResult/server/";
//
//
//    public static final String EMPTYSTRING = "";
//    public static final String CAPACITY = "capacity";
//    public static final String CUSTOMERS_ENROLLED = "customersEnrolled";
//    public static final String CUSTOMER_IDS = "customerIds";
//    public static final String CUSTOMER_ID = "customerId";
//    public static final String EVENT_ID = "eventId";
//    public static final String EVENT_TYPE = "eventType";
//    public static final String NEW_EVENT_TYPE = "newEventType";
//    public static final String NEW_EVENT_ID = "newEventId";
//    public static int MAX_CROSS_EVENTS = 3;
//
//
//    public static final String LOG_MSG = "METHOD[%s]; PARAMETERS%s; STATUS[%s]; SERVER_MESSAGE[%s]";
//    public static final String OP_ADD_EVENT = "addEvent";
//    public static final String OP_REMOVE_EVENT = "removeEvent";
//    public static final String OP_LIST_EVENT_AVAILABILITY = "listEventAvailability";
//    public static final String OP_BOOK_EVENT = "bookEvent";
//    public static final String OP_GET_BOOKING_SCHEDULE = "getBookingSchedule";
//    public static final String OP_CANCEL_EVENT = "cancelEvent";
//    public static final String OP_SWAP_EVENT = "swapEvent";
//
//    public static final int PORT_FE = 1234;
//    public static final int PORT_SE_TO_RM = 4321;
//    public static final int PORT_RM1 = 1233;
//    public static final int PORT_RM2 = 1232;
//    public static final int PORT_RM3 = 1231;
//    public static final int PORT_RM4 = 1230;
//    public static final int PORT_RMs = 3141;
//    public static final String IP_RM1 = "192.168.1.255";
//    public static final String IP_RM2 = "192.168.1.255";
//    public static final String IP_RM3 = "10.0.1.6";
//    public static final String IP_RM4 = "172.31.121.151";
//    public static final String IP_LISTENER = "233.0.0.0";
//    public static final String IP_SENDER = "172.16.1.255";
////    public static final String IP_SENDER = "172.31.121.151";
//
//}


package RMTwo.helper;


public class Constants {
    public static final String MANAGER_LOG_DIRECTORY = "./src/logResult/manager/";
    public static final String CUSTOMER_LOG_DIRECTORY = "./src/logResult/customer/";
    public static final String SERVER_LOG_DIRECTORY = "./src/logResult/server/";


    public static final String EMPTYSTRING = "";
    public static final String CAPACITY = "capacity";
    public static final String CUSTOMERS_ENROLLED = "customersEnrolled";
    public static final String CUSTOMER_IDS = "customerIds";
    public static final String CUSTOMER_ID = "customerId";
    public static final String EVENT_ID = "eventId";
    public static final String EVENT_TYPE = "eventType";
    public static final String NEW_EVENT_TYPE = "newEventType";
    public static final String NEW_EVENT_ID = "newEventId";
    public static int MAX_CROSS_EVENTS = 3;


    public static final String LOG_MSG = "METHOD[%s]; PARAMETERS%s; STATUS[%s]; SERVER_MESSAGE[%s]";
    public static final String OP_ADD_EVENT = "addEvent";
    public static final String OP_REMOVE_EVENT = "removeEvent";
    public static final String OP_LIST_EVENT_AVAILABILITY = "listEventAvailability";
    public static final String OP_BOOK_EVENT = "bookEvent";
    public static final String OP_GET_BOOKING_SCHEDULE = "getBookingSchedule";
    public static final String OP_CANCEL_EVENT = "cancelEvent";
    public static final String OP_SWAP_EVENT = "swapEvent";

    public static final int PORT_SE_TO_RM = 1233; //multicast request to RMs
    public static final int PORT_FE_TO_RM1 = 1234; //for sending crash report to RM1
    public static final int PORT_FE_TO_CLIENT = 1235; //for sending correct message to client
    public static final int PORT_HEARTBEAT = 1236; //for sending and receiving heartbeats
    public static final int PORT_RMs = 3141; //reply back to FE after processing request
//    public static final String IP_RM1 = "132.205.46.255";
//    public static final String IP_RM2 = "132.205.46.255";
//    public static final String IP_RM3 = "132.205.46.255";
//    public static final String IP_RM4 = "132.205.46.255";
//    public static final String IP_LISTENER = "230.0.0.0";
//    public static final String IP_SENDER = "132.205.46.255";
//    public static final int PORT_FE = 1234;
//    public static final int PORT_SEQUENCER = 4321;
    public static final int PORT_RM1 = 3333;
    public static final int PORT_RM2 = 4444;
    public static final int PORT_RM3 = 5555;
    public static final int PORT_RM4 = 6666;
//    public static final int PORT_RMs = 3141;
//    public static final String IP_RM1 = "172.31.89.74";
    public static final String IP_RM1 = "172.31.121.151";
    public static final String IP_RM2 = "172.31.121.151";
//    public static final String IP_RM3 = "172.31.89.74";
    public static final String IP_RM3 = "172.31.121.151";
    public static final String IP_RM4 = "172.31.121.151";
    public static final String IP_LISTENER = "233.0.0.0";
    public static final String IP_SENDER = "172.16.1.255";
//    public static final String IP_SENDER = "172.31.121.151";

}