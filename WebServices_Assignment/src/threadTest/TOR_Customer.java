package threadTest;

import com.web.service.InterfaceEventManagement;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.URL;

public class TOR_Customer implements Runnable{
    private static InterfaceEventManagement eventObj;
    public void run(){
        try{
            URL otwURL = new URL("http://localhost:8082/toronto?wsdl");
            QName otwQName = new QName("http://impl.service.web.com/", "EventManagementService");
            Service ottawaService = Service.create(otwURL, otwQName);

            eventObj = ottawaService.getPort(InterfaceEventManagement.class);
            eventObj.addEvent("TORM1234","TORE110619", "SEMINAR", 1);
            eventObj.bookEvent("TORC1234", "TORE110619","SEMINAR");
            String [] eventMap = eventObj.getBookingSchedule("TORC1234");
            if (eventMap != null)
                for (int i = 0; i < eventMap.length; i++) {
                    System.out.println("TORC1234 : "+eventMap[i]);
                }
            else
                System.out
                        .println("There was some problem in getting the booking schedule. Please try again later.");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
