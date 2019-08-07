package UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import RMTwo.helper.Constants;

public class UDPMulticastServer {
	public static String message = "";
	static MulticastSocket aSocket = null;

//	public static void main(String[] args)
//	{
//		System.out.println(ReceiveUDPMessage(Constants.PORT_RM1));
//	}

	public static String ReceiveUDPMessage(int port) {
		try
		{
			aSocket = new MulticastSocket(port);
			aSocket.joinGroup(InetAddress.getByName(Constants.IP_LISTENER));
			System.out.println("Server Started............");
//			while(!message.equalsIgnoreCase("finish")) {
				byte[] buffer = new byte[1000];
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				System.out.println("Receiving...");
				aSocket.receive(request);
				message = new String(request.getData(),0,request.getLength());
				System.out.println(message);
				DatagramPacket reply = new DatagramPacket(request.getData(), request.getLength(), request.getAddress(),
						request.getPort());
				aSocket.send(reply);
//			}

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

	public static String ReceiveUDPMessageForFront(int port) {
		try
		{
			aSocket = new MulticastSocket(port);
			aSocket.joinGroup(InetAddress.getByName(Constants.IP_LISTENER));
			System.out.println("Server Started............");
			int counter =0;
			while(counter<3) {
				byte[] buffer = new byte[1000];
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				System.out.println("Receiving...");
				aSocket.receive(request);
				message = new String(request.getData(),0,request.getLength());
//				System.out.println(message);
				DatagramPacket reply = new DatagramPacket(request.getData(), request.getLength(), request.getAddress(),
						request.getPort());
				aSocket.send(reply);
				counter++;
				System.out.print(counter);
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
}
