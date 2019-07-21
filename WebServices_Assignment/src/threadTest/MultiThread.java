package threadTest;

public class MultiThread {
    public static void main(String args[]) throws InterruptedException{
        MTL_Customer a1 = new MTL_Customer();
        OTW_Customer a2 = new OTW_Customer();
        TOR_Customer a3 = new TOR_Customer();
        Thread b[] = new Thread[20];
        Thread c[] = new Thread[20];
        Thread d[] = new Thread[20];

        for(int i = 0; i < 3; i++){
            b[i] = new Thread(a1);
            c[i] = new Thread(a2);
            d[i] = new Thread(a3);
        }

        b[0].start();
        b[1].start();
        b[2].start();
        c[0].start();
        c[1].start();
        c[2].start();
        d[0].start();
        d[1].start();
        d[2].start();


        b[0].join();
        b[1].join();
        b[2].join();
        c[0].join();
        c[1].join();
        c[2].join();
        d[0].join();
        d[1].join();
        d[2].join();

    }
}
