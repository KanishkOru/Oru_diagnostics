package com.oruphones.nativediagnostic.home;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/**
 * Created by Pervacio on 31/07/2017.
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }
    public static int currentItem = 0;
    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:

                return new CategoriesFragment();
            /*case 1:

                return new CategoriesFragment();
            case 2:

                return new HistoryFragment();*/
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 1;
    }

}