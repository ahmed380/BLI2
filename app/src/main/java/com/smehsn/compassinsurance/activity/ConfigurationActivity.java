package com.smehsn.compassinsurance.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;

import com.smehsn.compassinsurance.R;
import com.smehsn.compassinsurance.fragment.DealerRegistrationFragment;
import com.smehsn.compassinsurance.fragment.EmailConfigFragment;
import com.smehsn.compassinsurance.view.LockableViewPager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConfigurationActivity extends AppCompatActivity {

    @BindView(R.id.viewPager)
    LockableViewPager viewPager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        ButterKnife.bind(this);
        initViewPager();
    }

    private void initViewPager(){
        viewPager.setAdapter(new LockableViewPagerAdapter(getSupportFragmentManager()));
    }


    private static final class LockableViewPagerAdapter extends FragmentPagerAdapter{

        public LockableViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return DealerRegistrationFragment.newInstance();
                case 1:
                    return EmailConfigFragment.newInstance();
            }
            return null;
        }


        @Override
        public int getCount() {
            return 2;
        }
    }



}
