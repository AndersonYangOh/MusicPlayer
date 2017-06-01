package com.example.mynetmusicplayer.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;

/**
 * Created by shush on 2017/5/31.
 */

public class Song {

    long songID;      //歌曲ID
    String songTitle;   //歌曲名
    String artist;      //艺术家
    long duration;    //时长
    long size;       //文件大小
    String url ;    //文件路径
    public long getSongID() {
        return songID;
    }

    public void setSongID(long songID) {
        this.songID = songID;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public void downSong(Context context ,String songURL, String songName) {

        DownloadManager downloadManager  =  (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        String apkUrl = songURL;
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkUrl));

        request.setTitle(songName + ".mp3");
        request.setMimeType("audio/mpeg");
        request.allowScanningByMediaScanner();
        long downloadId = downloadManager.enqueue(request);
    }


}
