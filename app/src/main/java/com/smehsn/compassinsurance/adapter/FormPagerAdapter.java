package com.smehsn.compassinsurance.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.smehsn.compassinsurance.R;
import com.smehsn.compassinsurance.fragment.PaginatedFormFragment;
import com.smehsn.compassinsurance.fragment.PhotoAttachmentFragment;

import java.util.HashMap;
import java.util.Map;


public class FormPagerAdapter extends FragmentPagerAdapter {
    public static final String[] PAGE_TITLES = new String[]{
            "Insured Information",
            "General Information",
            "Coverages",
            "Vehicle Information",
            "Vehicle Coverages",
            "Photo Attachments"
    };

    public static final int[] LAYOUT_RES_IDS = new int[]{
            R.layout.form_insured_info,
            R.layout.form_general_info,
            R.layout.form_coverages,
            R.layout.form_vehicle_info,
            R.layout.form_vehicle_coverages
    };

    public static final int ITEMS_COUNT = PAGE_TITLES.length;



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
        if (position < LAYOUT_RES_IDS.length){
            return PaginatedFormFragment.newInstance(LAYOUT_RES_IDS[position], PAGE_TITLES[position], position);
        }else if(position == ITEMS_COUNT - 1){
            return PhotoAttachmentFragment.newInstance(PAGE_TITLES[position], position);
        }
        return null;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }
}