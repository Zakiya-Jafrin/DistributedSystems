package Sequencer;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import RMTwo.helper.Constants;
import UDP.UDPClient;
import UDP.UDPMulticastClient;
import UDP.UDPMulticastServer;

public class Sequencer {
	private static volatile int MaxSequenceNumber;
	static HashMap<String, String> requests;
	private static UDPClient udpclient;
	private static boolean RM1_heartbeat;
	private static boolean RM2_heartbeat;
	private static boolean RM3_heartbeat;
	private static boolean RM4_heartbeat;
	private static boolean allReceived = false;;
	private static String resultRMOne = null, resultRMTwo = null, resultRMThree = null, resultRMFour = null;
	private static int nErrors = 0;
	
	public static void main(String[] args)
	{
		/*
		verifier("100000000,RM1,true,'Event MTLA123456 added successfully.'");
		verifier("100000000,RM2,true,'Event MTLA123456 added successfully.'");
		verifier("100000000,RM3,false,'Some random result using a pseudo-random algorithm.'");
		verifier("100000001,RM1,true,'Event MTLA123456 added successfully.'");
		verifier("100000001,RM2,true,'Event MTLA123456 added successfully.'");
		verifier("100000001,RM3,false,'Some random result using a pseudo-random algorithm.'");
		verifier("100000002,RM1,true,'Event MTLA123456 added successfully.'");
		verifier("100000002,RM2,true,'Event MTLA123456 added successfully.'");
		verifier("100000002,RM3,false,'Some random result using a pseudo-random algorithm.'");
		*/

		Sequencer seq = new Sequencer();

		//System.out.println(verifier(UDPMulticastServer.ReceiveUDPMessage(Constants.PORT_SEQUENCER)));

	}
	
	public Sequencer()
	{
		//receive heartbeats
		//boolean allalive = checkHeartbeats();
		
		MaxSequenceNumber = 100000000;
		requests = new HashMap<String, String>();
		String message = "";
//		while(message != "finish")
//		{
//			message = UDPMulticastServer.ReceiveUDPMessage(Constants.PORT_SE_TO_RM);
//			System.out.println("Message sent from sequencer? " + AddAndForwardRequest(message, Constants.IP_SENDER, Constants.PORT_SE_TO_RM));
//		}
		//System.out.println(AddAndForwardRequest("MTLM1234 addEvent MTLE100519 SEMINAR 2", Constants.IP_SENDER, Constants.PORT_SEQUENCER));
	}
	public Sequencer(int max)
	{
		MaxSequenceNumber = max;
		requests = new HashMap<String, String>();
	}

	public static synchronized boolean AddAndForwardRequest(String msg, String mcaddress, int mcport)
	{
		boolean send_result = false;
		try
		{
			requests.put(String.valueOf(MaxSequenceNumber),String.valueOf(MaxSequenceNumber) + " " + msg);
			send_result = MulticastRequests(String.valueOf(MaxSequenceNumber), mcaddress, mcport);
			//receive_result = ReceiveAndForwardResponses(send_result, feaddress, feport);
			MaxSequenceNumber++;
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			send_result = false;
		}

		return send_result;
	}

	/**********



	private static boolean checkHeartbeats()
	{
		//initialize
		RM1_heartbeat = RM2_heartbeat = RM3_heartbeat = RM4_heartbeat = false;
		TimerTask tCheckHeartbeats = new TimerTask()
		{
			public void run()
			{
				try
					{
						while(!(RM1_heartbeat && RM2_heartbeat && RM3_heartbeat))// && RM4_heartbeat))
						{
							String rm = UDPMulticastServer.ReceiveUDPMessage(Constants.PORT_RMs);
							if(!RM1_heartbeat && rm.contains("RM1"))
							{
								RM1_heartbeat = true;
								System.out.println("RM1 alive...");
							}
							if(!RM1_heartbeat && rm.contains("RM2"))
							{
								RM2_heartbeat = true;
								System.out.println("RM2 alive...");
							}
							if(!RM1_heartbeat && rm.contains("RM3"))
							{
								RM3_heartbeat = true;
								System.out.println("RM3 alive...");
							}
							if(!RM1_heartbeat && rm.contains("RM4"))
							{
								RM4_heartbeat = true;
								System.out.println("RM4 alive...");
							}
						}

					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				System.out.println("All hearbeats received: " + (RM1_heartbeat && RM2_heartbeat && RM3_heartbeat) && RM4_heartbeat));
			}
		};
		
		Timer t = new Timer();
		t.schedule(tCheckHeartbeats, 10000L, 10000L);
		
		return RM1_heartbeat && RM2_heartbeat && RM3_heartbeat && RM4_heartbeat;
		 }
		 */

	public static boolean MulticastRequests(String key, String address, int port)
	{
		boolean result;
		try
		{
			result = UDPMulticastClient.SendUDPMessage(requests.get(key), address, port);
			return result;
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			return false;
		}
	}
}
