package com.smehsn.compassinsurance.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.smehsn.compassinsurance.R;
import com.smehsn.compassinsurance.adapter.FormPagerAdapter;
import com.smehsn.compassinsurance.dao.Dealer;
import com.smehsn.compassinsurance.dialog.ProgressDialogFragment;
import com.smehsn.compassinsurance.email.Email;
import com.smehsn.compassinsurance.email.EmailClient;
import com.smehsn.compassinsurance.email.EmailFinishedEvent;
import com.smehsn.compassinsurance.parser.AttachmentProvider;
import com.smehsn.compassinsurance.parser.FormValidationException;
import com.smehsn.compassinsurance.parser.fragment.FormProvider;
import com.smehsn.compassinsurance.util.Helper;
import com.squareup.otto.Subscribe;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnPageChange;


public class CompleteFormActivity extends AppCompatActivity {
    private static final String TAG                       = CompleteFormActivity.class.getSimpleName();
    private static final String PROGRESS_DIALOG_FRAGMENT_TAG = "progressDialog";

    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;

    @BindView(R.id.drawerListView)
    ListView drawerListView;


    private FragmentPagerAdapter viewPagerAdapter;
    private EmailClient          emailClient;
    private Dealer               dealer;

    //************* Lifecycle callbacks *******************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_form);
        ButterKnife.bind(this);
        initBeans();
        initViewPager();
        initDrawerLayout();
        initDrawerListView();
        initEmailClient();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EmailClient.getEventBus().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EmailClient.getEventBus().unregister(this);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    //************* Initializers *******************************
    private void initBeans(){
        dealer = Dealer.getInstance(this);
    }

    private void initViewPager() {
        viewPagerAdapter = new FormPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOffscreenPageLimit(viewPagerAdapter.getCount());
        viewPager.setCurrentItem(0);
        changeActivityTitle();

    }

    private void initDrawerLayout() {


        drawerLayout.closeDrawer(GravityCompat.START);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                null,
                R.string.app_name,
                R.string.app_name
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

    private void initEmailClient() {
        emailClient = EmailClient.getInstance(
                getString(R.string.client_email_address),
                getString(R.string.client_email_password)
        );
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragmentList) {
            if (fragment != null && fragment.isAdded()) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.form_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.send:
                onTryToSubmitForm();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @OnPageChange(R.id.viewPager)
    void changeActivityTitle() {
        setTitle(viewPagerAdapter.getPageTitle(viewPager.getCurrentItem()));
    }



    private void onTryToSubmitForm() {
        FragmentManager fm  = getSupportFragmentManager();
        Map<String, Map<String , String>> formData= new LinkedHashMap<>();
        String errorMessage = null;
        int errorPagePosition = 0;
        AttachmentProvider attachmentProvider = null;
        for (int position = 0; position < viewPager.getChildCount(); ++position){
            String fragmentTag = "android:switcher:" + viewPager.getId() + ":" + position;
            Fragment fragment = fm.findFragmentByTag(fragmentTag);
            if (fragment instanceof AttachmentProvider){
                attachmentProvider = (AttachmentProvider) fragment;
            }
            FormProvider formProvider = (FormProvider) fragment;
            try {
                Map<String, String> form = formProvider.parseForm();
                formData.put(
                        viewPagerAdapter.getPageTitle(position).toString(),
                        form
                );
            } catch (FormValidationException e) {
                if (errorMessage == null) {
                    errorMessage = e.getErrorMessage();
                    errorPagePosition = position;
                }
            }
        }

        if (errorMessage != null){
            viewPager.setCurrentItem(errorPagePosition);
            Snackbar.make(drawerLayout, errorMessage, Snackbar.LENGTH_LONG).show();
        }else {
            Email email = new Email()
                    .setBody(Helper.createEmailBody(formData))
                    .setSubject(dealer.getName() + " : " + new Date().toString())
                    .setRecipientAddresses(getResources().getStringArray(R.array.recipient_email_addresses));
            if (attachmentProvider != null) {
                email.setAttachments(attachmentProvider.getAttachedFiles());
            }
            sendEmail(email);
        }
    }



    @Subscribe
    public void onEmailFinishedEvent(EmailFinishedEvent event){
        String message = event.isSuccess() ?  "Form submitted successfully": "Error submitting form, ";
        if (!event.isSuccess()){
            if (Helper.internetIsConnected(this))
                message += "program has bugs!!";
            else
                message += "please check your connection";
        }

        ProgressDialogFragment dialog = (ProgressDialogFragment) getSupportFragmentManager().findFragmentByTag(PROGRESS_DIALOG_FRAGMENT_TAG);
        if (dialog != null){
            dialog.dismiss();
            if (event.isSuccess()){
                Toast.makeText(CompleteFormActivity.this, message, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        Snackbar.make(drawerLayout, message, Snackbar.LENGTH_LONG).show();
    }



    private void sendEmail(Email email) {
        Bundle params = new Bundle();
        params.putString("title", "Sending email...");
        params.putString("message", "Please wait");
        ProgressDialogFragment progressDialog = ProgressDialogFragment.newInstance(params);
        progressDialog.show(getSupportFragmentManager(), PROGRESS_DIALOG_FRAGMENT_TAG);
        emailClient.sendAsync(email);
    }


    private String buildEmailBody(Map<Integer, Object> positionToModelMapping) {
        return null;
    }





}
