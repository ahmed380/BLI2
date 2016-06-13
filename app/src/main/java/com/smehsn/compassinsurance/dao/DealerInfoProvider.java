package com.smehsn.compassinsurance.dao;

import android.content.Context;
import android.content.SharedPreferences;

import com.smehsn.compassinsurance.R;

import java.util.Map;

/**
 * @author Sam
 */
public class DealerInfoProvider {
    public static final String BEAN_KEY = DealerInfoProvider.class.getSimpleName();
    public static final String PREF_NAME = "dealer_info";
    private SharedPreferences prefs;
    private Context context;

    public DealerInfoProvider(Context context){
        this.context = context;
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public Map<String, String> getDealerInfo(){
        return (Map<String, String>) prefs.getAll();
    }

    public void saveDealerInfo(Map<String, String> data){
        SharedPreferences.Editor editor  =  prefs.edit();
        for (Map.Entry<String, String> entry: data.entrySet()){
            editor.putString(entry.getKey(), entry.getValue());
        }
        editor.apply();
    }

    public String getDealerName(){
        return prefs.getString(context.getString(R.string.label_dealerName), "NO DEALER NAME!!!");
    }

    public boolean dealerIdentified(){
        return (prefs.getString(context.getString(R.string.label_dealerName), null) != null);
    }

}
