package com.example.mynetmusicplayer.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;

/**
 * Created by shush on 2017/5/31.
 */

public class Song {

    public void downSong(Context context ,String songURL, String songName) {

        DownloadManager downloadManager  =  (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        String apkUrl = songURL;
        DownloadManager.Request request = new
                DownloadManager.Request(Uri.parse(apkUrl));

        request.setTitle(songName + ".mp3");
        request.setMimeType("audio/mpeg");
        request.allowScanningByMediaScanner();
        long downloadId = downloadManager.enqueue(request);
    }
}
