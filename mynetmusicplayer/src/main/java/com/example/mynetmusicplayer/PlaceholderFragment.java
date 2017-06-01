package com.example.mynetmusicplayer;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.mynetmusicplayer.utils.Song;

import static com.example.mynetmusicplayer.R.id.webView;

public class PlaceholderFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    static Activity mainActivity;
    WebView mWebview;
    public PlaceholderFragment() {
    }

    public PlaceholderFragment newInstance(int sectionNumber) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);

        return fragment;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mainActivity = getActivity();


        //webView
        //实例化WebView对象
        mWebview = (WebView) rootView.findViewById(webView);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.MATCH_PARENT);
        //设置WebView属性，能够执行Javascript脚本
        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebview.requestFocus();
        mWebview.getSettings().setLoadWithOverviewMode(true);
        mWebview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        mWebview.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && mWebview.canGoBack()) {  //表示按返回键
                        mWebview.goBack();   //后退
                        //webview.goForward();//前进
                        return true;    //已处理
                    }
                }
                return false;
            }
        });
        switch (getArguments().getInt(ARG_SECTION_NUMBER,0)){
            case 0:
                mWebview.loadUrl("http://h.xiami.com");
                mWebview.setWebViewClient(new WebViewClientXiami());
                break;
            case 1:
                mWebview.loadUrl("https://m.y.qq.com");
                mWebview.setWebViewClient(new WebViewClientQQ());
                break;
            case 2:
                mWebview.loadUrl("http://music.163.com/m");
                mWebview.setWebViewClient(new WebViewClientNTES());
                break;
        }
        return rootView;
    }

    public void downSong(String songURL, String songName) {
        Song song = new Song();
        song.downSong(mainActivity,songURL,songName);
        Toast.makeText(mainActivity, songName+"正在下载", Toast.LENGTH_SHORT).show();
    }

}

