package com.example.mynetmusicplayer;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.IOException;

import static android.R.attr.id;

public class LocalPlay extends AppCompatActivity {
    MediaPlayer mMediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_play);

        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query(uri,null,null,null,MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

        String url = "";
        if (cursor == null) {
            Log.i("LocalPlay", "null ");
        } else if (!cursor.moveToFirst()) {
            Log.i("LocalPlay", "no media ");
        } else {
            int titleColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
            url = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DATA));
            do {
                long thisId  = cursor.getLong(idColumn);
                String thisTitle = cursor.getString(titleColumn);
                Log.i("LocalPlay","thisId:"+thisId+"thisTitle"+thisTitle);
            } while (cursor.moveToNext());
        }

        Uri contentUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mMediaPlayer.setDataSource(url);
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.start();



    }
}
