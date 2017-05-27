package com.example.mynetmusicplayer;

import android.annotation.SuppressLint;
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
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import static com.example.mynetmusicplayer.MainActivity.mWebview;
import static com.example.mynetmusicplayer.R.id.webView;

public class PlaceholderFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    MainActivity mainActivity = (MainActivity) getContext();

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

        //webView
        //实例化WebView对象
        MainActivity.mWebview = (WebView) rootView.findViewById(webView);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.MATCH_PARENT);
        //设置WebView属性，能够执行Javascript脚本
        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebview.addJavascriptInterface(new InJavaScriptLocalObj(), "local_obj");
        mWebview.requestFocus();
        mWebview.getSettings().setLoadWithOverviewMode(true);
        mWebview.getSettings().setSupportZoom(true);
        mWebview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebview.getSettings().setBuiltInZoomControls(true);
        mWebview.setWebViewClient(new MyWebViewClient());
        //mWebview.setWebChromeClient(new MyWebChromeClient());

        mWebview.loadUrl("http://h.xiami.com/");
        return rootView;
    }

    static class MyWebViewClient extends WebViewClient {

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        public void onPageStarted(final WebView view, String url, Bitmap favicon) {

        }

        @Override
        public void onPageFinished(final WebView view, String url) {
            //页面加载开始时去掉最上面的logo
            view.post(new Runnable() {
                @SuppressLint("NewApi")
                @Override
                public void run() {
                    view.evaluateJavascript("javascript:document.getElementsByTagName('body')[0].removeChild(document.getElementsByClassName('navbar')[0])"
                            , new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String value) {
                                    System.out.println("webView返回的数据" + value);
                                }
                            });
                }
            });
        }

        @SuppressLint("NewApi")
        @Override
        public WebResourceResponse shouldInterceptRequest(final WebView view,
                                                          final WebResourceRequest request) {
            //如果点击了下载
            if (request != null && request.getUrl() != null
                    && request.getMethod().equalsIgnoreCase("get")
                    && request.getUrl().toString().contains("http://wgo.mmstat.com/xiamiwuxian")) {
                view.post(new Runnable() {
                    @Override
                    public void run() {

                        //
                        view.evaluateJavascript("javascript:document.getElementsByTagName('body')[0].removeChild(document.getElementById('J_dialogTips'))"
                                , new ValueCallback<String>() {
                                    @Override
                                    public void onReceiveValue(String value) {
                                        System.out.println("webView返回的数据" + value);
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
