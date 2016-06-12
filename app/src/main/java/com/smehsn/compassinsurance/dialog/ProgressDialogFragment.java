package com.smehsn.compassinsurance.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;



public class ProgressDialogFragment extends DialogFragment
{


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setCancelable(false);
    }

    public static ProgressDialogFragment newInstance(Bundle arg){
        ProgressDialogFragment fragment = new ProgressDialogFragment();
        fragment.setArguments(arg);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public ProgressDialogFragment(){

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        ProgressDialog dialog = new ProgressDialog(getActivity(), getTheme());
        dialog.setTitle(getArguments().getString("title"));
        dialog.setMessage(getArguments().getString("message"));
        dialog.setIndeterminate(true);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        return dialog;
    }
}
