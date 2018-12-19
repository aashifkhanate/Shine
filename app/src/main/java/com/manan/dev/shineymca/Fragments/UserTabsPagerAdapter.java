package com.manan.dev.shineymca.Fragments;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by neha on 6/16/2018.
 */

public class UserTabsPagerAdapter extends FragmentPagerAdapter {
    private final ArrayList<Fragment> mFragmentList = new ArrayList<>();
    private final ArrayList<String> mFragmentTitleList = new ArrayList<>();


    public UserTabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new CalenderFragment();
            case 2:
                return new NotificationFragment();
            case 3:
                return new ProfileFragment();

        }
        return new AboutFragment();
    }

    public void addFragment(Fragment fragment, String title){
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    public void removeFragment(int index){
        mFragmentList.remove(index);
        mFragmentTitleList.remove(index);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return super.getItemPosition(object);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

}
