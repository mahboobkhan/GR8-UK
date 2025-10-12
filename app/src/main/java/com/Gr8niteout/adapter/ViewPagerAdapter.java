package com.Gr8niteout.adapter;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.Gr8niteout.ActivityFragment.Birthdays;
import com.Gr8niteout.ActivityFragment.Drinks;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    public String Titles[] = {"Drinks","Birthdays"};

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        if(position == 0)
        {
            return new Drinks();
        }
        else
        {
            return new Birthdays();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    @Override
    public int getCount() {
        return 2;
    }
}