package com.smehsn.compassinsurance.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.smehsn.compassinsurance.R;
import com.smehsn.compassinsurance.adapter.FormPagerAdapter;
import com.smehsn.compassinsurance.fragment.FormFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnPageChange;

public class CompleteFormActivity extends AppCompatActivity {
    private static final String TAG = CompleteFormActivity.class.getSimpleName();

    @BindView(R.id.view_pager)
    ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_form);
        ButterKnife.bind(this);
        initViewPager();
    }

    private void initViewPager(){
        FormPagerAdapter adapter = new FormPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(FormPagerAdapter.ITEMS_COUNT);
        changeActivityTitle();
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
                onSubmitForm();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void onSubmitForm(){
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        List<Object> models = new ArrayList<>();
        for(Fragment f: fragmentList){
            if (f instanceof FormFragment){
                Object form = ((FormFragment)f).getFormModelObject();
                models.add(form);
            }
        }
    }

    @OnPageChange(R.id.view_pager)
    void changeActivityTitle(){
        setTitle(viewPager.getAdapter().getPageTitle(viewPager.getCurrentItem()));
    }



}
