package com.smehsn.compassinsurance.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smehsn.compassinsurance.R;
import com.smehsn.compassinsurance.dialog.DateDialog;
import com.smehsn.compassinsurance.parser.FormParser;
import com.smehsn.compassinsurance.parser.FormValidationException;
import com.smehsn.compassinsurance.parser.fragment.FormHostingFragment;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import icepick.Icepick;
import icepick.State;

/**
 * @author Sam
 */
public class DriverInfoFormFragment extends FormHostingFragment {
    private static final String DATE_PICKER_DIALOG_TAG = "date_picker";
    private FormParser parser;

    @State
    String formTitle;
    @State
    boolean showSecondDriverForm = false;

    @BindView(R.id.secondDriverForm)
    LinearLayout formLayout;


    @OnCheckedChanged(R.id.secondDriverCheckbox)
    void toggleSecondDriver(boolean checked){
        showSecondDriverForm = checked;
        formLayout.setVisibility(checked ? View.VISIBLE : View.GONE);
    }


    @OnClick(R.id.datePicker)
    void datePicker(final TextView textView){
        DateDialog dateDialog = new DateDialog();
        dateDialog.setOnDateSetListener(new DateDialog.OnDateSetListener() {
            @Override
            public void onDateSet(String date) {
                textView.setText(date);
            }
        });
        dateDialog.show(getFragmentManager(), DATE_PICKER_DIALOG_TAG);
    }

    public static DriverInfoFormFragment newInstance(String title){
        DriverInfoFormFragment formFragment = new DriverInfoFormFragment();
        formFragment.formTitle = title;
        return formFragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Icepick.restoreInstanceState(this, savedInstanceState);
        View rootView = inflater.inflate(R.layout.form_driver_info, container, false);
        ButterKnife.bind(this, rootView);
        parser = new FormParser(getContext(), rootView);
        formLayout.setVisibility(showSecondDriverForm? View.VISIBLE : View.GONE);
        return rootView;
    }


    @Override
    public Map<String, String> validateAndGetForm() throws FormValidationException {
        return parser.parse();
    }

    @Override
    public String getFormTitle() {
        return formTitle;
    }
}
