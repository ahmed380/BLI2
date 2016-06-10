package com.smehsn.compassinsurance.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.smehsn.compassinsurance.R;
import com.smehsn.compassinsurance.adapter.FormPagerAdapter;
import com.smehsn.compassinsurance.fragment.FormObjectProvider;
import com.smehsn.compassinsurance.fragment.OnValidationResultListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnPageChange;



public class CompleteFormActivity extends AppCompatActivity {
    private static final String TAG = CompleteFormActivity.class.getSimpleName();

    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;

    @BindView(R.id.drawerListView)
    ListView drawerListView;

    private boolean formContainsValidationErrors = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_form);
        ButterKnife.bind(this);
        initViewPager();
        initDrawerLayout();
        initDrawerListView();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        formContainsValidationErrors = savedInstanceState.getBoolean("formContainsValidationErrors");

        //Request form validation to highlight errors
        if (formContainsValidationErrors)
            //Validate without showing error message (we have shown it before)
            onTryToSubmitForm(false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //save previous validation check result to highlight errors after state restore
        outState.putBoolean("formContainsValidationErrors", formContainsValidationErrors);
    }

    private void initViewPager(){
        FormPagerAdapter adapter = new FormPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(FormPagerAdapter.ITEMS_COUNT);
        changeActivityTitle();
    }


    private void initDrawerLayout(){
        drawerLayout.closeDrawer(GravityCompat.START);
        drawerLayout.setScrimColor(getResources().getColor(android.R.color.transparent));
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                null,  /* nav drawer image to replace 'Up' caret */
                R.string.app_name,  /* "open drawer" description for accessibility */
                R.string.app_name  /* "close drawer" description for accessibility */
        );
        drawerLayout.addDrawerListener(drawerToggle);
    }

    private void initDrawerListView() {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.activity_complete_form_drawer_list_item, Arrays.asList(FormPagerAdapter.PAGE_TITLES));
        drawerListView.setAdapter(arrayAdapter);

        drawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                viewPager.setCurrentItem(position);
                drawerLayout.closeDrawer(GravityCompat.START);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.form_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.send:
                onTryToSubmitForm(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void onTryToSubmitForm(boolean triggeredByUser){
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        //Store error pagePosition -> errorMessage mapping in order to
        //Change the position of viewPager to the first occurred error position
        final Map<Integer, String> positionToMessageMapping = new TreeMap<Integer, String>();


        //synchronous validation callback
        OnValidationResultListener validationResultListener = new OnValidationResultListener() {
            @Override
            public void onValidationResultListener(boolean success, int pagePosition, String firstErrorMessage) {
                if (!success){
                    formContainsValidationErrors = true;
                    positionToMessageMapping.put(pagePosition, firstErrorMessage);
                }
            }
        };

        for(Fragment f: fragmentList){
            if (f != null && f instanceof FormObjectProvider && f.isAdded()){
                ((FormObjectProvider)f).validateForm(validationResultListener);
            }
        }


        Snackbar messageSnackbar = Snackbar.make(drawerLayout, "", Snackbar.LENGTH_LONG);
        if (formContainsValidationErrors && !positionToMessageMapping.isEmpty()){
            //First entry is the one with smallest page number
            Map.Entry<Integer, String> firstEntry = positionToMessageMapping.entrySet().iterator().next();
            int errorPagePosition = firstEntry.getKey();
            String errorMessage = firstEntry.getValue();
            if (triggeredByUser)
                viewPager.setCurrentItem(errorPagePosition);
            messageSnackbar.setText(errorMessage);
        }else {
            messageSnackbar.setText("Forms are valid");
        }
        if (triggeredByUser)
            messageSnackbar.show();
    }

    @OnPageChange(R.id.viewPager)
    void changeActivityTitle(){
        setTitle(viewPager.getAdapter().getPageTitle(viewPager.getCurrentItem()));
    }

}
