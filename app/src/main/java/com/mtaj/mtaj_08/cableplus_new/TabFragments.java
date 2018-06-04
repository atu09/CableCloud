package com.mtaj.mtaj_08.cableplus_new;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by MTAJ-08 on 7/23/2016.
 */
public class TabFragments extends Fragment {

    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static int int_items = 5;

    final int[] ICONS = new int[]{R.drawable.ic_home_white_24dp, R.drawable.ic_add_alert_white_24dp, R.drawable.ic_menu_camera, R.drawable.ic_menu_gallery, R.drawable.ic_perm_identity_white_24dp};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /**
         *Inflate tab_layout and setup Views.
         */
        View x = inflater.inflate(R.layout.tablayout, null);
        tabLayout = (TabLayout) x.findViewById(R.id.tabs);
        viewPager = (ViewPager) x.findViewById(R.id.viewpager);


        /**
         *Set an Apater for the View Pager
         */
        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));

        /**
         * Now , this is a workaround ,
         * The setupWithViewPager dose't works without the runnable .
         * Maybe a Support Library Bug .
         */

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
                tabLayout.getTabAt(0).setIcon(ICONS[0]);
                tabLayout.getTabAt(1).setIcon(ICONS[1]);
                tabLayout.getTabAt(2).setIcon(ICONS[2]);
                tabLayout.getTabAt(3).setIcon(ICONS[3]);
                tabLayout.getTabAt(4).setIcon(ICONS[4]);
            }
        });

        return x;
    }


    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Return fragment with respect to Position .
         */

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new HomeFragment();
                case 1:
                    return new PaymentFragment();
                case 2:
                    return new CollectionFragment();
                case 3:
                    return new ComplainFragment();
                case 4:
                    return new CustomerFragment();
            }
            return null;
        }

        @Override
        public int getCount() {

            return int_items;

        }

        /**
         * This method returns the title of the tab according to the position.
         */

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0:
                    return "";
                case 1:
                    return "";
                case 2:
                    return "";
                case 3:
                    return "";
                case 4:
                    return "";

            }
            return null;
        }
    }
}


