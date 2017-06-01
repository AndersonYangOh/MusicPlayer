package com.example.mynetmusicplayer;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by shush on 2017/5/31.
 */

public class WebViewClientQQ extends WebViewClient {
    private static String songURL;
    private static String songName;

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        if (url.contains("androidqqmusic"))
            return false;
        return true;
    }

    @SuppressLint("NewApi")
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

        view.loadUrl(request.getUrl().toString());
        if (request.getUrl().toString().contains("androidqqmusic"))
            return false;

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
                view.evaluateJavascript("javascript:document.getElementsByClassName('wrap')[0].removeChild(document.getElementsByClassName('mod_header')[0])"
                        , null);
                view.evaluateJavascript("javascript:document.getElementsByClassName('current_page loaded')[0].removeChild(document.getElementsByClassName('mod_footer')[0])"
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
                && request.getUrl().toString().contains("dl.stream.qqmusic.qq.com")) {
            songURL = request.getUrl().toString();
            view.post(new Runnable() {
                @Override
                public void run() {
                    view.evaluateJavascript("javascript:document.getElementsByClassName('song_info__flex')[0].innerText"
                            , new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String value) {
                                    if (value != null && !value.equals("null")) {
                                        songName = value.replace("\\n", "-").replace("\"", "");
                                        songName = songName.substring(0, songName.length() - 1);
                                    }

                                }
                            });
                }
            });
        }

        //如果点击了下载
        if (request != null && request.getUrl() != null
                && request.getMethod().equalsIgnoreCase("get")
                && request.getUrl().toString().contains("androidqqmusic://form=webpage")) {

            view.post(new Runnable() {
                @Override
                public void run() {
                    PlaceholderFragment placeholderFragment = new PlaceholderFragment();
                    placeholderFragment.downSong(songURL, songName);
                }
            });


        }


        return super.shouldInterceptRequest(view, request);
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(final WebView view, String url) {
        if (!TextUtils.isEmpty(url) && Uri.parse(url).getScheme() != null
                && url.contains("dl.stream.qqmusic.qq.com")) {
            songURL = url;
            view.post(new Runnable() {
                @Override
                public void run() {
                    view.evaluateJavascript("javascript:document.getElementsByClassName('song_info__flex')[0].innerText"
                            , new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String value) {
                                    if (value != null && !value.equals("null")) {
                                        songName = value.replace("\\n", "-").replace("\"", "");
                                        songName = decodeUnicode(songName);
                                        songName = songName.substring(0, songName.length() - 1);
                                    }

                                }
                            });
                }
            });
        }

        //如果点击了下载
        if (url != null && url.contains("androidqqmusic://form=webpage")) {
            url = null;
            view.post(new Runnable() {
                @Override
                public void run() {
                    PlaceholderFragment placeholderFragment = new PlaceholderFragment();
                    placeholderFragment.downSong(songURL, songName);
                }
            });

        }
        return null;
    }

    public static String decodeUnicode(String theString) {

        char aChar;

        int len = theString.length();

        StringBuffer outBuffer = new StringBuffer(len);

        for (int x = 0; x < len; ) {

            aChar = theString.charAt(x++);

            if (aChar == '\\') {

                aChar = theString.charAt(x++);

                if (aChar == 'u') {

                    // Read the xxxx

                    int value = 0;

                    for (int i = 0; i < 4; i++) {

                        aChar = theString.charAt(x++);

                        switch (aChar) {

                            case '0':

                            case '1':

                            case '2':

                            case '3':

                            case '4':

                            case '5':

                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed   \\uxxxx   encoding.");
                        }

                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't')
                        aChar = '\t';
                    else if (aChar == 'r')
                        aChar = '\r';

                    else if (aChar == 'n')

                        aChar = '\n';

                    else if (aChar == 'f')

                        aChar = '\f';

                    outBuffer.append(aChar);

                }

            } else

                outBuffer.append(aChar);

        }

        return outBuffer.toString();

    }
}
