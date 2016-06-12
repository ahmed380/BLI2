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
import android.widget.Toast;

import com.smehsn.compassinsurance.R;
import com.smehsn.compassinsurance.adapter.FormPagerAdapter;
import com.smehsn.compassinsurance.email.Email;
import com.smehsn.compassinsurance.email.EmailClient;
import com.smehsn.compassinsurance.form.ValidationException;
import com.smehsn.compassinsurance.form.AttachmentProvider;
import com.smehsn.compassinsurance.form.FormObjectProvider;
import com.smehsn.compassinsurance.util.Helper;

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
    private static final String TAG = CompleteFormActivity.class.getSimpleName();

    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;

    @BindView(R.id.drawerListView)
    ListView drawerListView;

    private FragmentPagerAdapter viewPagerAdapter;
    private EmailClient emailClient;
    private boolean formContainsValidationErrors = false;
    private boolean emailIsSending = false;

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
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        formContainsValidationErrors = savedInstanceState.getBoolean("formContainsValidationErrors");
        emailIsSending = savedInstanceState.getBoolean("emailIsSending");

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
        outState.putBoolean("emailIsSending", emailIsSending);
    }


    private void initViewPager(){
        viewPagerAdapter = new FormPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOffscreenPageLimit(viewPagerAdapter.getCount());
        viewPager.setCurrentItem(viewPagerAdapter.getCount()-1);
        changeActivityTitle();
    }


    private void initDrawerLayout(){
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

    private void initEmailClient(){
        emailClient = EmailClient.getInstance(
                getString(R.string.client_email_address),
                getString(R.string.client_email_password)
        );
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        for (Fragment fragment: fragmentList ){
            if (fragment != null && fragment.isAdded()){
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
        switch(item.getItemId()){
            case R.id.send:
                onTryToSubmitForm(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void onTryToSubmitForm(boolean triggeredByUser){
        final List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        //Store error pagePosition -> errorMessage mapping in order to
        //Change the position of viewPager to the first occurred error position
        final Map<Integer, String> positionToMessageMapping = new TreeMap<>();
        //Store pagePosition -> model object mapping to get sorted access
        final Map<Integer, Object> positionToFormModelMapping = new TreeMap<>();
        final List<File> attachments = new ArrayList<>();

        formContainsValidationErrors = false;
        for(Fragment fragment: fragmentList){
            if (fragment != null && fragment.isAdded()){
                try {
                    if (fragment instanceof FormObjectProvider) {
                        FormObjectProvider provider = (FormObjectProvider) fragment;
                        Object modelObject = provider.getFormModelObject();
                        int position = provider.getPagePosition();
                        positionToFormModelMapping.put(position, modelObject);
                    }
                    if (fragment instanceof AttachmentProvider){
                        AttachmentProvider provider = (AttachmentProvider) fragment;
                        List<File> fragmentAttachments = provider.getAttachedFiles();
                        for (File f: fragmentAttachments)
                            attachments.add(f);
                    }
                }
                catch (ValidationException e) {
                    formContainsValidationErrors = true;
                    positionToMessageMapping.put(e.getPagePosition(), e.getValidationErrorMessage());
                }
            }
        }


        if (formContainsValidationErrors && !positionToMessageMapping.isEmpty()){
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
        }
        else if (!Helper.internetIsConnected(this)){
            Snackbar.make(drawerLayout, "Failed to connect to the internet", Snackbar.LENGTH_LONG);
        }
        else if (!formContainsValidationErrors && triggeredByUser){
            final SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy, HH:mm", Locale.US);
            final String formattedDate = sdf.format(Calendar.getInstance().getTime());
            final Email email = new Email()
                    .setRecipientAddresses(getResources().getStringArray(R.array.recipient_email_addresses))
                    .setSubject(  getString(R.string.default_email_subject) + " " + formattedDate)
                    .setBody(buildEmailBody(positionToFormModelMapping))
                    .setAttachments(attachments);
            sendEmail(email);
        }

    }


    private void sendEmail(Email email){

//        ProgressDialogFragment progressDialog = new ProgressDialogFragment();
//        Bundle bundle = new Bundle();
//        bundle.putString("title", "Submitting form");
//        bundle.putString("message", "Please wait...");
//        progressDialog.setRetainInstance(true);
//        progressDialog.setArguments(bundle);
//        progressDialog.show(getSupportFragmentManager(), "progressDialog");
        Snackbar.make(drawerLayout, "Submitting form", Snackbar.LENGTH_LONG).show();
        emailClient.sendAsync(email, new EmailClient.OnEmailSentListener() {
            @Override
            public void onEmailSent(boolean success) {
//                FragmentManager fragmentManager = getSupportFragmentManager();
//                DialogFragment dialog = (DialogFragment) fragmentManager.findFragmentByTag("progressDialog");
//                if (dialog.isAdded()){
//                    dialog.dismiss();
//                }
                String displayMessage = success ? "Form submitted successfully" : "Failed submitting form, ";
                if (!success && !Helper.internetIsConnected(CompleteFormActivity.this)){
                    displayMessage += "please check your connection";
                }
                else if(!success){
                    displayMessage += "please contact the insurance";
                }
                Toast.makeText(CompleteFormActivity.this, displayMessage, Toast.LENGTH_SHORT).show();
            }
        });
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
