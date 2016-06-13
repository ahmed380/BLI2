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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.smehsn.compassinsurance.R;
import com.smehsn.compassinsurance.adapter.FormPagerAdapter;
import com.smehsn.compassinsurance.dialog.ProgressDialogFragment;
import com.smehsn.compassinsurance.email.Email;
import com.smehsn.compassinsurance.email.EmailClient;
import com.smehsn.compassinsurance.email.EmailFinishedEvent;
import com.smehsn.compassinsurance.util.Helper;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

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
    private boolean formContainsValidationErrors = false;
    private boolean emailIsSending               = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_form);
        ButterKnife.bind(this);
        initViewPager();
        initDrawerLayout();
        initDrawerListView();
        initEmailClient();
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerEventBus();
    }


    @Override
    protected void onPause() {
        super.onPause();
        unSubscribeEventBus();
    }

    private void unSubscribeEventBus() {
        EmailClient.getEventBus().unregister(this);
    }

    private void registerEventBus() {
        EmailClient.getEventBus().register(this);
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
                onTryToSubmitForm(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void onTryToSubmitForm(boolean triggeredByUser) {
        //Store error pagePosition -> errorMessage mapping in order to
        //Change the position of viewPager to the first occurred error position
        final Map<Integer, String> positionToMessageMapping = new TreeMap<>();
        //Store pagePosition -> model object mapping to get sorted access
        final Map<Integer, Object> positionToFormModelMapping = new TreeMap<>();
        final List<File> attachments = new ArrayList<>();


        formContainsValidationErrors = false;
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment != null && fragment.isAdded()) {

            }
        }


        if (formContainsValidationErrors && !positionToMessageMapping.isEmpty()) {
            final Snackbar messageSnackbar = Snackbar.make(drawerLayout, "", Snackbar.LENGTH_LONG);
            //First entry is the one with smallest page number
            Map.Entry<Integer, String> firstEntry = positionToMessageMapping.entrySet().iterator().next();
            int errorPagePosition = firstEntry.getKey();
            String errorMessage = firstEntry.getValue();
            if (triggeredByUser) {
                viewPager.setCurrentItem(errorPagePosition);
                messageSnackbar.show();
            }
            messageSnackbar.setText(errorMessage);
        } else if (!Helper.internetIsConnected(this)) {
            Snackbar.make(drawerLayout, "Failed to connect to the internet", Snackbar.LENGTH_LONG).show();
        } else if (!formContainsValidationErrors && triggeredByUser) {
            final SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd HH:mm", Locale.US);
            final String formattedDate = sdf.format(Calendar.getInstance().getTime());
            final Email email = new Email()
                    .setRecipientAddresses(getResources().getStringArray(R.array.recipient_email_addresses))
                    .setSubject(" " + formattedDate)
                    .setBody(buildEmailBody(positionToFormModelMapping))
                    .setAttachments(attachments);
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
        StringBuilder email = new StringBuilder();


        for (Integer position : positionToModelMapping.keySet()) {

            email.append(Helper.objectToEmailBody(
                    this,
                    viewPagerAdapter.getPageTitle(position).toString(),
                    positionToModelMapping.get(position)));

        }
        return email.toString();
    }

    @OnPageChange(R.id.viewPager)
    void changeActivityTitle() {
        setTitle(viewPagerAdapter.getPageTitle(viewPager.getCurrentItem()));
    }



}
