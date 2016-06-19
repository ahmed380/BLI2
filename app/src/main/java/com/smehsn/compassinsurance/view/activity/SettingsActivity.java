package com.smehsn.compassinsurance.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.smehsn.compassinsurance.R;
import com.smehsn.compassinsurance.data.pref.AdminConfig;
import com.smehsn.compassinsurance.data.pref.Dealer;
import com.smehsn.compassinsurance.data.pref.EmailConfig;
import com.smehsn.compassinsurance.view.fragment.DealerRegistrationFragment;
import com.smehsn.compassinsurance.view.fragment.EmailConfigFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import icepick.Icepick;
import icepick.State;

public class SettingsActivity extends AppCompatActivity implements
        DealerRegistrationFragment.DealerRegistrationListener, EmailConfigFragment.EmailConfigListener{

    @BindView(R.id.viewPager)
    ViewPager viewPager;


    @State String username;
    @State String password;

    private Dealer        dealer;
    private EmailConfig   emailConfig;
    private MPagerAdapter viewPagerAdapter;

    public static void launch(Context ctx, String username, String password){
        Intent intent = new Intent(ctx, SettingsActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("password", password);
        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        Icepick.restoreInstanceState(this, savedInstanceState);
        if (checkCredentials(getIntent())) {
            ButterKnife.bind(this);
            initBeans();
            initViewPager();
        }else{
            Toast.makeText(SettingsActivity.this, "Wrong admin credentials", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    private boolean checkCredentials(Intent intent){
        if (username == null || password == null){
            username = intent.getStringExtra("username");
            password = intent.getStringExtra("password");
        }

        return AdminConfig.getInstance(this).isAuthenticated(username, password);
    }

    private void initBeans(){
        dealer = Dealer.getInstance(this);
        emailConfig = EmailConfig.getInstance(this);
    }

    private void initViewPager(){
        viewPagerAdapter = new MPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOffscreenPageLimit(viewPagerAdapter.getCount());
        viewPager.setCurrentItem(0);
        if (dealer.isIdentified()) {
            viewPager.setCurrentItem(1);
        }
        if (emailConfig.isVerified()){
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


    private static final class MPagerAdapter extends FragmentPagerAdapter{

        public MPagerAdapter(FragmentManager fm) {
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
