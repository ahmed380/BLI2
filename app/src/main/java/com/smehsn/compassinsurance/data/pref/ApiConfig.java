package com.smehsn.compassinsurance.data.pref;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author Sam
 */
public class ApiConfig {
    public static final String PREF_NAME = "api_config";
    SharedPreferences sharedPreferences;

    public static ApiConfig getInstance(Context c){
        return new ApiConfig(c);
    }

    private ApiConfig (Context c){
        sharedPreferences = c.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setApiKey(String apiKey){
        sharedPreferences.edit().putString("api_key", apiKey).apply();
    }

    public String getApiKey(){
        return sharedPreferences.getString("api_key", "p6xf8mbwx2f3hq5eg48mfqzz");
    }

}
