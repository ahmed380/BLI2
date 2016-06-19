package com.smehsn.compassinsurance.data.pref;

import android.content.Context;

/**
 * @author Sam
 */
public class AdminConfig {

    private static AdminConfig instance;

    public static synchronized AdminConfig getInstance(Context ctx){
        if (instance == null)
            instance = new AdminConfig(ctx);
        return instance;
    }


    private AdminConfig(Context context){

    }


    public boolean isAuthenticated(String username, String pass){
        return username.equals("username") && pass.equals("password");
    }

}
