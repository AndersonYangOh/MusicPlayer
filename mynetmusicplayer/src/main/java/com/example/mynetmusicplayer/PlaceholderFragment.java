package com.example.mynetmusicplayer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import static android.content.Context.DOWNLOAD_SERVICE;
import static com.example.mynetmusicplayer.R.id.webView;

public class PlaceholderFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";


    public PlaceholderFragment() {
    }

    public PlaceholderFragment newInstance(int sectionNumber) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);

        return fragment;
    }

    static Activity MyActivity;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        MyActivity = getActivity();
        //webView
        //实例化WebView对象
        WebView mWebview = (WebView) rootView.findViewById(webView);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.MATCH_PARENT);
        //设置WebView属性，能够执行Javascript脚本
        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebview.requestFocus();
        mWebview.getSettings().setLoadWithOverviewMode(true);
        mWebview.getSettings().setSupportZoom(true);
        mWebview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebview.getSettings().setBuiltInZoomControls(true);
        mWebview.setWebViewClient(new MyWebViewClient());
        mWebview.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);

            }
        });
        mWebview.loadUrl("http://h.xiami.com/");
        return rootView;
    }

    public void downSong(String songURL, String songName) {
        DownloadManager downloadManager = (DownloadManager) MyActivity.getSystemService(DOWNLOAD_SERVICE);
        String apkUrl = songURL;
        DownloadManager.Request request = new
                DownloadManager.Request(Uri.parse(apkUrl));
        request.setTitle(songName+".mp3");
        request.allowScanningByMediaScanner();
        long downloadId = downloadManager.enqueue(request);
    }

    static class MyWebViewClient extends WebViewClient {
        private static String songURL;
        private static String songName;
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        public void onPageStarted(final WebView view, String url, Bitmap favicon) {

        }

        @Override
        public void onPageFinished(final WebView view, String url) {
            view.post(new Runnable() {
                @SuppressLint("NewApi")
                @Override
                public void run() {
                    //页面加载结束后 去掉一些div
                    view.evaluateJavascript("javascript:document.getElementsByTagName('body')[0].removeChild(document.getElementsByClassName('navbar')[0])"
                            , null);
                    view.evaluateJavascript("javascript:document.getElementsByTagName('section')[0].removeChild(document.getElementById('J_Slide'))"
                            , null);
                }
            });
            super.onPageFinished(view, url);
        }

        @SuppressLint("NewApi")
        @Override
        public WebResourceResponse shouldInterceptRequest(final WebView view,
                                                          final WebResourceRequest request) {
            //获取歌曲url
            if (request != null && request.getUrl() != null
                    && request.getMethod().equalsIgnoreCase("get")
                    && request.getUrl().toString().contains("om5.alicdn.com")) {
                songURL = request.getUrl().toString();
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        view.evaluateJavascript("javascript:document.getElementsByClassName('line current')[0].innerText"
                                , new ValueCallback<String>() {
                                    @Override
                                    public void onReceiveValue(String value) {
                                        if (value != null && !value.equals("null")){
                                            songName = value.replace("\\n","-").replace("\"","");
                                            songName = songName.substring(0,songName.length()-1);

                                        }

                                    }
                                });
                    }
                });
            }

            //如果点击了下载
            if (request != null && request.getUrl() != null
                    && request.getMethod().equalsIgnoreCase("get")
                    && request.getUrl().toString().contains("wgo.mmstat.com/xiamiwuxian")) {
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        view.evaluateJavascript("javascript:document.getElementsByTagName('body')[0].removeChild(document.getElementById('J_dialogTips'))"
                                , new ValueCallback<String>() {
                                    @Override
                                    public void onReceiveValue(String value) {
                                        //System.out.println("webView返回的数据" + value);
                                        //如果value不等于空，则说明出现了J_dialogTips，即点击了下载
                                        //调用下载方法
                                        if (value != null && songURL != null && songName !=null) {
                                            System.out.println(songURL);
                                            PlaceholderFragment placeholderFragment = new PlaceholderFragment();
                                            placeholderFragment.downSong(songURL, songName);

                                        }
                                    }
                                });
                    }
                });

            }


            return null;
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(final WebView view, String url) {
            if (!TextUtils.isEmpty(url) && Uri.parse(url).getScheme() != null
                    && url.contains("http://om5.alicdn.com")) {
                System.out.println(url);
            }

            //如果点击了下载
            if (!TextUtils.isEmpty(url) && url.contains("wgo.mmstat.com/xiamiwuxian.1.134")) {
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("点击了下载old");
                        view.loadUrl("javascript:document.getElementsByTagName('body')[0].removeChild(document.getElementById('J_dialogTips'));");
                        //view.loadUrl("javascript:document.cookie");
                    }
                });
            }
            return null;
        }

    }

}

final class InJavaScriptLocalObj {
    @JavascriptInterface
    public void execJS(String html) {
        System.out.println(html);
    }

}
