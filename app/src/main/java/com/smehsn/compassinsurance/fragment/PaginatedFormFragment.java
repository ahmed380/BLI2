package com.smehsn.compassinsurance.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.smehsn.compassinsurance.R;
import com.smehsn.compassinsurance.adapter.FormPagerAdapter;
import com.smehsn.compassinsurance.dialog.DateDialog;
import com.smehsn.compassinsurance.form.Coverages;
import com.smehsn.compassinsurance.form.DealerInfo;
import com.smehsn.compassinsurance.form.FormObjectProvider;
import com.smehsn.compassinsurance.form.GeneralInfo;
import com.smehsn.compassinsurance.form.InsuredInfo;
import com.smehsn.compassinsurance.form.ValidationException;
import com.smehsn.compassinsurance.form.VehicleCoverages;
import com.smehsn.compassinsurance.form.VehicleInfo;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;


public class PaginatedFormFragment extends Fragment implements FormObjectProvider {

    private static final Map<Integer, Class> FORM_MODEL_MAPPING;
    private static final String TAG = PaginatedFormFragment.class.getSimpleName();

    static {
        FORM_MODEL_MAPPING = new HashMap<>();
        FORM_MODEL_MAPPING.put(R.layout.form_general_info, GeneralInfo.class);
        FORM_MODEL_MAPPING.put(R.layout.form_coverages, Coverages.class);
        FORM_MODEL_MAPPING.put(R.layout.form_insured_info, InsuredInfo.class);
        FORM_MODEL_MAPPING.put(R.layout.form_vehicle_coverages, VehicleCoverages.class);
        FORM_MODEL_MAPPING.put(R.layout.form_vehicle_info, VehicleInfo.class);
        FORM_MODEL_MAPPING.put(R.layout.form_dealer_info, DealerInfo.class);
    }

    //The layout which this fragment should host
    private int layoutResId;
    //The title of this page in view pager
    private String displayName;
    //The root view inflated from layoutResId
    private View rootView;
    //The class containing all fields as Strings for this form
    private Class modelClass;
    //The position of hosting view pager
    private int positionInViewPager;


    public PaginatedFormFragment() {}

    public static PaginatedFormFragment newInstance(int id, String name, int position) {
        PaginatedFormFragment fragment = new PaginatedFormFragment();
        fragment.displayName = name;
        fragment.layoutResId = id;
        fragment.positionInViewPager = position;
        fragment.modelClass = FORM_MODEL_MAPPING.get(id);
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
            modelClass = FORM_MODEL_MAPPING.get(layoutResId);
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


    /**
     * onClick listener for forms containing date picker
     * trigger button with R.id.date
     * @param v
     */
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

    /**
     * Utility method to find child view of rootView by id Name
     * @param idName
     * @return child of rootView
     */
    private View getViewByIdName(String idName){
        int viewResId = this.getResources().getIdentifier(idName, "id", this.getContext().getPackageName());
        return rootView.findViewById(viewResId);
    }


    @Override
    public Object getFormModelObject() throws ValidationException {
        Field[] fields = modelClass.getDeclaredFields();
        Object modelObject = null;
        try {
            modelObject = modelClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ValidationException validationException = null;
        for (Field field: fields){

            View view = getViewByIdName(field.getName());
            String value = null;

            if (view instanceof EditText) {
                value = handleEditTextToString((EditText) view);
            }
            else if (view instanceof Spinner){
                value = handleSpinnerToString((Spinner) view);
            }
            //Date button case
            else if (view instanceof Button){
                value = handleButtonToString((Button) view);
            }

            boolean isFieldRequired = false;//field.isAnnotationPresent(RequiredField.class);
            if (value != null){
                value = value.trim();
                TextView labelTextView = (TextView) getViewByIdName("label_" + field.getName());
                if(labelTextView == null)
                    throw new IllegalArgumentException("No label TextView for "+modelClass.getName() + ":" + field.getName());
                if (value.equals("") && isFieldRequired) {
                    labelTextView.setBackgroundColor(getResources().getColor(R.color.label_error_background));
                    if (validationException == null) {
                        int formFieldDisplayLabelId = getResources().getIdentifier(
                                "label_" + field.getName(),
                                "string", getContext().getPackageName());
                        String displayLabel = getResources().getString(formFieldDisplayLabelId);
                        String errorMessage = "Field " + displayLabel + " is required";
                        validationException = new ValidationException(positionInViewPager, errorMessage);
                    }
                }
                else{
                    field.setAccessible(true);
                    try {
                        field.set(modelObject, value);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    labelTextView.setBackgroundColor(getResources().getColor(R.color.label_default_background));
                }
            }
        }

        if (validationException != null)
            throw validationException;
        else
            return modelObject;

    }

    @Override
    public int getPagePosition() {
        return positionInViewPager;
    }

    @Override
    public String getTitle() {
        return FormPagerAdapter.PAGE_TITLES[positionInViewPager];
    }

    private String handleEditTextToString(EditText editText){
        return editText.getText().toString();
    }

    private String handleSpinnerToString(Spinner spinner){
        int position = spinner.getSelectedItemPosition();
        return (position == 0) ? "" : spinner.getSelectedItem().toString();
    }

    private String handleButtonToString(Button button){
        String dateText = button.getText().toString();
        dateText = dateText.equals(getString(R.string.date_picker_prompt))? "" : dateText;
        return dateText;
    }
}
