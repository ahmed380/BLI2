package com.smehsn.compassinsurance.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

import com.smehsn.compassinsurance.email.EmailClient;

import java.lang.reflect.Field;


public class Helper {

    public static String objectToEmailBody(Context ctx, String formTitle, Object obj) throws IllegalAccessException {
        Field fields[] = obj.getClass().getDeclaredFields();
        StringBuilder sb = new StringBuilder();
        sb
                .append("<h4>")
                .append(formTitle)
                .append("</h4>");
        for (Field field: fields){
            if (field.getType() != String.class)
                continue;
            field.setAccessible(true);
            int formFieldDisplayLabelId = ctx.getResources().getIdentifier(
                    "label_" + field.getName(),
                    "string",  ctx.getPackageName());
            String label = ctx.getResources().getString(formFieldDisplayLabelId);
            String value = (String) field.get(obj);
            sb
                    .append("<i><b>")
                    .append(label)
                    .append("</i></b>")
                    .append(": ")
                    .append(isEmpty(value)? "blank": value)
                    .append("<br/>");
        }
        sb.append("<br/>");
        return sb.toString();
    }


    public static boolean isEmpty(String str){
        return str == null || str.trim().equals("");
    }

    public static boolean internetIsConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

    }
}
