package com.example.mynetmusicplayer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.mynetmusicplayer.LocalPlay;

public class NotificationBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        int type = intent.getIntExtra("type", -1);
//
//        if (type != -1) {
//            NotificationManager notificationManager =
//                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//            notificationManager.cancel(type);
//        }

        if (action.equals("notification_cancelled")) {
            //处理滑动清除和点击删除事件
            LocalPlay.stopMusic();
        }
    }
}
