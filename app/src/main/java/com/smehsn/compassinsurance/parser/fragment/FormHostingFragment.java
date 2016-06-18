package com.smehsn.compassinsurance.parser.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.smehsn.compassinsurance.parser.FormValidationException;

import icepick.Icepick;
import icepick.State;

/**
 * @author Sam
 */
public abstract class FormHostingFragment extends Fragment implements FormProvider{
    @State boolean validateRequested = false;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);

        if (validateRequested){
            try {
                validateAndGetForm();
            } catch (FormValidationException ignored) {}
        }
    }

    public void setValidateRequested(boolean val){
        this.validateRequested = val;
    }


}
