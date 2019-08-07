//package FE;
//
//import java.util.Scanner;
//import java.util.Timer;
//import java.util.TimerTask;
//import java.util.Vector;
//
//import RMOne.Enums;
//import RMOne.Enums.Role;
//import RMOne.User;
//import RMTwo.helper.Constants;
//import Sequencer.Sequencer;
//import UDP.UDPClient;
//import UDP.UDPMulticastClient;
//import UDP.UDPMulticastServer;
//
//public class CORBA_Client {
//
//	static Scanner scanner = new Scanner(System.in);
//	static String command = "";
//	static User user = null;
//	static UDPClient udpclient = new UDPClient("localhost", 8284);
//
//      static String resultRMOne;
//      static String resultRMTwo;
//      static String resultRMThree;
//      static String resultRMFour;
//      static boolean allReceived;
//      static int nErrors;
//
//	public static String GetManagerCommand()
//	{
//		System.out.println("Please choose from the following menus - please type the letter in parenthesis:");
//		System.out.println("1. Add event (A)");
//		System.out.println("2. Remove event (R)");
//		System.out.println("3. List event availability (L)");
//		System.out.println("4. Book event (B)");
//		System.out.println("5. Cancel event (T)");
//		System.out.println("6. Get booking schedule (S)");
//		System.out.println("7. Swap event (W)");
//		System.out.println("8. Exit (E)");
//		return scanner.nextLine();
//	}
//	public static String GetCustomerCommand()
//	{
//		System.out.println("Please choose from the following menu - please type the letter in parenthesis:");
//		System.out.println("1. Book event (B)");
//		System.out.println("2. Cancel event (T)");
//		System.out.println("3. Get booking schedule (S)");
//		System.out.println("4. Swap event (W)");
//		System.out.println("5. Exit (E)");
//		return scanner.nextLine();
//	}
//
//	public static String GetEventID()
//	{
//		System.out.println("Please enter the event ID: ");
//		return scanner.nextLine();
//	}
//
//	public static String GetEventType(boolean includeAll)
//	{
//		System.out.println("Please enter the event type: ");
//		System.out.println("1. Seminar (S)");
//		System.out.println("2. Conference (C)");
//		System.out.println("3. TradeShow (T)");
//		if(includeAll)
//			System.out.println("4. All (A)");
//		return scanner.nextLine();
//	}
//
//	public static String GetUserID()
//	{
//		System.out.println("Please enter user ID: ");
//		return scanner.nextLine();
//	}
//
//	public static int GetBookingCapacity()
//	{
//		System.out.println("Please enter booking capacity: ");
//		return (Integer.parseInt(scanner.nextLine()));
//	}
//
//	public static void main(String[] args) {
//		//get remote object reference for calling remote methods from client:
//		try
//		{
//			/*
//			ORB orb = ORB.init(args, null);
//			//-ORBInitialPort 1050 -ORBInitialHost localhost
//			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
//			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
//
//			//populate servers
//			EventManagementIDL mtlserver = EventManagementIDLHelper.narrow(ncRef.resolve_str("MTL"));;
//			EventManagementIDL otwserver =EventManagementIDLHelper.narrow(ncRef.resolve_str("OTW"));;
//			EventManagementIDL torserver = EventManagementIDLHelper.narrow(ncRef.resolve_str("TOR"));;
//
//			EventManagementIDL evmanagerobj = mtlserver;
//			*/
//			Sequencer sequencer = new Sequencer(100000);
//			while(!command.toUpperCase().equals("E"))
//			{
//				try
//				{
//
//					//will log new user or returning user login time
//					System.out.println("Please enter your user ID: ");
//					user = new User(scanner.nextLine());
//
//					/*
//					switch(user.getCity())
//					{
//						case MTL:
//							evmanagerobj = mtlserver;
//							break;
//						case OTW:
//							evmanagerobj = otwserver;
//							break;
//						case TOR:
//							evmanagerobj = torserver;
//							break;
//						default:
//							evmanagerobj = mtlserver;
//					}
//					*/
//
//					if(user.getRole() == Role.Manager)
//					{
//						command = GetManagerCommand();
//					}
//					else
//					{
//						command = GetCustomerCommand();
//					}
//
//					//get necessary input for retrieving information
//					String eventtype = "";
//					String ID = "";
//					String eventID = "";
//					int bookingcapacity = 0;
//					String newEventID = "";
//					String newEventType = "";
//					String customerID = "";
//
//					switch(command)
//					{
//					//addevent
//						case "A":
//					//removeevent
//						case "R":
//							eventID = GetEventID();
//				        	if(Enums.CityCode.valueOf(eventID.substring(0,3)) != user.getCity())
//				        	{
//				        		System.out.println("Only events in the manager's own city can be added or removed. Please try again.");
//				        	}
//				        	else
//				        	{
//								eventtype = GetEventType(false);
//								if(command.equals("A"))
//								{
//									bookingcapacity = GetBookingCapacity();
//								}
//				        	}
//							break;
//					//bookevent
//						case "B":
//					//cancelevent
//						case "T":
//					//swap event
//						case "W":
//							if(user.getRole() == Role.Manager)
//								customerID = GetUserID();
//							else
//								customerID = user.getID();
//							eventID = GetEventID();
//							eventtype = GetEventType(false);
//							if(command.equals("W"))
//							{
//								System.out.println("New Event Information: ");
//								newEventID = GetEventID();
//								newEventType = GetEventType(false);
//							}
//							break;
//					//listevent
//						case "L":
//					//booking schedule
//						case "S":
//							eventtype = GetEventType(true);
//							break;
//					//add customer
//						case "C":
//					//add manager
//						case "M":
//							ID = GetUserID();
//							break;
//					//exit
//						case "E":
//							return;
//						default:
//							System.err.println("Please enter a valid command (letter in parenthesis)...");
//						break;
//					}
//
//					String[] result = new String[2];
//					//perform operation requested on remote object
//					switch(command)
//					{
//					case "A":
//						Sequencer.AddAndForwardRequest(user.getID() + " " + Constants.OP_ADD_EVENT + " " + eventID + " " + eventtype + " " + String.valueOf(bookingcapacity), c, Constants.PORT_SE_TO_RM);
//						String reply = UDPMulticastServer.ReceiveUDPMessage(Constants.PORT_RMs);
//						boolean result_rm1 = verifier(reply);
//						System.out.println("Received from RM1: " + result_rm1);
//						break;
//					/*
//					case "R":
//						if(evmanagerobj.removeEvent(user.getID(), eventID, eventtype))
//							System.out.println("Event removed successfully");
//						else
//							System.out.println("Error removing event - please see log for details");
//						break;
//					case "L":
//						System.out.println("Availability for event type " + eventtype + ": ");
//						String[] events = evmanagerobj.listEventAvailability(user.getID(), eventtype);
//						for(String ev : events)
//							System.out.println(ev);
//						break;
//					case "B":
//						result = (evmanagerobj.bookEvent(customerID, eventID, eventtype));
//						if(Boolean.valueOf(result[0]))
//							System.out.println("Event booked successfully");
//						else
//							System.out.println(result[1]);
//						break;
//					case "T":
//						result = (evmanagerobj.cancelEvent(customerID, eventID, eventtype));
//						if(Boolean.valueOf(result[0]))
//							System.out.println("Event successfully cancelled");
//						else
//							System.out.println(result[1]);
//						break;
//					case "W":
//						result = (evmanagerobj.swapEvent(customerID, eventID, eventtype, newEventID, newEventType));
//						if(Boolean.valueOf(result[0]))
//							System.out.println("Event swapped successfully");
//						else
//							System.out.println(result[1]);
//						break;
//					case "S":
//						String[] schedule = evmanagerobj.getBookingSchedule(user.getID());
//						for(String event : schedule)
//							System.out.println(event);
//						/*
//						if(evmanagerobj.GetBookingSchedule(user.getID()))
//							System.out.println("Please see schedule above");
//						else
//							System.out.println("Error showing schedule - please see log for details");
//
//						break;
//						*/
//						default:
//							System.out.println("Invalid input, please try again...");
//							break;
//					}
//				}
//				catch(Exception e)
//				{
//					System.err.println("An exception occurred: ");
//					e.printStackTrace(System.out);
//				}
//			}
//		}
//		catch(Exception e)
//		{
//			System.out.println("Error fetching remote object: ");
//			e.printStackTrace(System.out);
//		}
//
//	}
//	private static boolean verifier(String result_received) {
//	  	TimerTask timerForDetectingCrash = new TimerTask()
//	  			{
//	  				public void run()
//	  				{
//	  					System.out.println("Timer for receiving responses started...");
//	  			        String result = result_received;
//
//	  			        String resultRMOneArray[] = null, resultRMTwoArray[] = null, resultRMThreeArray[]=null, resultRMFourArray[]=null;
//	  			        Vector vector=new Vector();
//	  			        int[] arFalse = new int[4];
//
//	  			        String falseResult = "";
//						while(!allReceived){
//	  			           String sequenceNumber = result.split(",")[0];
//	  			            try {
//								if (result != null && result.contains("RM1")) {
//	  			                    resultRMOne = result;
//	  			                    if(resultRMOne.contains("true"))
//	  			                    {
//	  			                        vector.add("RM1");
//		  			                    System.out.println("Receive from RM1 ...");
//	  			                    }
//	  			                    else
//	  			                    	arFalse[0]++;
//	  			                }
//								if (result != null && result.contains("RM2")) {
//	  			                    resultRMTwo = result;
//	  			                    if(resultRMTwo.contains("true"))
//	  			                    {
//	  			                        vector.add("RM2");
//		  			                    System.out.println("Receive from RM2 ...");
//	  			                    }
//	  			                    else
//	  			                    	arFalse[1]++;
//	  			                }
//								if (result != null && result.contains("RM3")) {
//	  			                    resultRMThree = result;
//	  			                    if(resultRMThree.contains("true"))
//	  			                    {
//	  			                        vector.add("RM3");
//		  			                    System.out.println("Receive from RM3 ...");
//	  			                    }
//	  			                    else
//	  			                    	arFalse[2]++;
//	  			                }
//								if (result != null && result.contains("RM4")) {
//	  			                    resultRMFour = result;
//	  			                    if(resultRMFour.contains("true"))
//	  			                    {
//	  			                        vector.add("RM1");
//		  			                    System.out.println("Receive from RM1 ...");
//	  			                    }
//	  			                    else
//	  			                    	arFalse[3]++;
//	  			                }
//	  			                if(resultRMOne!=null && resultRMTwo !=null && resultRMThree !=null && resultRMFour !=null)// && resultRMFour !=null)
//	  			                {
//	  			                    resultRMOneArray = resultRMOne.split(",");
//	  			                    resultRMTwoArray = resultRMTwo.split(",");
//	  			                    resultRMThreeArray = resultRMThree.split(",");
//	  			                    //resultRMFourArray = resultRMFour.split(",");
//	  			                    allReceived = true;
//	  			                    System.out.println("All Packets are received in Front-End: ");
//	  			                    if((sequenceNumber.equals(resultRMOneArray[0]))&& (sequenceNumber.equals(resultRMTwoArray[0]))){
//	  			                    		//&& (sequenceNumber.equals(resultRMThreeArray[0]))) {  && (sequenceNumber.equals(resultRMFourArray[0]))){
////	  			                    if((sequenceNumber.equals(resultRMThreeArray[0])) && (sequenceNumber.equals(resultRMFourArray[0]))){
//	  			                        if(vector.size() >= 3){
//	  			                        	if(vector.contains("RM1"))
//	  			                        		UDPMulticastClient.SendUDPMessage(resultRMOne, Constants.IP_SENDER, Constants.PORT_FE_TO_CLIENT);
//	  			                        	else if(vector.contains("RM2"))
//	  			                        		UDPMulticastClient.SendUDPMessage(resultRMTwo, Constants.IP_SENDER, Constants.PORT_FE_TO_CLIENT);
//	  			                        	else if(vector.contains("RM3"))
//	  			                        		UDPMulticastClient.SendUDPMessage(resultRMThree, Constants.IP_SENDER, Constants.PORT_FE_TO_CLIENT);
//	  			                        	else if(vector.contains("RM4"))
//	  			                        		UDPMulticastClient.SendUDPMessage(resultRMFour, Constants.IP_SENDER, Constants.PORT_FE_TO_CLIENT);
//
//	  			                        }
//	  			                        if(arFalse[0] == 3 || arFalse[1] == 3 || arFalse[2] == 3 || arFalse[3] == 3)
//	  			                        {
//											System.out.println("One incorrect result for 3 or more times");
//	  			                        	UDPMulticastClient.SendUDPMessage("Crash Report: RM3", Constants.IP_SENDER, Constants.PORT_FE_TO_RM1);
//	  			                        }
//	  			                    }
//	  			                }
//	  			            } catch (Exception e) {
//	  			                e.printStackTrace();
//	  			            }
//	  			        }
//
////			                    UDPMulticastClient.SendUDPMessage("Crash Report: Calling RM3...", Constants.IP_SENDER, Constants.PORT_RMs);
//
//
//	  				}
//	  			};
//			  	Timer timer = new Timer();
//			  	timer.schedule(timerForDetectingCrash, 0, 10000L);
//			  	return allReceived;
//	  }
//
//
//}
