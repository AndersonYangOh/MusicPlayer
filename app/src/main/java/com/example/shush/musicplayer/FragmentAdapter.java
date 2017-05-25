package com.example.shush.musicplayer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shush on 2017/5/21.
 */

public class FragmentAdapter extends FragmentPagerAdapter {
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "虾米音乐";
            case 1:
                return "QQ音乐";
            case 2:
                return "其他音乐";
        }
        return null;
    }

    private List<Fragment> mFragments = new ArrayList<>();

    public FragmentAdapter(FragmentManager fm,List<Fragment> fragments) {
        super(fm);
        mFragments = fragments;
    }

    public FragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }


}
