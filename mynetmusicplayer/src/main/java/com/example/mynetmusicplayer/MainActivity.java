package com.example.mynetmusicplayer;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.mynetmusicplayer.adapter.SectionsPagerAdapter;
import com.example.mynetmusicplayer.service.PlayerService;
import com.example.mynetmusicplayer.utils.GetWeather;
import com.example.mynetmusicplayer.utils.LocationUtils;

import static com.example.mynetmusicplayer.R.id.container;

public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    Toolbar toolbar;
    public static Location location;
    String sWeather;
    private static final int OK = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        //获取天气信息
        GetWeather();
    }

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case OK:
                    TextView tvWeather = (TextView) findViewById(R.id.weather);
                    tvWeather.setText((String)msg.obj);
                    break;
            }
        }

    };
    public void GetWeather(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                location = LocationUtils.getInstance(MainActivity.this).showLocation();
                if (location != null) {
                    sWeather = GetWeather.getWeather(String.valueOf(location.getLatitude())
                            , String.valueOf(location.getLongitude()));
                    Message msg = new Message();
                    msg.what = OK;
                    msg.obj = sWeather;

                    mHandler.sendMessage(msg);

                }
                Looper.loop();
            }
        }).start();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent = new Intent(this, LocalPlay.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.slide_out_right);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(this, PlayerService.class);
        stopService(intent);
    }
}



