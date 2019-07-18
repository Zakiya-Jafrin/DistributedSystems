package client;

import helper.City;
import helper.Role;
import helper.Utils;

import java.util.Scanner;

public class Login {
    static Scanner input = new Scanner(System.in);

    public static void main(String[] args) {

        System.out.println("WELCOME TO DISTRIBUTED EVENT MANAGEMENT SYSTEM");
        System.out.print("Please enter your ID : ");
        String id = input.next();

        User user = new User();
        String value = validateUser(id, user);

        switch (value) {

            case "success":
                System.out.println("Login Successful : " + user);
                Thread t = null;
                if (user.getRole() == Role.CUSTOMER) {
                    t = new Thread(new CustomerClient(user));
                } else {
                    t = new Thread(new ManagerClient(user));
                }
                t.start();
                break;
            default:
                System.out.println(value);
                break;
        }

    }

    private static String validateUser(final String id, final User user) {
        String returnValue = null, c, role, value;
        int userId;
        if (id.length() != 8)
            return "Seems to be an invalid id(length not equal to 8).";

        c = id.substring(0, 3);
        role = id.substring(3, 4);
        value = id.substring(4);

        // validate department
        if (!Utils.cityMatch(c))
            return "Your city('" + c + "') isn't recognized.";
            // validate role
        else if (!Utils.roleMatch(role))
            return "Your role('" + role + "') isn't recognized.";

        try {
            // validate user id (integer value)
            userId = Integer.parseInt(value);
        } catch (NumberFormatException nfe) {
            return "Your id('" + value + "') isn't recognized.";
        }
        returnValue = "success";
        user.setCity(City.valueOf(c.toUpperCase()));
        user.setRole(Role.fromString(role.toUpperCase()));
        user.setId(userId);
        return returnValue;
    }
}
