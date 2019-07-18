package helper;


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
    public static final String OP_ALL_EVENLIST = "listOfAll";


}