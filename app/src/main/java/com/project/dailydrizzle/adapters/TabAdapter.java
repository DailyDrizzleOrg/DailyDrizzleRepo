package com.project.dailydrizzle.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.project.dailydrizzle.TabFragment;
import com.project.dailydrizzle.models.Category;

import java.util.ArrayList;

/**
 * Created by Neeraj on 27,September,2020
 */
public class TabAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    ArrayList<Category> categoryList;

    public TabAdapter(FragmentManager fm, int NumOfTabs, ArrayList<Category> categoryList) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.categoryList = categoryList;
    }

    @Override
    public Fragment getItem(int position) {
        return TabFragment.newInstance(position, categoryList);
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}