package com.smehsn.compassinsurance.dao;

import android.content.Context;
import android.content.SharedPreferences;

import com.smehsn.compassinsurance.R;

import java.util.Map;

/**
 * @author Sam
 */
public class Dealer {
    private static Dealer instance;
    public static final String PREF_NAME = "dealer_info";
    private SharedPreferences prefs;
    private Context context;

    public synchronized static Dealer getInstance(Context context){
        if (instance == null)
            instance = new Dealer(context);
        return instance;
    }

    private Dealer(Context context){
        this.context = context;
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public Map<String, String> getInfo(){
        return (Map<String, String>) prefs.getAll();
    }

    public void saveDealerInfo(Map<String, String> data){
        SharedPreferences.Editor editor  =  prefs.edit();
        for (Map.Entry<String, String> entry: data.entrySet()){
            editor.putString(entry.getKey(), entry.getValue());
        }
        editor.apply();
    }

    public void setName(String name){
        prefs.edit().putString(context.getString(R.string.label_dealerName), name).apply();
    }

    public void setAddress(String addr){
        prefs.edit().putString(context.getString(R.string.label_dealerAddress), addr).apply();
    }

    public void setFax(String fax){
        prefs.edit().putString(context.getString(R.string.label_dealerFax), fax).apply();
    }

    public void setEmail(String email){
        prefs.edit().putString(context.getString(R.string.label_dealerEmail), email).apply();
    }

    public String getName(){
        return prefs.getString(context.getString(R.string.label_dealerName), "NO DEALER NAME!!!");
    }

    public boolean isIdentified(){
        return (prefs.getString(context.getString(R.string.label_dealerName), null) != null);
    }

}
