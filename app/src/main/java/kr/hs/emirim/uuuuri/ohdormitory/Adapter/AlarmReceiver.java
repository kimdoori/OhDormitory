package kr.hs.emirim.uuuuri.ohdormitory.Adapter;

/**
 * Created by 유리 on 2017-10-19.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.view.Gravity;
import android.widget.Toast;

import kr.hs.emirim.uuuuri.ohdormitory.R;

public class AlarmReceiver extends BroadcastReceiver {
    private final String NOTIFICATION_MESSAGE_TITLE = "오몇점";
    private final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    private final String EXTRA_COUNT = "EXTRA_COUNT";

    Context context;

    private int mCount;
    private String mMessage;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        PowerManager powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wakeLock.acquire();


        mCount = intent.getExtras().getInt(EXTRA_COUNT);
        mMessage = intent.getExtras().getString(EXTRA_MESSAGE);

        Toast toast = Toast.makeText(context, "알람이 울립니다.", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 200);
        toast.show();

        wakeLock.release();

        notification();
    }

    void notification(){
        Intent intent = new Intent();

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notifiBuilder = new NotificationCompat.Builder(context)
                .setContentTitle(NOTIFICATION_MESSAGE_TITLE)
                .setContentText(mMessage)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.notification);

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(mCount, notifiBuilder.build());
    }
}