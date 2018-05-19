package com.softmed.uzazi_salama.pageradapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.softmed.uzazi_salama.Fragments.ReferredClientsFragment;
import com.softmed.uzazi_salama.Fragments.FollowupClientsFragment;


/**
 * Created by martha on 8/22/17.
 */

public class CHWPagerAdapter extends FragmentPagerAdapter {

    public CHWPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ReferredClientsFragment() ;

            case 1:
                return new FollowupClientsFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "wakufuatilia";

            case 1:
                return "rufaa ya awali";

            default:
                return String.valueOf(position + 1);
        }
    }
}
