package com.smehsn.compassinsurance.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.smehsn.compassinsurance.R;
import com.smehsn.compassinsurance.dao.Dealer;
import com.smehsn.compassinsurance.dao.EmailConfig;
import com.smehsn.compassinsurance.fragment.DealerRegistrationFragment;
import com.smehsn.compassinsurance.fragment.EmailConfigFragment;
import com.smehsn.compassinsurance.view.LockableViewPager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity implements
        DealerRegistrationFragment.DealerRegistrationListener, EmailConfigFragment.EmailConfigListener{

    @BindView(R.id.viewPager)
    LockableViewPager viewPager;


    private Dealer dealer;
    private EmailConfig emailConfig;
    private LockableViewPagerAdapter viewPagerAdapter;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        ButterKnife.bind(this);
        initBeans();
        initViewPager();
    }

    private void initBeans(){
        dealer = Dealer.getInstance(this);
        emailConfig = EmailConfig.getInstance(this);
    }

    private void initViewPager(){
        viewPagerAdapter = new LockableViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOffscreenPageLimit(viewPagerAdapter.getCount());

        if (dealer.isIdentified()) {
            viewPager.setCurrentItem(1);
        }
        else if (emailConfig.isVerified()){
            finish();
            Toast.makeText(SettingsActivity.this, "App settings are already set", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void dealerRegistered() {
        viewPager.setCurrentItem(1);
    }

    @Override
    public void emailConfigurationFinished() {
        finish();
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
