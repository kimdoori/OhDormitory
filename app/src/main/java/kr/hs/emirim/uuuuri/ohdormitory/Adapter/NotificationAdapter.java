package kr.hs.emirim.uuuuri.ohdormitory.Adapter;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Date;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by 유리 on 2017-10-19.
 */

public class NotificationAdapter {
    private final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    private final String EXTRA_COUNT = "EXTRA_COUNT";

    private int mNotiCount;
    Activity mActivity;
    Context mContext;

    public NotificationAdapter(Activity mActivity, Context mContext) {
        this.mActivity = mActivity;
        this.mContext = mContext;
        mNotiCount = 0;
    }

    public void setNotification(String message, Date date){
        Intent intent = new Intent(mActivity, AlarmReceiver.class);
        intent.putExtra(EXTRA_MESSAGE, message);
        intent.putExtra(EXTRA_COUNT, mNotiCount++);
        PendingIntent servicePending = PendingIntent.getBroadcast(mActivity, 111, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Log.e("TAG", servicePending.toString());
        AlarmManager alarmManager = (AlarmManager)mContext.getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, date.getTime(), servicePending);
        Log.e("TAG", "알람 설정"+date);
    }
}

