package com.lovejoy.baishi.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    private final String[] titles;
    private final List<Fragment> list;

    public MyFragmentPagerAdapter(FragmentManager manager, List list, String[] titles) {
        super(manager);
        this.titles = titles;
        this.list = list;//构造方法，参数是我们的页卡和标题，这样比较方便。
    }

    @Override
    public int getCount() {
        return this.list.size();//返回页卡的数量
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        if (position < this.list.size()) {
            fragment = this.list.get(position);
        } else {
            fragment = this.list.get(0);
        }
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (this.titles != null && this.titles.length > 0) {
            return this.titles[position];
        }
        return null;
    }
}
