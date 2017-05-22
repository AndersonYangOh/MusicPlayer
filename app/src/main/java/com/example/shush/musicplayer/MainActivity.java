package com.example.shush.musicplayer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViewPager();


    }

    private void setColorToTextView(){
        TextView textView1 = (TextView) findViewById(R.id.localMusic_tv);
        TextView textView2= (TextView) findViewById(R.id.netMusic_tv);
        TextView textView3= (TextView) findViewById(R.id.setting_tv);



    }
    private void initViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.viewPager);

        Fragment localFragment = new LocalMusic();
        Fragment netFragment = new NetMusic();
        Fragment settingFragment = new SettingMusic();

        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(localFragment);
        fragmentList.add(netFragment);
        fragmentList.add(settingFragment);

        FragmentAdapter myFramentAdapter = new FragmentAdapter(
                getSupportFragmentManager(), fragmentList);
        mViewPager.setAdapter(myFramentAdapter);

        mViewPager.addOnPageChangeListener((ViewPager.OnPageChangeListener) MainActivity.this);
    }
}