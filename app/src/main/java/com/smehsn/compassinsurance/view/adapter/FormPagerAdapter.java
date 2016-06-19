package com.smehsn.compassinsurance.view.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.smehsn.compassinsurance.R;
import com.smehsn.compassinsurance.parser.fragment.SimpleFormFragment;
import com.smehsn.compassinsurance.view.fragment.DriverInfoFormFragment;
import com.smehsn.compassinsurance.view.fragment.PhotoAttachmentFragment;

import java.util.ArrayList;
import java.util.List;


public class FormPagerAdapter extends FragmentPagerAdapter {

    public static final String[] PAGE_TITLES = new String[]{
            "Photo Attachments",
            "Insured information",
            "Vehicle Information",
            "Driver Information",
            "Vehicle Coverages",
    };
    private static final int ITEMS_COUNT = PAGE_TITLES.length;

    private List<Fragment> fragmentList;


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

    private void initFragmentList(){
        fragmentList = new ArrayList<>();
        fragmentList.add(PhotoAttachmentFragment.newInstance(PAGE_TITLES[0]));
        fragmentList.add(SimpleFormFragment.newInstance(R.layout.form_insured_info, PAGE_TITLES[1]));
        fragmentList.add(SimpleFormFragment.newInstance(R.layout.form_vehicle_info, PAGE_TITLES[2]));
        fragmentList.add(DriverInfoFormFragment.newInstance( PAGE_TITLES[3]));
        fragmentList.add(SimpleFormFragment.newInstance(R.layout.form_vehicle_coverages, PAGE_TITLES[4]));
    }

    @Override
    public Fragment getItem(int position) {
        if (fragmentList == null){
            initFragmentList();
        }
        return fragmentList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }
}