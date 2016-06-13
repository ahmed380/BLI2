package com.smehsn.compassinsurance.parser;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smehsn.compassinsurance.parser.fragment.FormHostingFragment;
import com.smehsn.compassinsurance.parser.fragment.FormParser;

import java.util.Map;

import icepick.Icepick;
import icepick.State;


public final class SimpleFormFragment extends FormHostingFragment {
    private FormParser formParser;
    @State
    int layoutResId;
    @State
    String formTitle;


    public static SimpleFormFragment newInstance(int resId, String title){
        SimpleFormFragment fragment = new SimpleFormFragment();
        fragment.layoutResId = resId;
        fragment.formTitle = title;
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        View rootView = inflater.inflate(layoutResId, container ,false);
        formParser = new FormParser(getContext(), rootView);
        return rootView;
    }


    @Override
    public Map<String, String> parseForm() throws FormValidationException {
        super.parseForm();
        return formParser.parse();
    }

    @Override
    public String getFormTitle() {
        return formTitle;
    }

}
