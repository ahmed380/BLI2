package com.smehsn.compassinsurance.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.smehsn.compassinsurance.R;
import com.smehsn.compassinsurance.dao.Dealer;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_CODE = 123;
    private static final boolean DEBUG_MODE = true;
    private static final Class DEBUGGABLE_ACTIVITY = SettingsActivity.class;
    private Dealer dealer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initBeans();
        if (checkPermissions() && DEBUG_MODE)
            routeActivities();
    }

    private void initBeans() {
        dealer = Dealer.getInstance(this);
    }


    @OnClick(R.id.button)
    void onOpenFormActivity(){
        startActivity(new Intent(MainActivity.this, CompleteFormActivity.class));
    }

    @OnClick(R.id.button2)
    void onOpenSettings(){
        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
    }

    private void routeActivities() {

        Class activityClass;
        if(!dealer.isIdentified()){
            activityClass = SettingsActivity.class;
        }
        else if (DEBUGGABLE_ACTIVITY == null){
            activityClass = CompleteFormActivity.class;
        }
        else{
            activityClass = DEBUGGABLE_ACTIVITY;
        }
        startActivity(new Intent(this, activityClass));
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
        else routeActivities();
    }
}
