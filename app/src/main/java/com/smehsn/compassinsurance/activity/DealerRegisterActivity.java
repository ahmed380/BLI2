package com.smehsn.compassinsurance.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.smehsn.compassinsurance.form.DealerInfo;
import com.smehsn.compassinsurance.form.ValidationException;
import com.smehsn.compassinsurance.form.provider.DealerInfoProvider;
import com.smehsn.compassinsurance.fragment.PaginatedFormFragment;

import java.lang.reflect.Field;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DealerRegisterActivity extends AppCompatActivity {

    private static final String FRAGMENT_TAG = "DEALER_INFO_FRAGMENT";
    private PaginatedFormFragment formFragment;
    public static final String DEALER_IDENTIFIED_FLAG = "dealer_identified";


    @BindView(R.id.frame_layout)
    FrameLayout rootView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dealer_info);
        ButterKnife.bind(this);
        initFragment(savedInstanceState);
    }

    private void initFragment(Bundle state){
        if (state != null){
            formFragment = (PaginatedFormFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        }
        else {
            formFragment = PaginatedFormFragment.newInstance(
                    R.layout.form_dealer_info,
                    "Dealer Information",
                    0
            );
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
        try {
            DealerInfo dealer = (DealerInfo) formFragment.getFormModelObject();
            SharedPreferences.Editor editor = getSharedPreferences(DealerInfoProvider.PREF_NAME, Context.MODE_PRIVATE).edit();
            Field[] fields = dealer.getClass().getDeclaredFields();
            for (Field field: fields){
                if (field.getType() != String.class)
                    continue;
                field.setAccessible(true);
                String fieldName = field.getName();
                String value = (String) field.get(dealer);

                editor.putString(fieldName, value);

            }
            editor.putBoolean(DEALER_IDENTIFIED_FLAG, true);
            editor.apply();
            Toast.makeText(DealerRegisterActivity.this, "Dealer information saved", Toast.LENGTH_SHORT).show();


            startActivity(new Intent(this, CompleteFormActivity.class));
            finish();

        } catch (ValidationException e) {
            Snackbar.make(rootView, e.getValidationErrorMessage(), Snackbar.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


}
