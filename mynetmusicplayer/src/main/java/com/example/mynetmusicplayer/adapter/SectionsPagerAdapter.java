package com.example.mynetmusicplayer.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.mynetmusicplayer.PlaceholderFragment;

/**
 * Created by shush on 2017/5/25.
 */

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).

        PlaceholderFragment placeholderFragment = new PlaceholderFragment();
        return placeholderFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "虾米音乐";
            case 1:
                return "QQ音乐";
            case 2:
                return "网易云音乐";
        }
        return null;
    }
}