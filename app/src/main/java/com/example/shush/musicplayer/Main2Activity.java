package com.example.shush.musicplayer;

import android.app.DownloadManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


    }

    public void down(View view) {
        DownloadManager downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        String apkUrl = "https://om5.alicdn.com/110/3110/2102731162/1795779511_1493694030185.mp3?auth_key=f345dd92cec197cd4b6fb6764b389135-1496458800-0-null";
        DownloadManager.Request request = new
                DownloadManager.Request(Uri.parse(apkUrl));
        request.setTitle("Title歌曲下载中");
        request.allowScanningByMediaScanner();
        long downloadId = downloadManager.enqueue(request);

    }

    final static class MyWebViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.d("WebView", "onPageStarted");
            super.onPageStarted(view, url, favicon);
        }

        public void onPageFinished(WebView view, String url) {
            view.loadUrl("javascript:window.local_obj.showSource("
                    + "document.getElementsByTagName('html')[0].innerHTML);");
//            view.loadUrl("javascript:window.local_obj.showSource("
//                    + "alert(\"提示信息！\");");
            super.onPageFinished(view, url);
        }
    }
}


final class InJavaScriptLocalObj {
    @JavascriptInterface
    public void showSource(String html) {
        System.out.println(html);
    }
}