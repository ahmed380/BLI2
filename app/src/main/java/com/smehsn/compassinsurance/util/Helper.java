package com.smehsn.compassinsurance.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.smehsn.compassinsurance.data.pref.Dealer;
import com.smehsn.compassinsurance.data.pref.EmailConfig;

import java.util.Map;


public class Helper {

    public static String createEmailBody(Map<String, Map<String, String>> email)  {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Map<String, String>> entry: email.entrySet()){
            String formSubtitle = entry.getKey();
            sb
                    .append("<h3>")
                    .append(formSubtitle)
                    .append("</h3>")
                    .append("<fieldset>");
            for (Map.Entry<String, String> fields: entry.getValue().entrySet()){
                sb
                        .append("<i><b>")
                        .append(fields.getKey())
                        .append("</i></b>")
                        .append(": ")
                        .append(TextUtils.isEmpty(fields.getValue())? "blank": fields.getValue())
                        .append("<br/>");
            }
            sb.append("</fieldset>");

        }
        return sb.toString();
    }

    public static boolean internetIsConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

    }


    public static boolean isAppConfigured(Context context){
        Dealer dealer = Dealer.getInstance(context);
        EmailConfig emailConfig = EmailConfig.getInstance(context);
        return dealer.isIdentified() && emailConfig.isVerified();
    }
}
