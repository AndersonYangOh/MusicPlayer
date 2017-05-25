package com.example.mynetmusicplayer;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import static com.example.mynetmusicplayer.R.id.container;
import static com.example.mynetmusicplayer.R.id.webView;

public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    public static class PlaceholderFragment extends Fragment {
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

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            //webView
            //实例化WebView对象
            WebView mWebview = (WebView) rootView.findViewById(webView);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.MATCH_PARENT);
            //设置WebView属性，能够执行Javascript脚本
            mWebview.getSettings().setJavaScriptEnabled(true);
            mWebview.addJavascriptInterface(new InJavaScriptLocalObj(), "local_obj");
            mWebview.requestFocus();
            mWebview.getSettings().setLoadWithOverviewMode(true);
            mWebview.getSettings().setSupportZoom(true);
            mWebview.getSettings().setBuiltInZoomControls(true);
            mWebview.setWebViewClient(new MyWebViewClient());

            //加载需要显示的网页
            mWebview.loadUrl("http://h.xiami.com/");

            return rootView;
        }
    }

    final static class MyWebViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            view.loadUrl("javascript:window.local_obj.showCookie("+"document.cookie);");

            super.onPageStarted(view, url, favicon);
        }

        public void onPageFinished(WebView view, String url) {
            view.loadUrl("javascript:window.local_obj.showSongUrl("
                    + "document.getElementsByTagName('script')[0].getAttribute('src'));");


            super.onPageFinished(view, url);
        }

    }
}

final class InJavaScriptLocalObj {
    @JavascriptInterface
    public void showSongUrl(String html) {
        System.out.println(html);
    }
    @JavascriptInterface
    public void showCookie(String html){
        System.out.println(html);
    }

}

