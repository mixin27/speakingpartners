package com.team29.speakingpartners.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class MyViewPagerAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> mFragmentLists = new ArrayList<>();

    public MyViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        return mFragmentLists.get(i);
    }

    @Override
    public int getCount() {
        return mFragmentLists.size();
    }

    public void addFragment(Fragment fragment) {
        mFragmentLists.add(fragment);
    }
}
