package threadTest;

import com.web.service.InterfaceEventManagement;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.URL;

public class MTL_Customer implements Runnable{
    private static InterfaceEventManagement eventObj;
    public void run(){
        try{
            URL otwURL = new URL("http://localhost:8080/montreal?wsdl");
            QName otwQName = new QName("http://impl.service.web.com/", "EventManagementService");
            Service ottawaService = Service.create(otwURL, otwQName);

            eventObj = ottawaService.getPort(InterfaceEventManagement.class);
            eventObj.bookEvent("MTLC1234", "TORE110619","SEMINAR");
            String [] eventMap = eventObj.getBookingSchedule("MTLC1234");
            if (eventMap != null)
                for (int i = 0; i < eventMap.length; i++) {
                    System.out.println("MTLC1234 : "+eventMap[i]);
                }
            else
                System.out
                        .println("There was some problem in getting the booking schedule. Please try again later.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
