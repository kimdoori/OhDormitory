package kr.hs.emirim.uuuuri.ohdormitory.Model;

import java.io.Serializable;

/**
 * Created by 유리 on 2017-10-01.
 */

public class User implements Serializable {
    private boolean isNull;
    private boolean isWeekDay;
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

    public boolean isNull() {
        return isNull;
    }

    public User() {
        isNull = true;
    }

    public User(int roomNumber) {
        isWeekDay = true;
        this.roomNumber = roomNumber;
    }

    public User(String uid, String name, int allowCode, int roomNumber) {
        isNull = false;
        isWeekDay = false;
        this.uid = uid;
        this.name = name;
        this.allowCode = allowCode;
        this.roomNumber = roomNumber;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", allowCode=" + allowCode +
                ", roomNumber=" + roomNumber +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (isNull != user.isNull) return false;
        if (allowCode != user.allowCode) return false;
        if (roomNumber != user.roomNumber) return false;
        if (uid != null ? !uid.equals(user.uid) : user.uid != null) return false;
        return name != null ? name.equals(user.name) : user.name == null;

    }

    @Override
    public int hashCode() {
        int result = (isNull ? 1 : 0);
        result = 31 * result + (uid != null ? uid.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + allowCode;
        result = 31 * result + roomNumber;
        return result;
    }
}
