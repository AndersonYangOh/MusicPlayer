package com.example.mynetmusicplayer;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.mynetmusicplayer.service.PlayerService;
import com.example.mynetmusicplayer.utils.Song;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class LocalPlay extends AppCompatActivity {

    private static final int PLAY_MSG = 0;
    private int ISPLAYING = 0; // 0播放，1暂停
    private int currentPosition = -1 ;  //当前播放的歌曲ID

    PlayerService musicService;
    SimpleAdapter mAdapter;
    ListView mMusicList;
    public static List<Song> songList;
    Song song;
    Intent intent;
    ImageButton ibPlay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_play);
        mMusicList = (ListView) findViewById(R.id.music_list);
        ibPlay = (ImageButton) findViewById(R.id.play_music);
        ImageButton ibPre = (ImageButton) findViewById(R.id.previous_music);
        ImageButton ibNext = (ImageButton) findViewById(R.id.next_music);
        mMusicList.setOnItemClickListener(new MusicListItemClickListener());
        musicService = new PlayerService();

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
                musicService.playOrPause();
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
                if (currentPosition-1 <= 0) {
                    try {
                        musicService.mediaPlayer.setDataSource(
                                songList.get(currentPosition - 1).getUrl());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    musicService.mediaPlayer.reset();
                    musicService.playOrPause();
                }
                break;
            case R.id.next_music:
                if (currentPosition+1 >= songList.size()){
                    try {
                        musicService.mediaPlayer.setDataSource(
                                songList.get(currentPosition+1).getUrl());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    musicService.mediaPlayer.reset();
                    musicService.playOrPause();
                }

                break;
            default:
                break;
        }
    }

    //listview点击事件
    private class MusicListItemClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mMusicList != null) {
                if (currentPosition == -1){                 //第一次点击列表
                    song = songList.get(position);
                    intent = new Intent(LocalPlay.this, PlayerService.class);
                    intent.putExtra("url", song.getUrl());
                    startService(intent);
                    currentPosition = position;
                    ISPLAYING = 0;
                    ibPlay.setBackgroundResource(R.drawable.pause_dark);
                }else if (currentPosition == position){  //点击了当前正在播放的歌曲
                    //更改图片
                    if (ISPLAYING == 1) {
                        ibPlay.setBackgroundResource(R.drawable.pause_dark);
                        ISPLAYING = 0;
                        musicService.playOrPause();
                    } else {
                        ibPlay.setBackgroundResource(R.drawable.play_dark);
                        ISPLAYING = 1;
                        musicService.playOrPause();
                    }
                }else {                             //点击了非当前播放的歌曲
                    song = songList.get(position);
                    intent = new Intent(LocalPlay.this, PlayerService.class);
                    intent.putExtra("url", song.getUrl());
                    startService(intent);
                    currentPosition = position;
                    ISPLAYING = 0;
                    ibPlay.setBackgroundResource(R.drawable.pause_dark);
                }

            }
        }
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
}
