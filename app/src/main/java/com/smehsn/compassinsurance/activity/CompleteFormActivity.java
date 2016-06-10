package com.smehsn.compassinsurance.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.smehsn.compassinsurance.R;
import com.smehsn.compassinsurance.util.Helper;
import com.smehsn.compassinsurance.adapter.FormPagerAdapter;
import com.smehsn.compassinsurance.fragment.FormObjectProvider;
import com.smehsn.compassinsurance.fragment.ValidationException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
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

    private FragmentPagerAdapter viewPagerAdapter;

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
        viewPagerAdapter = new FormPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
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
        //Store pagePosition -> model object mapping to get sorted access
        final Map<Integer, Object> positionToFormModelMapping = new TreeMap<>();
        //synchronous validation callback



        for(Fragment fragment: fragmentList){
            if (fragment != null && fragment instanceof FormObjectProvider && fragment.isAdded()){
                try {
                    FormObjectProvider provider = (FormObjectProvider)fragment;
                    Object modelObject = provider.getFormModelObject();
                    int position = provider.getPagePosition();
                    positionToFormModelMapping.put(position, modelObject);

                }
                catch (ValidationException e) {
                    formContainsValidationErrors = true;
                    positionToMessageMapping.put(e.getPagePosition(), e.getValidationErrorMessage());
                }
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
            String emailBody = buildEmailBody(positionToFormModelMapping);
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("message/rfc822");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"samvel1024@gmail.com"});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Report");
            emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(emailBody));
            startActivity(emailIntent);
        }
        if (triggeredByUser)
            messageSnackbar.show();
    }


    private String buildEmailBody(Map<Integer, Object> positionToMessageMapping){
        StringBuilder email = new StringBuilder();
        for(Integer position: positionToMessageMapping.keySet()){
            try {
                email.append(Helper.objectToEmailBody(
                        this,
                        viewPagerAdapter.getPageTitle(position).toString(),
                        positionToMessageMapping.get(position)));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return email.toString();
    }

    @OnPageChange(R.id.viewPager)
    void changeActivityTitle(){
        setTitle(viewPagerAdapter.getPageTitle(viewPager.getCurrentItem()));
    }

}
