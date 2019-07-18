package helper;

import java.io.*;
import java.util.AbstractMap.SimpleEntry;

public class Utils {

    public static SimpleEntry<Boolean, String> validateUser(final String id, final Role userRole, final City city) {
        String c, role, value;
        // string length !=9
        if (id.length() != 8)
            return new SimpleEntry<Boolean, String>(false, "Seems to be an invalid id(length not equal to 8).");

        c = id.substring(0, 3).toUpperCase();
        role = id.substring(3, 4).toUpperCase();
        value = id.substring(5);

        // validate City
        if (!cityMatch(c))
            return new SimpleEntry<Boolean, String>(false, "The City('" + c + "') isn't recognized.");
        else if (city != null && city != City.valueOf(c))
            return new SimpleEntry<Boolean, String>(false, "You are not authorized for this city('" + c + "').");
        else if (!roleMatch(role))
            return new SimpleEntry<Boolean, String>(false, "The role('" + role + "') isn't correct.");
        else if (role != null && userRole != Role.fromString(role)) {
            return new SimpleEntry<Boolean, String>(false, "This operation is invalid for a Manager('" + role + "').");
        }

        try {
            Integer.parseInt(value);
        } catch (NumberFormatException nfe) {
            return new SimpleEntry<Boolean, String>(false, "The id('" + value + "') isn't correct.");
        }

        return new SimpleEntry<Boolean, String>(true, "valid");
    }

    public static SimpleEntry<Boolean, String> validateEvent(final String eventId) {
        return validateEvent(eventId, null);

    }

    public static SimpleEntry<Boolean, String> validateEvent(final String eventId, City city) {

        if (eventId.length() != 10)
            return new SimpleEntry<Boolean, String>(false, "Seems to be an invalid event(length not equal to 10).");
        String c, value;

        c = eventId.substring(0, 3).toUpperCase();
        value = eventId.substring(4);

        if (!Utils.cityMatch(c))
            return new SimpleEntry<Boolean, String>(false, "The city('" + c + "') isn't recognized.");
        else if (city != null && city != City.valueOf(c))
            return new SimpleEntry<Boolean, String>(false, "You are not authorized for this city('" + c + "').");
        try {
            Integer.parseInt(value);
        } catch (NumberFormatException nfe) {
            return new SimpleEntry<Boolean, String>(false, "Event id('" + value + "') isn't valid.");
        }

        return new SimpleEntry<Boolean, String>(true, "valid");
    }

    public static SimpleEntry<Boolean, String> validateEventType(String eventType) {
        boolean status = (EventType.isValidEventType(eventType) != null) ? true : false;
        String msg = null;
        if (!status)
            msg = eventType + " isn't valid eventType.";
        return new SimpleEntry<Boolean, String>(status, msg);
    }

    public static byte[] objectToByteArray(Object obj) {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out;
        try {
            out = new ObjectOutputStream(byteOut);
            out.writeObject(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteOut.toByteArray();
    }

    public static Object byteArrayToObject(byte[] data) {
        ByteArrayInputStream byteIn = new ByteArrayInputStream(data);
        Object result = null;
        ObjectInputStream in;
        try {
            in = new ObjectInputStream(byteIn);
            result = (Object) in.readObject();
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean cityMatch(final String c) {
        return c.matches("(?i)MTL|TOR|OTW");
    }

    public static boolean roleMatch(final String role) {
        return role.matches("(?i)M|C");
    }

    public static int decideServerPort(String cityName) {
        int serverPort=1;
        if(cityName.equals("MTL")) {
            serverPort = 2017;
        }else if(cityName.equals("OTW")) {
            serverPort = 2018;
        }else if(cityName.equals("TOR")) {
            serverPort = 2019;
        }else {
            System.out.println("This is an invalid request. Please check your username");
        }
        return serverPort;
    }
}
