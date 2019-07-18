package helper;

public enum City {

    MTL(4445), TOR(5556), OTW(6667);
    int udpPort;

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
