package kr.hs.emirim.uuuuri.ohdormitory.Model;

/**
 * Created by 유리 on 2017-10-01.
 */

public class User {
    private String uid;
    private String name;
    private int allowCode;
    private int roomNumber;


    public String getName() {
        return name;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public int getAllowCode() {
        return allowCode;
    }

    public String getUid() {
        return uid;
    }

    public User(String uid, String name, int allowCode, int roomNumber) {
        this.uid = uid;
        this.name = name;
        this.allowCode = allowCode;
        this.roomNumber = roomNumber;
    }
}
