package kr.hs.emirim.uuuuri.ohdormitory.Model;

/**
 * Created by doori on 2017-10-01.
 */
public class NoticeCardViewData {
    private String notice_kind;
    private String w_time;
    private String d_time;
    private String notice_title;
    private String content;
    private String clean[];
    //0번째방에 저장되는 호수는 세면실 담당
    // 0 : 세면실
    // 1 : 세탁실/탈의실
    // 2: 샤워실
    // 3 : 복도
    // 4 : 휴게실
    // 5: 계단
    // 6 : 화장실

    public NoticeCardViewData(String notice_kind, String w_time, String d_time, String notice_title) {
        this.notice_kind = notice_kind;
        this.w_time = w_time;
        this.d_time = d_time;
        this.notice_title = notice_title;
    }

    public NoticeCardViewData(String notice_kind, String w_time, String d_time, String notice_title, String content) {
        this.notice_kind = notice_kind;
        this.w_time = w_time;
        this.d_time = d_time;
        this.notice_title = notice_title;
        this.content = content;
    }

    public NoticeCardViewData(String notice_kind, String w_time, String d_time, String notice_title, String[] clean) {
        this.notice_kind = notice_kind;
        this.w_time = w_time;
        this.d_time = d_time;
        this.notice_title = notice_title;
        this.clean = clean;
    }

    public String getNotice_kind() {
        return notice_kind;
    }

    public String getW_time() {
        return w_time;
    }

    public String getD_time() {
        return d_time;
    }

    public String getNotice_title() {
        return notice_title;
    }

    public String getContent() {
        return content;
    }

    public String[] getClean() {
        return clean;
    }
}
