package kr.hs.emirim.uuuuri.ohdormitory.Model;

/**
 * Created by 유리 on 2017-10-03.
 */

public class SleepOut {
    private String parentNumber;
    private String type;

    public String getParentNumber() {
        return parentNumber;
    }

    public String getType() {
        return type;
    }

    public SleepOut(String parentNumber, String type) {
        this.parentNumber = parentNumber;
        this.type = type;
    }
}

