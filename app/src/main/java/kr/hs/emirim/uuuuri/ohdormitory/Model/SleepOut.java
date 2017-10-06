package kr.hs.emirim.uuuuri.ohdormitory.Model;

/**
 * Created by 유리 on 2017-10-03.
 */

public class SleepOut {
    private String parentNumber;
    private String type;
    private String recognize;

    public String getParentNumber() {
        return parentNumber;
    }

    public String getType() {
        return type;
    }

    public String getRecognize() {
        return recognize;
    }

    public SleepOut(String parentNumber, String type,String recognize) {
        this.parentNumber = parentNumber;
        this.type = type;
        this.recognize = recognize;
    }
}

