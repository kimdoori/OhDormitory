package kr.hs.emirim.uuuuri.ohdormitory.Model;

/**
 * Created by 유리 on 2017-10-04.
 */

//https://stackoverflow.com/questions/34110853/writing-array-in-firebase-android
public class StudentScore {
    private String date;
    private int type;

    public String getDate() {
        return date;
    }

    public int getType() {
        return type;
    }

    public StudentScore(String date, int type) {
        this.date = date;
        this.type = type;
    }
}
