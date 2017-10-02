package kr.hs.emirim.uuuuri.ohdormitory.Model;

/**
 * Created by 유리 on 2017-10-01.
 */

public class User {
    private String name;
    private String roomNumber;
    private int allowCode;

    public String getName() {
        return name;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public int getAllowCode() {
        return allowCode;
    }

    public User(String name, String roomNumber, int allowCode) {

        this.name = name;
        this.roomNumber = roomNumber;
        this.allowCode = allowCode;
    }
}
