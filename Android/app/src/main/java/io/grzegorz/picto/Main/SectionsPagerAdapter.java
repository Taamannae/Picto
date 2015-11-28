package io.grzegorz.picto.Main;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import io.grzegorz.picto.Result.ResultFragment;

public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

    private static boolean resultMode = false;
    private int mNumOfTabs;

    public SectionsPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    public static void setResultMode(boolean b) {
        resultMode = b;
    }

    public static boolean getResultMode() {
        return resultMode;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                if (resultMode) {
                    ResultFragment resultTab = new ResultFragment();
                    return resultTab;
                } else {
                    CameraFragment cameraTab = new CameraFragment();
                    return cameraTab;
                }

                //case 1:
                //    FavouritesFragment favouritesTab = new FavouritesFragment();
                //    return favouritesTab;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}