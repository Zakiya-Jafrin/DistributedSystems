package com.web.servers;

import com.web.service.InterfaceEventManagement;
import com.web.service.impl.EventManagement;
import helper.Constants;
import log.MyLogger;

import javax.xml.ws.Endpoint;
import java.io.File;
import java.io.IOException;

public class OTW_Server {
    private static InterfaceEventManagement implementation;

    public static void main(String args[]) {

        try {
            setupLogging();
            System.out.println("OTW Server ready and waiting ...");
            implementation = new EventManagement("OTW");
            Endpoint endpoint = Endpoint.publish("http://localhost:8081/ottawa", implementation);

            new Thread(() -> {
                ((EventManagement) implementation).UDPServer();
            }).start();

        }catch (Exception e) {
            System.err.println("ERROR: " + e);
            e.printStackTrace(System.out);
        }

//        System.out.println("OTW Server Exiting ...");
    }

    private static void setupLogging() throws IOException {
        File files = new File(Constants.SERVER_LOG_DIRECTORY);
        if (!files.exists())
            files.mkdirs();
        files = new File(Constants.SERVER_LOG_DIRECTORY + "OTW_Server.log");
        if (!files.exists())
            files.createNewFile();
        MyLogger.setup(files.getAbsolutePath());
    }
}
