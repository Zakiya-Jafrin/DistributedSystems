package client;

import helper.City;
import helper.Constants;
import helper.Role;

public class User {
    private City city;
    private Role role;
    private int id;

    User() {

    }

    User(City city, Role role, int id) {
        this.city = city;
        this.role = role;
        this.id = id;
    }

    public City getCity() {
        return city;
    }

    public Role getRole() {
        return role;
    }

    public int getId() {
        return id;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return city + Constants.EMPTYSTRING + role + Constants.EMPTYSTRING + id;
    }

}
