package com.web.servers;

import com.web.service.InterfaceEventManagement;
import com.web.service.impl.EventManagement;
import helper.City;
import helper.Constants;
import log.MyLogger;

import javax.xml.ws.Endpoint;
import java.io.File;
import java.io.IOException;

public class TOR_Server { private static InterfaceEventManagement implementation;
    public static void main(String args[]) {
        try {
            setupLogging();
            System.out.println("TOR Server ready and waiting ...");
            implementation = new EventManagement("TOR");
            Endpoint endpoint = Endpoint.publish("http://localhost:8082/toronto", implementation);

            new Thread(() -> {
                ((EventManagement) implementation).UDPServer();
            }).start();

        }catch (Exception e) {
            System.err.println("ERROR: " + e);
            e.printStackTrace(System.out);
        }

//        System.out.println("TOR Server Exiting ...");
    }


    private static void setupLogging() throws IOException {
        File files = new File(Constants.SERVER_LOG_DIRECTORY);
        if (!files.exists())
            files.mkdirs();
        files = new File(Constants.SERVER_LOG_DIRECTORY + "TOR_Server.log");
        if (!files.exists())
            files.createNewFile();
        MyLogger.setup(files.getAbsolutePath());
    }
}
