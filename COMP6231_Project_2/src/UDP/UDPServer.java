package UDP;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

import RMOne.Enums;
import RMOne.Event;
import RMOne.Enums.CityCode;
import RMOne.Enums.EventType;
import RMOne.City;

public class UDPServer {
//	static int portnumber;
//	static String multicastaddress;
//	public UDPServer(int port, String mcaddress)
//	{
//		portnumber = port;
//		multicastaddress = mcaddress;
//	}
//	public static void main(String[] args) throws Exception {
//		if(portnumber == 0)
//			portnumber = 8284;
//		 //DatagramSocket serverSocket = new DatagramSocket(portnumber);
//
//	     while(true)
//	        {
//		 	   MulticastSocket serverSocket = new MulticastSocket(portnumber);
//			   serverSocket.joinGroup(InetAddress.getByName(multicastaddress));
//
//			   byte[] receiveData = new byte[576];
//			   System.out.println("UDP Server started on port " + portnumber);
//	           DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
//	           serverSocket.receive(receivePacket);
//	           String sentence = new String( receivePacket.getData(),0,receivePacket.getLength()).toUpperCase();
//	           System.out.println("RECEIVED: " + sentence);
//	           String requestedoption = new String(receivePacket.getData(), 0, receivePacket.getLength());
//               //HashMap<String, Event> events = new HashMap<String, Event>();
//               String replymessage = "";
//               /*City city = new City(Enums.CityCode.MTL);
//               String[] result = new String[100];
//               switch(requestedoption.substring(0,1))
//                {
//                case "L":
//                	 result = city.ListEventAvailability(requestedoption.substring(2,3));
//                case "S":
//                	result = city.GetBookingSchedule(requestedoption.substring(2));
//                	break;
//                //to receive reply from the two servers
//                default:
//                	replymessage = replymessage + requestedoption;
//                }
//
//               /*
//            	ArrayList<String> eventlist = new ArrayList<String>();
//
//            	try
//            	{
//	            	events.forEach((k, v) -> {
//	            		Event ev = (Event)v;
//	            		eventlist.add(ev.ID + " " + String.valueOf(ev.BookingCapacity));
//	            	});
//            	}
//            	catch(Exception e)
//            	{
//            		e.printStackTrace();
//            	}
//
//            	replymessage = requestedoption + " - ";
//            	for(String s : eventlist)
//            	{
//            		replymessage += s + ",";
//            	}
//
//               replymessage = String.valueOf(result.length);
//	           DatagramPacket sendPacket =  new DatagramPacket(replymessage.getBytes(), replymessage.getBytes().length, receivePacket.getAddress(), receivePacket.getPort());
//	           serverSocket.send(sendPacket);
//
//	           for(int i = 0; i < result.length; i++)
//	           {
//	        	   sendPacket = new DatagramPacket(result[i].getBytes(), result[i].getBytes().length, receivePacket.getAddress(), receivePacket.getPort());
//	        	   serverSocket.send(sendPacket);
//	           }
//        	*/
//	  	     serverSocket.close();
//	        }
//	}

	public static void main(String[] args) {
		MulticastSocket aSocket = null;
		try {

			aSocket = new MulticastSocket(1313);

			aSocket.joinGroup(InetAddress.getByName("230.1.1.5"));

			System.out.println("InetAddress.getLocalHost() = " + InetAddress.getLocalHost());

			byte[] buffer = new byte[1000];
			System.out.println("Server Started............");

			while (true) {
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				System.out.println(new String(request.getData()));
				DatagramPacket reply = new DatagramPacket(request.getData(), request.getLength(), request.getAddress(),
						request.getPort());
				aSocket.send(reply);
			}

		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		} finally {
			if (aSocket != null)
				aSocket.close();
		}
	}

}
