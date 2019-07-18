package threadTest;

import client.CustomerClient;
import client.User;
import helper.Role;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Customer implements Runnable {
    public void run() {
        Thread t = null;
        try {
            User user = new User();
            if (user.getRole() == Role.CUSTOMER) {
                t = new Thread(new CustomerClient(user));
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
