package FE;

import IDL.EventManagementIDLPOA;
import Sequencer.Sequencer;
import UDP.UDPMulticastClient;
import UDP.UDPMulticastServer;
import org.omg.CORBA.ORB;
//import com.fasterxml.jackson.annotation.JsonInclude;

import RMTwo.helper.*;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FrontEndImplementation extends EventManagementIDLPOA{

    private ORB orb;
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private City City;
//    static String resultRMOne;
//    static String resultRMTwo;
//    static String resultRMThree;
//    static String resultRMFour;
//    static boolean allReceived;
    private static String result_received = ",";

    private static boolean allReceived = false;
    private static boolean test = true;
    private static String resultRMOne = null, resultRMTwo = null, resultRMThree = null, resultRMFour = null;
    private static int nErrors = 0;
    private static Vector vector=new Vector();
    private static int[] arFalse = new int[4];
    private static String final_result = null;



//    public static String result;

    private Sequencer seq = new Sequencer();

    // in-memory database
    private HashMap<String, HashMap<String, HashMap<String, Object>>> cityDatabase;

    public FrontEndImplementation(){
        super();
    }

    public FrontEndImplementation(String cityName){
        this.City = City.valueOf(cityName);
        if (!cityName.equals("FRONTEND")){
            cityDatabase = new HashMap<>();
        }
    }

    public void setORB(ORB orb_val) {
        orb = orb_val;
    }

    public boolean addEvent(String managerID, String eventID, String eventType, int bookingCapacity){
        boolean status = false;
        String bookingCap = " "+ bookingCapacity;
        sendRequest(Constants.OP_ADD_EVENT,managerID,eventID,eventType, bookingCap, null, null);

        String result_received = ReceiveUDPMessageForFront(Constants.PORT_RMs);
//        System.out.println("testing: "+result_received);
//        arrayOfResult.add("100000000,RM4,true,'Event MTLA123456 added successfully.'");
        while(!allReceived){
            verifier(arrayOfResult.get(0));
            verifier(arrayOfResult.get(1));
            verifier(arrayOfResult.get(2));
//            verifier(arrayOfResult.get(3));
        }
        String [] splitMessage = final_result.split(",");
        status = Boolean.parseBoolean(splitMessage[2]);
        return status;
    }

    public boolean removeEvent(String managerID, String eventID, String eventType){
        boolean status = false;
        sendRequest(Constants.OP_REMOVE_EVENT,managerID,eventID,eventType," "+null, null, null);
//        waitForResponse();
        String result_received = ReceiveUDPMessageForFront(Constants.PORT_RMs);
//        System.out.println("testing: "+result_received);
//        arrayOfResult.add("100000000,RM4,true,'Event MTLA123456 added successfully.'");
        while(!allReceived){
            verifier(arrayOfResult.get(0));
            verifier(arrayOfResult.get(1));
            verifier(arrayOfResult.get(2));
//            verifier(arrayOfResult.get(3));
        }
        String [] splitMessage = final_result.split(",");
        status = Boolean.parseBoolean(splitMessage[2]);

        return status;
    }

    public String[] listEventAvailability(String managerId, String eventType){
        sendRequest(Constants.OP_LIST_EVENT_AVAILABILITY, managerId,null, eventType," "+null , null, null);
//        waitForResponse();
        ArrayList<String> result = new ArrayList<String>();
//        String result[] = new String[2];
//        String [] splitMessage = verifier().split(",");


        String result_received = UDPMulticastServer.ReceiveUDPMessage(Constants.PORT_RMs);
        while(!allReceived){
            verifier(arrayOfResult.get(0));
            verifier(arrayOfResult.get(1));
            verifier(arrayOfResult.get(2));
//            verifier(arrayOfResult.get(3));
        }
        String [] splitMessage = final_result.split(",");
        for (int i =3 ; i <= splitMessage.length-1; i++ ) {
            System.out.println(splitMessage[i]);
            result.add(splitMessage[i]);
        }

//        return result;
        return result.toArray(new String[result.size()]);
    }

    public String[] bookEvent(String customerId, String eventId, String eventType) {
        String result[] = new String[2];
        sendRequest(Constants.OP_BOOK_EVENT, customerId, eventId, eventType, " "+null, null, null);
//        waitForResponse();

        String result_received = UDPMulticastServer.ReceiveUDPMessage(Constants.PORT_RMs);
        while(!allReceived){
            verifier(arrayOfResult.get(0));
            verifier(arrayOfResult.get(1));
            verifier(arrayOfResult.get(2));
//            verifier(arrayOfResult.get(3));
        }
        String [] splitMessage = final_result.split(",");
        result[0] = splitMessage [2];
        result[1] = splitMessage [3];
        return result;
    }

    public String[] swapEvent(String customerId, String newEventId, String newEventType, String oldEventId, String oldEventType) {
        sendRequest(Constants.OP_SWAP_EVENT, customerId,oldEventId,oldEventType," "+null,newEventId,newEventType);
//        waitForResponse();
        String [] result = new String[2];
        String result_received = UDPMulticastServer.ReceiveUDPMessage(Constants.PORT_RMs);
        while(!allReceived){
            verifier(arrayOfResult.get(0));
            verifier(arrayOfResult.get(1));
            verifier(arrayOfResult.get(2));
//            verifier(arrayOfResult.get(3));
        }
        String [] splitMessage = final_result.split(",");
        result[0] = splitMessage [2];
        result[1] = splitMessage [3];
        return result;
    }

    public String[] getBookingSchedule(String customerId){
        sendRequest(Constants.OP_GET_BOOKING_SCHEDULE, customerId, null, null," "+null, null, null);
//        waitForResponse();
        ArrayList<String> result = new ArrayList<String>();
        String result_received = UDPMulticastServer.ReceiveUDPMessage(Constants.PORT_RMs);
        while(!allReceived){
            verifier(arrayOfResult.get(0));
            verifier(arrayOfResult.get(1));
            verifier(arrayOfResult.get(2));
//            verifier(arrayOfResult.get(3));
        }
        String [] splitMessage = final_result.split(",");
        for (int i =3 ; i <= splitMessage.length-1; i++ ){
            System.out.println(splitMessage[i]);
            result.add(splitMessage[i]);
        }
        return result.toArray(new String[result.size()]);
    }

    public String[] cancelEvent(String customerId, String eventId, String eventType) {
        sendRequest(Constants.OP_CANCEL_EVENT, customerId, eventId, eventType," "+null, null, null);
//        waitForResponse();
        String [] result = new String[2];
        String result_received = UDPMulticastServer.ReceiveUDPMessage(Constants.PORT_RMs);
        while(!allReceived){
            verifier(arrayOfResult.get(0));
            verifier(arrayOfResult.get(1));
            verifier(arrayOfResult.get(2));
//            verifier(arrayOfResult.get(3));
        }
        String [] splitMessage = final_result.split(",");
        result[0] = splitMessage [2];
        result[1] = splitMessage [3];
        return result;
    }

    private void sendRequest(String function, String userId, String eventId, String eventType, String bookingCapacity,
                            String newEventId, String newEventType) {
        String dataFromClient = Stream.of(userId, " "+function," "+newEventId, " "+newEventType," "+eventId," "+ eventType, bookingCapacity)
                .filter(s -> !s.equals(" " + null))
                .collect(Collectors.joining());
//        if(Sequencer.checkHeartbeats()){
//            System.out.println("Message sent from : " + Sequencer.AddAndForwardRequest(dataFromClient, Constants.IP_LISTENER, Constants.PORT_SE_TO_RM));
            Sequencer.AddAndForwardRequest(dataFromClient, Constants.IP_LISTENER, Constants.PORT_SE_TO_RM);
//        }
    }


    private static boolean verifier(String result_received) {
        System.out.println("Timer for receiving responses started...");
        String result = result_received;

        String resultRMOneArray[] = null, resultRMTwoArray[] = null, resultRMThreeArray[]=null, resultRMFourArray[]=null;


        String falseResult = "";
        //while(!allReceived){
        String sequenceNumber = result.split(",")[0];
        try {
            if (result != null && result.contains("RM1")) {
                resultRMOne = result;
                if(resultRMOne.contains("true"))
                {
                    vector.add("RM1");
                    System.out.println("Receive from RM1 ...");
                }
                else
                    arFalse[0]++;
            }
            if (result != null && result.contains("RM2")) {
                resultRMTwo = result;
                if(resultRMTwo.contains("true"))
                {
                    vector.add("RM2");
                    System.out.println("Receive from RM2 ...");
                }
                else
                    arFalse[1]++;
            }
            if (result != null && result.contains("RM3")) {
                resultRMThree = result;
                if(resultRMThree.contains("true"))
                {
                    vector.add("RM3");
                    System.out.println("Receive from RM3 ...");
                }
                else
                    arFalse[2]++;
            }
            if (result != null && result.contains("RM4")) {
                resultRMFour = result;
                if(resultRMFour.contains("true"))
                {
                    vector.add("RM1");
                    System.out.println("Receive from RM1 ...");
                }
                else
                    arFalse[3]++;
            }
            if((resultRMOne!=null && resultRMTwo !=null && resultRMThree !=null) )// && resultRMFour !=null)
            {
                resultRMOneArray = resultRMOne.split(",");
                resultRMTwoArray = resultRMTwo.split(",");
                resultRMThreeArray = resultRMThree.split(",");
                //resultRMFourArray = resultRMFour.split(",");
                allReceived = true;
                System.out.println("All Packets are received in Front-End: ");
                if((sequenceNumber.equals(resultRMOneArray[0]))&& (sequenceNumber.equals(resultRMTwoArray[0]))//){
                        && (sequenceNumber.equals(resultRMThreeArray[0]))) //{  && (sequenceNumber.equals(resultRMFourArray[0]))){
//	 	                   if((sequenceNumber.equals(resultRMThreeArray[0])) && (sequenceNumber.equals(resultRMFourArray[0]))){
                    if(vector.size() >= 2){
                        if(vector.contains("RM1")) {
                            UDPMulticastClient.SendUDPMessage(resultRMOne, Constants.IP_SENDER, Constants.PORT_FE_TO_CLIENT);
                            final_result = resultRMOne;
                        }
                        else if(vector.contains("RM2")) {
                            UDPMulticastClient.SendUDPMessage(resultRMTwo, Constants.IP_SENDER, Constants.PORT_FE_TO_CLIENT);
                            final_result = resultRMTwo;
                        }
                        else if(vector.contains("RM3")) {
                            UDPMulticastClient.SendUDPMessage(resultRMThree, Constants.IP_SENDER, Constants.PORT_FE_TO_CLIENT);
                            final_result = resultRMThree;
                        }
                        else if(vector.contains("RM4")){
                            UDPMulticastClient.SendUDPMessage(resultRMFour, Constants.IP_SENDER, Constants.PORT_FE_TO_CLIENT);
                            final_result = resultRMFour;
//                            System.out.println(resultRMOne);
                        }
                    }
                if(arFalse[0] == 3 || arFalse[1] == 3 || arFalse[2] == 3 || arFalse[3] == 3)
                {
                    System.out.println("One incorrect result for 3 or more times");
                    UDPMulticastClient.SendUDPMessage("Crash Report: RM3", Constants.IP_SENDER, Constants.PORT_FE_TO_RM1);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        //}

//	UDPMulticastClient.SendUDPMessage("Crash Report: Calling RM3...", Constants.IP_SENDER, Constants.PORT_RMs);
        return allReceived;
    }

    public static String message = "";
    static MulticastSocket aSocket = null;
    static StringBuilder sb = new StringBuilder();
    static ArrayList<String> arrayOfResult = new ArrayList<>();
    public static String ReceiveUDPMessageForFront(int port) {
        try
        {
            aSocket = new MulticastSocket(port);
            aSocket.joinGroup(InetAddress.getByName(Constants.IP_LISTENER));
            System.out.println("Server Started............");
            int counter =0;
            while(counter<4) {
                byte[] buffer = new byte[1000];
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                System.out.println("Receiving...");
                aSocket.receive(request);
                message = new String(request.getData(),0,request.getLength());

                System.out.println(message);
//                sb.append(message);
//                System.out.println(sb);
                arrayOfResult.add(message);


//                DatagramPacket reply = new DatagramPacket(request.getData(), request.getLength(), request.getAddress(),
//                        request.getPort());
//                aSocket.send(reply);
                counter++;
//                System.out.print(counter);
            }


        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        } finally {
            if (aSocket != null)
                aSocket.close();

            return message;
        }
    }

    private void waitForResponse(){

        try {
            System.out.println("waiting for result...");
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void shutdown() {
        orb.shutdown(false);
    }

}
