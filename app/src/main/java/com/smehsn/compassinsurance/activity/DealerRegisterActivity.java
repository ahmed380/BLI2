package com.smehsn.compassinsurance.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.smehsn.compassinsurance.R;
import com.smehsn.compassinsurance.beans.DealerInfoProvider;
import com.smehsn.compassinsurance.context.AppContext;
import com.smehsn.compassinsurance.parser.FormValidationException;
import com.smehsn.compassinsurance.parser.SimpleFormFragment;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DealerRegisterActivity extends AppCompatActivity {

    private static final String FRAGMENT_TAG = "DEALER_INFO_FRAGMENT";
    private SimpleFormFragment formFragment;
    public static final String DEALER_IDENTIFIED_FLAG = "dealer_identified";
    private DealerInfoProvider dealerInfoProvider;

    @BindView(R.id.frame_layout)
    FrameLayout rootView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dealer_info);
        ButterKnife.bind(this);
        initFragment(savedInstanceState);
        initBeans();
    }

    private void initBeans() {
        dealerInfoProvider = (DealerInfoProvider) ((AppContext) getApplication()).get(DealerInfoProvider.BEAN_KEY);
    }

    private void initFragment(Bundle state){
        if (state != null){
            formFragment = (SimpleFormFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        }
        else {
            formFragment = SimpleFormFragment.newInstance(R.layout.form_dealer_info, "Dealer Information");
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout,formFragment, FRAGMENT_TAG);
            fragmentTransaction.addToBackStack(FRAGMENT_TAG);
            fragmentTransaction.commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.dealer_register_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.save:
                onTryToSaveDealer();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onTryToSaveDealer() {
        Map<String, String> form ;
        try {
            form = formFragment.parseForm();
            dealerInfoProvider.saveDealerInfo(form);
            Toast.makeText(DealerRegisterActivity.this, "Dealer Information saved", Toast.LENGTH_SHORT).show();
            finish();
        } catch (FormValidationException e) {
            String message = e.getErrorMessage();
            Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show();
        }
    }




}
