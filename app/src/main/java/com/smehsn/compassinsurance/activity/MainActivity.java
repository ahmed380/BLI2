package com.smehsn.compassinsurance.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.smehsn.compassinsurance.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startActivity(new Intent(this, DealerRegisterActivity.class));

//        if(checkPermissions())
//            openFormActivity();
    }







    private void openFormActivity(){
        Intent intent = new Intent(this, CompleteFormActivity.class);
        startActivity(intent);
    }


    private boolean checkPermissions(){
        PackageManager pm = getPackageManager();
        PackageInfo info = null;
        boolean unallowedPermissions = false;
        try{
            info = pm.getPackageInfo(getPackageName(), PackageManager.GET_PERMISSIONS);
            String permissions[] = info.requestedPermissions;
            List<String> notGrantedPermissions = new ArrayList<>();
            for (String permission: permissions){
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                    notGrantedPermissions.add(permission);
            }
            if (notGrantedPermissions.size() != 0){
                unallowedPermissions = true;
                String []requestDialogPermissions = notGrantedPermissions.toArray(new String[notGrantedPermissions.size()]);
                ActivityCompat.requestPermissions(this, requestDialogPermissions, REQUEST_PERMISSION_CODE);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return !unallowedPermissions;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean unallowedPermission = false;
        for (int res: grantResults){
            if (res != PackageManager.PERMISSION_GRANTED){
                unallowedPermission = true;
                Toast.makeText(MainActivity.this, "Please grant all permissions", Toast.LENGTH_SHORT).show();
                break;
            }
        }
        if (unallowedPermission)
            checkPermissions();
        else openFormActivity();
    }
}
