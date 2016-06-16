package com.smehsn.compassinsurance.dao;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * @author Sam
 */
public class EmailConfig {
    public static final String PREF_NAME = "email_config";
    SharedPreferences prefs;


    public synchronized static EmailConfig getInstance(Context context){
        return new EmailConfig(context);
    }


    private EmailConfig(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public String[] getRecipientEmailAddressed(){
        List<String> emails = new  ArrayList<>(prefs.getStringSet("recipients", new HashSet<>(Arrays.asList(new String[]{"samvel1024@gmail.com"}))));
        return emails.toArray(new String[emails.size()]);
    }

    public String getClientEmailAddress(){
        return prefs.getString("client_email", "compass.insurance.client@gmail.com");
    }

    public String getClientEmailPassword(){
        return prefs.getString("client_password", "clientpassword");
    }

    public void setRecipientAddressed(List<String> addresses){
        prefs.edit().putStringSet("recipients", new HashSet<>(addresses)).apply();
    }

    public void setClientEmailAddress(String email){
        prefs.edit().putString("client_email", email).apply();
    }

    public void setClientEmailPassword(String password){
        prefs.edit().putString("client_password", password).apply();
    }

    public void setVerified(boolean verified){
        prefs.edit().putBoolean("verified", verified).apply();
    }

    public boolean isVerified(){
        return prefs.getBoolean("verified", false);
    }

}
