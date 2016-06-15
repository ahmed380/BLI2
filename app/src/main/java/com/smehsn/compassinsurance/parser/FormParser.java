package com.smehsn.compassinsurance.parser;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.smehsn.compassinsurance.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class FormParser {


    private Map<String, FormElement> labelToElementMap = new LinkedHashMap<>();
    private View    rootView;
    private Context context;


    public FormParser(Context context, View view){
        this.context = context;
        setRootView(view);
    }

    public void setRootView(View view){
        this.rootView = view;
        if (!(rootView instanceof ViewGroup)){
            throw new IllegalArgumentException("rootView is not ViewGroup");
        }
        scanViewTree((ViewGroup) rootView);
    }


    private void scanViewTree(ViewGroup parent){
        for (int i=0; i<parent.getChildCount(); ++i){
            View child = parent.getChildAt(i);
            if(child instanceof ViewGroup && !(child instanceof Spinner)){
                scanViewTree((ViewGroup) child);
            }
            else if ( child.getTag() != null){
                String tag = (String) child.getTag();
                try {
                    JSONObject config = new JSONObject(tag);
                    handleConfig(child, config);
                } catch (JSONException e) {
                    throw new IllegalArgumentException("Can't parse json tag for " + child.toString());
                }
            }
        }
    }

    private View findViewByIdName(String name){
        int id = context.getResources().getIdentifier(name, "id", context.getPackageName());
        if (id == 0)
            throw new IllegalArgumentException("No view with idName=" + name );
        return rootView.findViewById(id);
    }

    private String findStringResourceByName(String name){
        int id = context.getResources().getIdentifier(name, "string", context.getPackageName());
        if (id == 0)
            throw new IllegalArgumentException("Resource @string/" + name + " does not exist");
        return context.getString(id);
    }


    private void handleConfig(View view, JSONObject config){
        FormElement element = new FormElement(view, config);

        //Required configuration fields
        // strategy: String
        try{
            element.stringConverter = ConvertStrategyProvider.get(config.getString("strategy"));
        }catch (JSONException e){
            throw new IllegalArgumentException("View must contain stringConverter config field in android:tag");
        }

        //label: String
        try{
            element.label = findStringResourceByName(config.getString("label"));
        } catch (JSONException e) {
            throw new IllegalArgumentException("View must contain label config field in android:tag");
        }


        //Optional configuration fields
        //validators: [String]
        try{
            JSONArray validatorNames = config.getJSONArray("validators");
            for(int i=0; i<validatorNames.length(); ++i){
                String validatorName = (String) validatorNames.get(i);
                element.addValidator(ValidatorProvider.get(validatorName));
            }
        } catch (JSONException ignored) {}

        //labelView: String
        try {
            String labelId = config.getString("labelView");
            if (!TextUtils.isEmpty(labelId))
                element.labelView = (TextView) findViewByIdName(labelId);
        } catch (JSONException ignored) {}


        labelToElementMap.put(element.label, element);
    }


    public Map<String, String> parse() throws FormValidationException {
        Map<String, String> labelToInputMapping = new LinkedHashMap<>();

        String firstErrorMessage = null;

        for (Map.Entry<String, FormElement> entry: labelToElementMap.entrySet()){

            if (!entry.getValue().inputView.isShown())
                continue;

            FormElement element = entry.getValue();
            String converted = element.stringConverter.convertViewToString(element.inputView);
            for (FormValidator validator: element.validators){
                if(!validator.isValid(converted)){
                    if (element.labelView != null)
                        element.labelView.setBackgroundColor(context.getResources().getColor(R.color.label_error_background));
                    if(firstErrorMessage == null)
                        firstErrorMessage = validator.errorMessage(element.label);
                }
                else if (element.labelView != null){
                    element.labelView.setBackgroundColor(context.getResources().getColor(R.color.label_default_background));
                }
            }
            labelToInputMapping.put(element.label, converted);
        }

        if (firstErrorMessage != null){
            throw new FormValidationException(firstErrorMessage);
        }

        return labelToInputMapping;
    }


    private static final class FormElement{
        public TextView        labelView;
        public View            inputView;
        public StringConverter stringConverter;
        public JSONObject      config;
        public String          label;
        public List<FormValidator> validators = new ArrayList<>();
        public FormElement(View input, JSONObject config){
            this.inputView = input;
            this.config = config;
        }
        public void addValidator(FormValidator v){
            this.validators.add(v);
        }
    }


}
