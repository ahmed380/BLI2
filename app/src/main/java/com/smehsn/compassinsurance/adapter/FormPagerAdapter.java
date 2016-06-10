package com.smehsn.compassinsurance.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.smehsn.compassinsurance.R;
import com.smehsn.compassinsurance.fragment.PaginatedFormFragment;


public class FormPagerAdapter extends FragmentPagerAdapter {
    public static final String[] PAGE_TITLES = new String[]{
            "Insured Information",
            "General Information",
            "Coverages",
            "Vehicle Information",
            "Vehicle Coverages"
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
        return PaginatedFormFragment.newInstance(LAYOUT_RES_IDS[position], PAGE_TITLES[position], position);
    }
}