package com.smehsn.compassinsurance.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.smehsn.compassinsurance.PhotoAttachmentFragment;
import com.smehsn.compassinsurance.R;
import com.smehsn.compassinsurance.parser.SimpleFormFragment;


public class FormPagerAdapter extends FragmentPagerAdapter {

    public static final String[] PAGE_TITLES = new String[]{
            "Insured information",
            "Vehicle Information",
            "Photo Attachments"
    };
    private static final int ITEMS_COUNT = PAGE_TITLES.length;



    public FormPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return ITEMS_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return PAGE_TITLES[position];
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return SimpleFormFragment.newInstance(R.layout.form_insured_info, PAGE_TITLES[position]);
            case 1:
                return SimpleFormFragment.newInstance(R.layout.form_vehicle_info, PAGE_TITLES[position]);
            case 2:
                return PhotoAttachmentFragment.newInstance(PAGE_TITLES[position], position);
        }
        return null;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }
}