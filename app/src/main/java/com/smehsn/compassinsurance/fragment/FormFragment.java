package com.smehsn.compassinsurance.fragment;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.smehsn.compassinsurance.R;
import com.smehsn.compassinsurance.dialog.DateDialog;
import com.smehsn.compassinsurance.model.Coverages;
import com.smehsn.compassinsurance.model.GeneralInfo;
import com.smehsn.compassinsurance.model.InsuredInfo;
import com.smehsn.compassinsurance.model.RequiredField;
import com.smehsn.compassinsurance.model.VehicleCoverage;
import com.smehsn.compassinsurance.model.VehicleInfo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;


public class FormFragment extends Fragment{

    private static final Map<Integer, Class> formModelMapping;
    private static final String TAG = FormFragment.class.getSimpleName();

    static {
        formModelMapping = new HashMap<>();
        formModelMapping.put(R.layout.form_general_info, GeneralInfo.class);
        formModelMapping.put(R.layout.form_coverages, Coverages.class);
        formModelMapping.put(R.layout.form_insured_info, InsuredInfo.class);
        formModelMapping.put(R.layout.form_vehicle_coverage, VehicleCoverage.class);
        formModelMapping.put(R.layout.form_vehicle_info, VehicleInfo.class);
    }



    private int layoutResId;
    private String displayName;
    private View rootView;
    private Class modelClass;
    private int positionInViewPager;


    public FormFragment() {
        // Required empty public constructor
    }


    public static FormFragment newInstance(int id, String name, int position) {
        FormFragment fragment = new FormFragment();
        fragment.displayName = name;
        fragment.layoutResId = id;
        fragment.positionInViewPager = position;
        fragment.modelClass = formModelMapping.get(id);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("layoutResId", layoutResId);
        outState.putString("displayName", displayName);
        outState.putInt("positionInViewPager", positionInViewPager);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        restoreState(savedInstanceState);
    }

    private void restoreState(Bundle savedInstanceState){
        if (savedInstanceState != null){
            layoutResId = savedInstanceState.getInt("layoutResId");
            displayName = savedInstanceState.getString("displayName");
            positionInViewPager = savedInstanceState.getInt("positionInViewPager");
            modelClass = formModelMapping.get(layoutResId);
            Log.d(TAG, "Restoring state for page: " + positionInViewPager);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        restoreState(savedInstanceState);
        rootView = inflater.inflate(layoutResId, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }



    @Optional
    @OnClick(R.id.date)
    public void selectDate(View v){
        final Button button = (Button)v;
        DateDialog datePicker = new DateDialog();
        datePicker.setOnDateSetListener(new DateDialog.OnDateSetListener() {
            @Override
            public void onDateSet(String date) {
                button.setText(date);
            }
        });
        datePicker.show(getFragmentManager(), "datePicker");

    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    private Object instantiateModelClass(){
        Object model = null;
        try {
            model = modelClass.newInstance();
        } catch (java.lang.InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return model;
    }

    public Object getFormModelObject() {
        Object model = instantiateModelClass();

        Field[] fields = modelClass.getDeclaredFields();
        for (Field f: fields){
            f.setAccessible(true);
            String fieldName = f.getName();
            int viewResId = this.getResources().getIdentifier(fieldName, "id", this.getContext().getPackageName());
            View view = rootView.findViewById(viewResId);
            try {
                if (view instanceof EditText) {
                    f.set(model, handleEditTextToString((EditText) view));
                }
                if (view instanceof Spinner){
                    f.set(model, handleSpinnerToString((Spinner) view));
                }
            }catch (IllegalAccessException ex){
                ex.printStackTrace();
            }
        }
        return model;
    }

    private String handleEditTextToString(EditText editText){
        return editText.getText().toString();
    }

    private String handleSpinnerToString(Spinner spinner){
        int position = spinner.getSelectedItemPosition();
        return (position == 0) ? "" : spinner.getSelectedItem().toString();
    }

    public String getDisplayName() {
        return displayName;
    }
}
