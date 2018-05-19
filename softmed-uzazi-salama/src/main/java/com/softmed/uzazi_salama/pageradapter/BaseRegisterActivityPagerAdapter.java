package com.softmed.uzazi_salama.pageradapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.softmed.uzazi_salama.Fragments.CHWSmartRegisterFragment;

import org.ei.opensrp.view.fragment.DisplayFormFragment;

/**
 * Created by koros on 11/2/15.
 */
public class BaseRegisterActivityPagerAdapter extends FragmentPagerAdapter {
    private static final String TAG = BaseRegisterActivityPagerAdapter.class.getSimpleName();
    public static final String ARG_PAGE = "page";
    String[] dialogOptions;
    Fragment mBaseFragment;
    Fragment mProfileFragment;
    public int offset = 0;

    public BaseRegisterActivityPagerAdapter(FragmentManager fragmentManager, String[] dialogOptions, Fragment baseFragment) {
        super(fragmentManager);
        this.dialogOptions = dialogOptions;
        this.mBaseFragment = baseFragment;
        offset += 1;
    }
    public BaseRegisterActivityPagerAdapter(FragmentManager fragmentManager, String[] dialogOptions, Fragment baseFragment, Fragment mProfileFragment) {
        super(fragmentManager);
        this.dialogOptions = dialogOptions;
        this.mBaseFragment = baseFragment;
        this.mProfileFragment = mProfileFragment;
        offset += 2;
    }


    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = mBaseFragment;
                break;
            case 1:
                Log.d(TAG,"setting AncRegisterFormFragment");
                fragment = new CHWSmartRegisterFragment();
                break;
            case 2:
                Log.d(TAG,"setting Chw Registration fragment");
                fragment = new CHWSmartRegisterFragment();
                break;
            case 3:
                Log.d(TAG,"setting Chw women list fragment");
                fragment = new CHWSmartRegisterFragment();
                break;


            default:
                String formName = dialogOptions[position - offset]; // account for the base fragment
                Log.d(TAG,"Form name  = "+formName);
                DisplayFormFragment f = new DisplayFormFragment();
                f.setFormName(formName);
                fragment = f;
                break;
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return dialogOptions.length + offset; // index 0 is always occupied by the base fragment
    }

    public int offset() {
        return offset;
    }

}
