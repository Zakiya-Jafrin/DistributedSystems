package threadTest;

import EventManagementIDLApp.EventManagementIDL;

public class multithread extends Thread{
    EventManagementIDL eventObj;
    @Override
    public void run() {
        eventObj.bookEvent("TORC1234", "TORE110619","SEMINAR");
        eventObj.bookEvent("MTLC1234", "TORE110619","SEMINAR");
        eventObj.bookEvent("OTWC1234", "TORE110619","SEMINAR");
    }

    public static void main(String []args){
        Thread multiThreadTest = new Thread();
        multiThreadTest.start();
    }
}
