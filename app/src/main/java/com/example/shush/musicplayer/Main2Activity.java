package com.example.shush.musicplayer;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        WebView mWebview = (WebView) findViewById(R.id.wevView);
        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebview.addJavascriptInterface(new InJavaScriptLocalObj(), "local_obj");
        mWebview.requestFocus();
        mWebview.getSettings().setLoadWithOverviewMode(true);
        mWebview.getSettings().setSupportZoom(true);
        mWebview.getSettings().setBuiltInZoomControls(true);
        mWebview.setWebViewClient(new MyWebViewClient());
            mWebview.loadUrl("http://192.168.15.52:9000/");

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