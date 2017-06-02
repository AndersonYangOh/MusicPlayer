package com.example.mynetmusicplayer.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.example.mynetmusicplayer.MainActivity;
import com.example.mynetmusicplayer.R;

import java.io.IOException;

public class PlayerService extends Service {
    private static final int PLAY_MSG = 0;    //播放
    private static final int PAUSE_MSG = 1;   //暂停
    private static final int STOP_MSG = 2;    //停止
    public static MediaPlayer mediaPlayer = new MediaPlayer();//媒体播放器对象
    private String path;                        //音乐文件路径

    public PlayerService() {
    }


    public final IBinder binder = new MyBinder();

    public class MyBinder extends Binder {
        PlayerService getService() {
            return PlayerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {

        super.onCreate();

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("This is content title")
                .setContentText("This is content text")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentIntent(pi)
                .build();
        startForeground(1, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        path = intent.getStringExtra("url");
        int msg = intent.getIntExtra("MSG", 0);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        if (msg == PLAY_MSG) {
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(path);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.start();
        } else if (msg == PAUSE_MSG) {
            mediaPlayer.pause();
        } else if (msg == STOP_MSG) {
            mediaPlayer.stop();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void playOrPause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.start();
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            try {
                mediaPlayer.prepare();
                mediaPlayer.seekTo(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
