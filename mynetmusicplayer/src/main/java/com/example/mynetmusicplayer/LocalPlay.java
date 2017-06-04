package com.example.mynetmusicplayer;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.mynetmusicplayer.receiver.NotificationBroadcastReceiver;
import com.example.mynetmusicplayer.service.PlayerService;
import com.example.mynetmusicplayer.utils.Song;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.mynetmusicplayer.service.PlayerService.mediaPlayer;

public class LocalPlay extends AppCompatActivity {

    private static int ISPLAYING = 1; // 0播放，1暂停
    private static int currentPosition = -1;  //当前播放的歌曲ID
    private static String currentSongName = "";
    SimpleAdapter mAdapter;
    ListView mMusicList;
    public static List<Song> songList;
    Song song;
    Intent intent;
    ImageButton ibPlay;
    SeekBar mSeekBar;

    private RemoteViews contentView;
    NotificationManager manager;
    private Notification notification;


    @Override
    protected void onResume() {
        setCurrentSong();
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_play);
        mMusicList = (ListView) findViewById(R.id.music_list);
        ibPlay = (ImageButton) findViewById(R.id.play_music);
        mMusicList.setOnItemClickListener(new MusicListItemClickListener());

        mSeekBar = (SeekBar) this.findViewById(R.id.seekBar);
        //实时设置seekBar进度
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    mediaPlayer.seekTo(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (currentPosition < songList.size() - 1) {
                    try {
                        ISPLAYING = 0;
                        PlayerService.playOrPause();
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(
                                songList.get(currentPosition + 1).getUrl());
                        currentPosition += 1;
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    mediaPlayer.seekTo(0);
                    mediaPlayer.start();
                } else {
                    try {
                        ISPLAYING = 0;
                        PlayerService.playOrPause();
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(
                                songList.get(0).getUrl());
                        currentPosition = 0;
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    mediaPlayer.seekTo(0);
                    mediaPlayer.start();
                }
                setCurrentSong();
                setSeekBar();
            }
        });

