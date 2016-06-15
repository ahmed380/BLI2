package com.smehsn.compassinsurance.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.smehsn.compassinsurance.R;
import com.smehsn.compassinsurance.dao.EmailConfig;
import com.smehsn.compassinsurance.dialog.ProgressDialogFragment;
import com.smehsn.compassinsurance.email.Email;
import com.smehsn.compassinsurance.email.EmailClient;
import com.smehsn.compassinsurance.email.EmailFinishedEvent;
import com.smehsn.compassinsurance.parser.FormParser;
import com.smehsn.compassinsurance.parser.FormValidationException;
import com.smehsn.compassinsurance.parser.fragment.FormHostingFragment;
import com.squareup.otto.Subscribe;

import java.util.Collections;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Sam
 */
public class EmailConfigFragment extends FormHostingFragment {


    private static final String PROGRESS_FRAGMENT_TAG = "progress_fragment";
    private FormParser parser;
    private EmailConfig emailConfig;
    private View rootView;


    @BindView(R.id.clientEmail)
    EditText clientEmail;

    @BindView(R.id.clientPassword)
    EditText clientPassword;

    @BindView(R.id.recipientEmail)
    EditText recipientEmail;


    public static EmailConfigFragment newInstance(){
        return new EmailConfigFragment();
    }

    @OnClick(R.id.testConnection)
    void onTetsButtonClicked(){
        try {
            parseForm();
            sendEmail();
        } catch (FormValidationException e) {
            e.printStackTrace();
            Snackbar.make(rootView, e.getErrorMessage(), Snackbar.LENGTH_LONG).show();
        }

    }





    private void sendEmail(){
        String email = clientEmail.getText().toString();
        String password = clientPassword.getText().toString();
        String recipient = recipientEmail.getText().toString();

        emailConfig.setClientEmailAddress(email);
        emailConfig.setClientEmailPassword(password);
        emailConfig.setRecipientAddressed(Collections.singletonList(recipient));

        EmailClient testClient = new EmailClient(email, password);
        Email testEmail = new Email()
                .setRecipientAddresses(new String[]{recipient})
                .setBody("Successfully configured client email !")
                .setSubject("Test Email from client");
        testClient.sendAsync(testEmail);

        Bundle args = new Bundle();
        args.putString("message", "Please wait...");
        args.putString("title", "Sending test email");

        ProgressDialogFragment dialogFragment = ProgressDialogFragment.newInstance(args);
        dialogFragment.show(getFragmentManager(), PROGRESS_FRAGMENT_TAG);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        emailConfig = EmailConfig.getInstance(getContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        EmailClient.getEventBus().register(this);

    }

    @Override
    public void onPause() {
        super.onPause();
        EmailClient.getEventBus().unregister(this);
    }


    @Subscribe
    void emailResult(EmailFinishedEvent ev){
        Fragment fragment = getFragmentManager().findFragmentByTag(PROGRESS_FRAGMENT_TAG);
        if (fragment != null){
            ((ProgressDialogFragment) fragment).dismiss();
        }
        if (ev.isSuccess()){
            emailConfig.setVerified(true);
            Toast.makeText(getContext(), "Sent", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(getContext(), "Failure", Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.config_mail, container, false);
        ButterKnife.bind(this, rootView);
        parser = new FormParser(getContext(), rootView);
        return rootView;
    }

    @Override
    public Map<String, String> parseForm() throws FormValidationException {
        parser.parse();
        return null;
    }

    @Override
    public String getFormTitle() {
        return null;
    }

}
