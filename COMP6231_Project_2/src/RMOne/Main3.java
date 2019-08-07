package RMOne;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import RMOne.Enums.CityCode;
import RMTwo.helper.Constants;
import UDP.UDPClient;
import UDP.UDPMulticastClient;
import UDP.UDPMulticastServer;
import UDP.UDPServer;

public class Main3 {
	public static City mtl;
	public static City otw;
	public static City tor;
	private static UDPServer udpserver;
	private static UDPClient udpclient;
	private static String sequencer_address;
	private static int sequencer_port;
	
	public static void main(String[] args) {
		try {
			mtl = new City(Enums.CityCode.MTL);
			System.out.println("Montreal server started...");
			otw = new City(Enums.CityCode.OTW);
			System.out.println("Ottawa server started...");
			tor = new City(Enums.CityCode.TOR);
			System.out.println("Toronto server started...");			//udpserver = new UDPServer(Integer.valueOf(args[0]), args[1]);

			//sendHeartbeats();

			
			//udpclient = new UDPClient(sequencer_address, sequencer_port);
//			String request = "100000001 MTLM1234 " + Constants.OP_ADD_EVENT + " MTLE100519 SEMINAR 2";
//			UDPMulticastServer.ReceiveUDPMessage(Constants.PORT_SE_TO_RM);//"";
			String request = UDPMulticastServer.ReceiveUDPMessage(Constants.PORT_SE_TO_RM);
			//
//			String request = "100000002 MTLM1234 " + Constants.OP_REMOVE_EVENT + " MTLE110619 S";
//			String request = "100000003 MTLM1234 " + Constants.OP_LIST_EVENT_AVAILABILITY + " A";
//			String request = "100000004 MTLM1234 " + Constants.OP_BOOK_EVENT + " MTLE100519 S";
//			String request = "100000005 MTLM1234 " + Constants.OP_CANCEL_EVENT + " MTLE100519 S";
//			String request = "100000006 MTLM1234 " + Constants.OP_SWAP_EVENT + " MTLE110619 S MTLE100519 S";
//			String request = "100000007 MTLM1234 " + Constants.OP_GET_BOOKING_SCHEDULE + " A";
			String events = "";
			while (request != "") {
				
				if(request.contains("Crash Report")){
					
				}
				String[] request_parts = request.split(" ");
				// check if request has already been processed
				// endcheck
				User user = new User(request_parts[1]);
				City city;
				CityCode code = user.getCity();
				String[] result;

				switch (code) {
				case MTL:
					city = mtl;
					break;
				case TOR:
					city = tor;
					break;
				case OTW:
					city = otw;
					break;
				default:
					city = mtl;
					break;
				}

				switch (request_parts[2]) {
				case Constants.OP_ADD_EVENT:
					if (city.addEvent(user.getID(), request_parts[3], request_parts[4],
							Integer.valueOf(request_parts[5]))) {
						UDPMulticastClient.SendUDPMessage(request_parts[0] + ",RM3,true," + "'Event " + request_parts[3] + " added successfully.'", Constants.IP_RM3, Constants.PORT_RMs);
					} else {
						UDPMulticastClient.SendUDPMessage(request_parts[0] + ",RM3,false," + "'Error adding event " + request_parts[3] + "..'", Constants.IP_RM3, Constants.PORT_RMs);
					}
					break;
				case Constants.OP_REMOVE_EVENT:
					if (city.removeEvent(user.getID(), request_parts[3], request_parts[4])) {
						UDPMulticastClient.SendUDPMessage(
								request_parts[0] + ",RM3,true," + "'Event " + request_parts[3] + " removed successfully'", Constants.IP_RM3, Constants.PORT_RMs);
					} else {
						UDPMulticastClient.SendUDPMessage(request_parts[0] + ",RM3,false," + "'Error removing event " + request_parts[3] + "'", Constants.IP_RM3, Constants.PORT_RMs);
					}
					break;
				case Constants.OP_LIST_EVENT_AVAILABILITY:
					result = city.listEventAvailability(user.getID(), request_parts[3]);
					for (int i = 0; i < result.length; i++) {
						events += "'" + (result[i]) + "'";
						if (i < result.length - 1)
							events += ",";
					}
					UDPMulticastClient.SendUDPMessage(request_parts[0] + ",RM3,true," + events, Constants.IP_RM3, Constants.PORT_RMs);
					break;
				case Constants.OP_BOOK_EVENT:
					result = (city.bookEvent(user.getID(), request_parts[3], request_parts[4]));
					if (Boolean.valueOf(result[0]))
						UDPMulticastClient.SendUDPMessage(request_parts[0] + ",RM3,true," + "'Event (" + request_parts[3] + ") booked successfully'", Constants.IP_RM3, Constants.PORT_RMs);
					else
						UDPMulticastClient.SendUDPMessage(request_parts[0] + ",RM3,false," + result[1], Constants.IP_RM3, Constants.PORT_RMs);
					break;
				case Constants.OP_CANCEL_EVENT:
					result = (city.cancelEvent(user.getID(), request_parts[3], request_parts[4]));
					if (Boolean.valueOf(result[0]))
						UDPMulticastClient.SendUDPMessage(
								request_parts[0] + ",RM3,true," + "Event (" + request_parts[3] + ") successfully cancelled", Constants.IP_RM3, Constants.PORT_RMs);
					else
						UDPMulticastClient.SendUDPMessage(request_parts[0] + ",RM3,false," + result[1], Constants.IP_RM3, Constants.PORT_RMs);
					break;
				case Constants.OP_SWAP_EVENT:
					result = (city.swapEvent(user.getID(), request_parts[3], request_parts[4], request_parts[5],
							request_parts[6]));
					if (Boolean.valueOf(result[0]))
						UDPMulticastClient.SendUDPMessage(request_parts[0] + ",RM3,true," + "Event (" + request_parts[3] + ") swapped successfully", Constants.IP_RM3, Constants.PORT_RMs);
					else
						UDPMulticastClient.SendUDPMessage(request_parts[0] + ",RM3,false," + result[1], Constants.IP_RM3, Constants.PORT_RMs);
					break;
				case Constants.OP_GET_BOOKING_SCHEDULE:
					String[] schedule = city.getBookingSchedule(user.getID());
					for (String event : schedule)
						events += "'" + event + "',";
					UDPMulticastClient.SendUDPMessage(request_parts[0] + ",RM3,true," + events, Constants.IP_RM3, Constants.PORT_RMs);
					break;
				}
				request = UDPMulticastServer.ReceiveUDPMessage(Constants.PORT_SE_TO_RM);
//
//				if(request.contains(Constants.OP_ADD_EVENT) && request.contains("MTLE100519"))
//					request = "100000003 MTLM1234 " + Constants.OP_LIST_EVENT_AVAILABILITY + " ALL";
//				else if(request.contains(Constants.OP_LIST_EVENT_AVAILABILITY))
//					request = "100000002 MTLM1234 " + Constants.OP_REMOVE_EVENT + " MTLE110519 SEMINAR";
//				else if(request.contains(Constants.OP_REMOVE_EVENT))
//					request = "100000004 MTLM1234 " + Constants.OP_BOOK_EVENT + " MTLE100519 SEMINAR";
//				else if(request.contains(Constants.OP_BOOK_EVENT))
//					request = "100000007 MTLM1234 " + Constants.OP_GET_BOOKING_SCHEDULE + " ALL";
//				else if(request.contains(Constants.OP_GET_BOOKING_SCHEDULE))
//					request = "100000001 MTLM1234 " + Constants.OP_ADD_EVENT + " MTLE110619 SEMINAR 2";
//				else if(request.contains(Constants.OP_ADD_EVENT) && request.contains("MTLE110619"))
//					request = "100000006 MTLM1234 " + Constants.OP_SWAP_EVENT + " MTLE100519 SEMINAR MTLE110619 SEMINAR";
//				else if(request.contains(Constants.OP_SWAP_EVENT))
//					request = "100000005 MTLM1234 " + Constants.OP_CANCEL_EVENT + " MTLE100519 SEMINAR";
//				else if(request.contains(Constants.OP_CANCEL_EVENT))
//						request = "100000008 MTLM1234 " + Constants.OP_REMOVE_EVENT + " MTLE100519 SEMINAR";
//				else
//					request = "";
				
			}

		} catch (Exception e) {
			System.out.println("Incorect message format...");
			e.printStackTrace(System.out);
		}
	}

	public static void sendHeartbeats()
	{
		TimerTask task = new TimerTask() {
	        public void run() {
	        	try
	        	{
		        	DateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
 		            UDPMulticastClient.SendUDPMessage("RM3... Sending heartbeat at : " + dateformat.format(new Date()) +
		              " \\r\\n Thread's name: " + Thread.currentThread().getName(), Constants.IP_SENDER, Constants.PORT_RMs);	
 		            //UDPMulticastClient.SendUDPMessage("finish", Constants.IP_RM3, 1234);
	        	}
	        	catch(Exception e)
	        	{
	        		
	        	}
	        }
	    };
	    Timer timer = new Timer("Timer");
	     
	    long delay = 10000L;
	    long period = 10000L;
	    timer.schedule(task, delay, period);
	}
}
