package com.example.mynetmusicplayer.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;

import java.io.IOException;

public class PlayerService extends Service {
    private static final int PLAY_MSG = 0;
    private static final int PAUSE_MSG = 1;
    private static final int STOP_MSG = 2;
    private MediaPlayer mediaPlayer =  new MediaPlayer();       //媒体播放器对象
    private String path;                        //音乐文件路径
    private boolean isPause;                    //暂停状态
    public PlayerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        path = intent.getStringExtra("url");
        int msg = intent.getIntExtra("MSG", 0);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        if(msg == PLAY_MSG) {
            try {
                mediaPlayer.setDataSource(path);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.start();
        } else if(msg == PAUSE_MSG) {
            mediaPlayer.pause();
        } else if(msg == STOP_MSG) {
            mediaPlayer.stop();
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
