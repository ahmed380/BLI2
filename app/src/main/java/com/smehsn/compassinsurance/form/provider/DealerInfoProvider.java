package com.smehsn.compassinsurance.form.provider;

import android.content.Context;
import android.content.SharedPreferences;

import com.smehsn.compassinsurance.form.DealerInfo;
import com.smehsn.compassinsurance.form.FormObjectProvider;
import com.smehsn.compassinsurance.form.ValidationException;

import java.lang.reflect.Field;

/**
 * Created by sam on 6/12/16.
 */
public class DealerInfoProvider implements FormObjectProvider{

    public static final String PREF_NAME = "dealer_info";

    private static DealerInfoProvider myInstance;
    private DealerInfo dealerInfo;
    private SharedPreferences prefs;

    public static synchronized DealerInfoProvider getInstance(Context context){
        if (myInstance == null)
            myInstance = new DealerInfoProvider(context);
        return myInstance;
    }

    private DealerInfoProvider(Context context){
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        dealerInfo = new DealerInfo();
        initDealerInfo();
    }

    private void initDealerInfo(){
        Field[] fields = dealerInfo.getClass().getDeclaredFields();

        for(Field field: fields){
            field.setAccessible(true);
            if (field.getType() == String.class) {
                try {
                    field.set(dealerInfo, prefs.getString(field.getName(), "blank"));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public Object getFormModelObject() {
        return dealerInfo;
    }

    @Override
    public int getPagePosition() {
        return 0;
    }

    @Override
    public String getTitle() {
        return "Dealer Information";
    }

    public String getDealerName() {
        return dealerInfo.getName();
    }
}
