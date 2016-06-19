package com.smehsn.compassinsurance.view.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smehsn.compassinsurance.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Sam
 */
public class ConfirmDialog extends DialogFragment {

    private OnConfirmListener listener;


    @OnClick(R.id.confirmDialog)
    void onConfirm(){
        if (listener != null) {
            listener.onConfirm();
            this.dismiss();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.dialog_confirm , container, false);
        ButterKnife.bind(this,rootView);
        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnConfirmListener)
            this.listener = (OnConfirmListener) context;
    }

    public interface OnConfirmListener{
        void onConfirm();
    }
}
