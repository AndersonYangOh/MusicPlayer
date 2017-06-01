package com.example.mynetmusicplayer;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.mynetmusicplayer.utils.Song;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class LocalPlay extends AppCompatActivity {
    MediaPlayer mMediaPlayer;
    SimpleAdapter mAdapter;
    ListView mMusicList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_play);
        mMusicList= (ListView) findViewById(R.id.music_list);

        //运行时申请权限
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }else{
            List<Song> songList = getSongList();
            setListAdapter(songList);
        }

    }

    public List<Song> getSongList() {
        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

//        this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
//                Uri.parse("file://"+ Environment.getExternalStorageDirectory())));

        MediaScannerConnection.scanFile(this,
                new String[] { Environment.getExternalStorageDirectory().toString() }
                , null, new MediaScannerConnection.OnScanCompletedListener() {
            /*
             *   (non-Javadoc)
             * @see android.media.MediaScannerConnection.OnScanCompletedListener#onScanCompleted(java.lang.String, android.net.Uri)
             */
            public void onScanCompleted(String path, Uri uri)
            {
                Log.i("ExternalStorage", "Scanned " + path + ":");
                Log.i("ExternalStorage", "-> uri=" + uri);
            }
        });
        Cursor cursor = contentResolver.query(uri, null, null, null, null);

        List<Song> songList = new ArrayList<>();
        for (int i = 0; i < cursor.getCount(); i++) {
            Song song = new Song();
            cursor.moveToFirst();
            long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
            String title = cursor.getString((cursor
                    .getColumnIndex(MediaStore.Audio.Media.TITLE)));//音乐标题
            String artist = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.ARTIST));//艺术家
            long duration = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DURATION));//时长
            long size = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media.SIZE));  //文件大小
            String url = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DATA));   //文件路径

            int isMusic = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));//是否为音乐

            if (true) {
                song.setSongID(id);
                song.setSongTitle(title);
                song.setSongID(id);
                song.setArtist(artist);
                song.setDuration(duration);
                song.setSize(size);
                song.setUrl(url);
                songList.add(song);

            }
        }
        return songList;
    }

    public void setListAdapter(List<Song> mp3Infos) {
        List<HashMap<String, String>> mp3list = new ArrayList<>();
        for (Iterator iterator = mp3Infos.iterator(); iterator.hasNext(); ) {
            Song mp3Info = (Song) iterator.next();
            HashMap<String, String> map = new HashMap<>();
            map.put("title", mp3Info.getSongTitle());
            map.put("artist", mp3Info.getArtist());
            map.put("duration", String.valueOf(mp3Info.getDuration()));
            map.put("size", String.valueOf(mp3Info.getSize()));
            map.put("url", mp3Info.getUrl());
            mp3list.add(map);
        }
        mAdapter = new SimpleAdapter(this, mp3list,
                R.layout.music_list_item, new String[]{"title", "artist", "duration"},
                new int[]{R.id.title, R.id.artist, R.id.duration});
        mMusicList.setAdapter(mAdapter);
    }
}
