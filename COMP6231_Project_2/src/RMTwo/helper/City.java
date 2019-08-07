package RMTwo.helper;

public enum City {

    MTL, TOR, OTW, FRONTEND(1333);
    int udpPort;
    private City() {
    }

    private City(int udpPort) {
        this.udpPort = udpPort;
    }

    public int getUdpPort() {
        return udpPort;
    }

    public static boolean CityExist(String city) {
        for (City c : City.values()) {
            if (c.toString().equals(city.toUpperCase()))
                return true;
        }
        return false;
    }
}
//-Djava.net.preferIPv4Stack=true