        //运行时申请权限
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            List<Song> songList = getSongList();
            setListAdapter(songList);
        }

    }

    private void setSeekBar() {
        mSeekBar.setMax(mediaPlayer.getDuration());
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                mSeekBar.setProgress(mediaPlayer.getCurrentPosition());
            }
        };
        timer.schedule(timerTask, 0, 500);
    }

    //获取歌曲
    public List<Song> getSongList() {
        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        songList = new ArrayList<>();
        for (int i = 0; i < cursor.getCount(); i++) {
            Song song = new Song();
            cursor.moveToNext();
            long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
            String title = cursor.getString((cursor
                    .getColumnIndex(MediaStore.Audio.Media.TITLE)));//音乐标题
            long duration = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DURATION));//时长
            String url = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DATA));   //文件路径
            int isMusic = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));//是否为音乐

            if (isMusic != 0) {
                song.setSongID(id);
                song.setSongTitle(title);
                song.setSongID(id);
                song.setDuration(duration);
                song.setUrl(url);
                songList.add(song);

            }
        }
        return songList;
    }

    //设置歌曲到listview
    public void setListAdapter(List<Song> mp3Infos) {
        List<HashMap<String, String>> mp3list = new ArrayList<>();
        for (Iterator iterator = mp3Infos.iterator(); iterator.hasNext(); ) {
            Song mp3Info = (Song) iterator.next();
            HashMap<String, String> map = new HashMap<>();
            map.put("title", mp3Info.getSongTitle());
            map.put("artist", mp3Info.getArtist());
            map.put("duration", String.valueOf(formatTime(mp3Info.getDuration())));
            map.put("size", String.valueOf(mp3Info.getSize()));
            map.put("url", mp3Info.getUrl());
            mp3list.add(map);
        }
        mAdapter = new SimpleAdapter(this, mp3list,
                R.layout.music_list_item, new String[]{"title", "duration"},
                new int[]{R.id.title, R.id.duration});
        mMusicList.setAdapter(mAdapter);
    }

    //上一首、播放、下一首
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.play_music:
                if (currentPosition == -1) {
                    try {
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(
                                songList.get(0).getUrl());
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    currentPosition = 0;
                }

                PlayerService.playOrPause();
                //更改图片
                if (ISPLAYING == 1) {
                    ibPlay.setBackgroundResource(R.drawable.pause_dark);
                    ISPLAYING = 0;
                } else {
                    ibPlay.setBackgroundResource(R.drawable.play_dark);
                    ISPLAYING = 1;
                }
                break;
            case R.id.previous_music:
                if (currentPosition > 0) {
                    try {
                        ISPLAYING = 0;
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(
                                songList.get(currentPosition - 1).getUrl());
                        currentPosition -= 1;
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mediaPlayer.seekTo(0);
                    mediaPlayer.start();
                }
                break;
            case R.id.next_music:
                if (currentPosition < songList.size() - 1) {
                    try {
                        ISPLAYING = 0;
                        PlayerService.playOrPause();
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(
                                songList.get(currentPosition + 1).getUrl());
                        currentPosition += 1;
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    mediaPlayer.seekTo(0);
                    mediaPlayer.start();
                }

                break;
            default:
                break;
        }
        setCurrentSong();
        setSeekBar();
    }

    //设置当前播放的音乐的文本颜色
    private void setCurrentSong() {
        if (currentPosition != -1) {
            for (int i = 0; i < mMusicList.getChildCount(); i++) {
                if (i == currentPosition) {
                    LinearLayout layout = (LinearLayout) mMusicList.getChildAt(currentPosition);
                    TextView text = (TextView) layout.findViewById(R.id.title);
                    text.setTextColor(Color.parseColor("#FF4081"));
                } else {
                    LinearLayout layout = (LinearLayout) mMusicList.getChildAt(i);
                    TextView text = (TextView) layout.findViewById(R.id.title);
                    text.setTextColor(Color.parseColor("#66000000"));
                }

            }
            if (ISPLAYING == 0) {
                ibPlay.setBackgroundResource(R.drawable.pause_dark);
            } else {
                ibPlay.setBackgroundResource(R.drawable.play_dark);
            }
            initNotificationBar();
            setSeekBar();
        }


    }

    //listview点击事件
    private class MusicListItemClickListener
            implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if (mMusicList != null) {
                if (currentPosition == -1) {                 //第一次点击列表
                    song = songList.get(position);
                    intent = new Intent(LocalPlay.this, PlayerService.class);
                    intent.putExtra("url", song.getUrl());
                    startService(intent);
                    currentPosition = position;
                    ISPLAYING = 0;
                    ibPlay.setBackgroundResource(R.drawable.pause_dark);
                } else if (currentPosition == position) {  //点击了当前正在播放的歌曲
                    //更改图片
                    if (ISPLAYING == 1) {
                        ISPLAYING = 0;
                        PlayerService.playOrPause();
                    } else {
                        ISPLAYING = 1;
                        PlayerService.playOrPause();
                    }
                } else {                             //点击了非当前播放的歌曲
                    song = songList.get(position);
                    intent = new Intent(LocalPlay.this, PlayerService.class);
                    intent.putExtra("url", song.getUrl());
                    startService(intent);
                    currentPosition = position;
                    ISPLAYING = 0;
                }
                setCurrentSong();
                setSeekBar();
            }
        }
    }

    public void initNotificationBar() {
        currentSongName = songList.get(currentPosition).getSongTitle();
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //点击通知，打开播放页面
        Intent playIntent = new Intent(this, LocalPlay.class);
        PendingIntent playPendingIntent = PendingIntent.getActivity(this, 0,
                playIntent, PendingIntent.FLAG_ONE_SHOT);
        //滑动清除通知
        Intent intentCancel = new Intent(this, NotificationBroadcastReceiver.class);
        intentCancel.setAction("notification_cancelled");
        PendingIntent pendingIntentCancel = PendingIntent.getBroadcast(this, 0,
                intentCancel, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.my)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_round))
                        .setContentTitle("正在播放歌曲")
                        .setContentText(currentSongName)
                        .setContentIntent(playPendingIntent)
                        .setDeleteIntent(pendingIntentCancel);
        Notification notification = mBuilder.build();
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(1, mBuilder.build());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 格式化时间，将毫秒转换为分:秒格式
     *
     * @param time
     * @return
     */
    public static String formatTime(long time) {
        String min = time / (1000 * 60) + "";
        String sec = time % (1000 * 60) + "";
        if (min.length() < 2) {
            min = "0" + time / (1000 * 60) + "";
        } else {
            min = time / (1000 * 60) + "";
        }
        if (sec.length() == 4) {
            sec = "0" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 3) {
            sec = "00" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 2) {
            sec = "000" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 1) {
            sec = "0000" + (time % (1000 * 60)) + "";
        }
        return min + ":" + sec.trim().substring(0, 2);
    }

    private void clearNotify() {
        manager.cancelAll();
    }

    public static void stopMusic() {
        PlayerService.stop();
        currentPosition = -1;  //当前播放的歌曲ID
        currentSongName = "";
    }
}
