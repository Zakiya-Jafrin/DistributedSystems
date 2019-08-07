package UDP;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import RMTwo.helper.Constants;

public class UDPMulticastClient {

    public static void main(String[] args) throws Exception {
    	//SendUDPMessage("al;sdkfj", Constants.IP_SENDER, Constants.PORT_SE_TO_RM);
    }

    public static boolean SendUDPMessage(String message, String IP,  int port) {
        DatagramSocket aSocket = null;
        try {
            aSocket = new DatagramSocket();
            byte[] m = message.getBytes();
            byte[] receiveData = new byte[1000];
            InetAddress aHost = InetAddress.getByName(IP);

            DatagramPacket request = new DatagramPacket(m, m.length, aHost, port);
            System.out.println("message to send: " + message);
            aSocket.send(request);
            return true;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;

    }
}
