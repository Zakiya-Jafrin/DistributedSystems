package UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPClient {
	String hostname;
	int portnumber;
	public UDPClient(String host, int port)
	{
		hostname = host;
		portnumber = port;
	}
	
	public String[] send(String msg, boolean multicast) throws IOException{
		
		      DatagramSocket clientSocket = new DatagramSocket();
		      InetAddress IPAddress = InetAddress.getByName(hostname);
		      byte[] sendData = new byte[576];
		      byte[] receiveData = new byte[576];
		      
		      sendData = msg.getBytes();
		      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, portnumber);
		      clientSocket.send(sendPacket);

		      int i = 0;
		      int messageCount = 0;
		      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		      clientSocket.receive(receivePacket);
		      String receivedString = new String(receivePacket.getData(),0,receivePacket.getLength());
		      messageCount = Integer.valueOf(receivedString);
		      
		      String[] result = new String[messageCount];
		      while(i < messageCount)
		      {
			      receivePacket = new DatagramPacket(receiveData, receiveData.length);
			      clientSocket.receive(receivePacket);
			      receivedString = new String(receivePacket.getData(),0,receivePacket.getLength());
			      System.out.println("FROM SERVER:" + receivedString);
			      result[i] = "FROM SERVER: " + receivedString;
			      i++;
		      }
		      clientSocket.close();
		      
		      return result;
	}
}
