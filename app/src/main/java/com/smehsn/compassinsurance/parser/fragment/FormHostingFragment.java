package com.smehsn.compassinsurance.parser.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.smehsn.compassinsurance.parser.FormValidationException;

import java.util.Map;

import icepick.Icepick;
import icepick.State;

/**
 * @author Sam
 */
public abstract class FormHostingFragment extends Fragment implements FormProvider{

    @State boolean wasParseRequested = false;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);

        if (wasParseRequested){
            try {
                parseForm();
            } catch (FormValidationException ignored) {}
        }
    }

    @Override
    public Map<String, String> parseForm() throws FormValidationException {
        wasParseRequested = true;
        return null;
    }
}
