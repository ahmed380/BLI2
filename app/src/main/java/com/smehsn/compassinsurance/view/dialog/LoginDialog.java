package com.smehsn.compassinsurance.view.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.smehsn.compassinsurance.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Sam
 */
public class LoginDialog extends DialogFragment{

    private OnLoginCompleteListener listener;

    @BindView(R.id.usernameText)
    EditText login;


    @BindView(R.id.passwordText)
    EditText password;


    @OnClick(R.id.submitButton)
    void onSubmit(){
        if (TextUtils.isEmpty(login.getText().toString()) || TextUtils.isEmpty(password.getText().toString())){
            Toast.makeText(getContext(), "Please fill the fields", Toast.LENGTH_SHORT).show();
        }else if(listener != null){
            this.dismiss();
            listener.onLoginComplete(login.getText().toString(), password.getText().toString());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.dialog_login, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLoginCompleteListener)
            this.listener = (OnLoginCompleteListener) context;
    }

    public interface OnLoginCompleteListener{
        void onLoginComplete(String username, String password);
    }
}
