package com.smehsn.compassinsurance.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import com.smehsn.compassinsurance.R;
import com.smehsn.compassinsurance.util.Helper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int    REQUEST_PERMISSION_CODE = 123;
    public  static final boolean DEBUG_MODE = false;
    private static final Class  DEBUGGABLE_ACTIVITY = SettingsActivity.class;

    @BindViews({R.id.edit1, R.id.edit2, R.id.edit3})
    List<EditText> inputs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermissions())
                routeActivities();
        }
        else {
            routeActivities();
        }

        initEditTexts();

    }

    private void initEditTexts(){
        for (int i=0; i<inputs.size(); ++i){
            final EditText current = inputs.get(i);
            final int currPos = i;
            inputs.get(i).addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after){

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count){
                    if (s.length() >= 3){
                        EditText next = inputs.get(currPos + 1);
                        if (next != null)
                            next.requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
    }


    @OnClick(R.id.button)
    void onOpenFormActivity(){
        if(Helper.isAppConfigured(this))
            startActivity(new Intent(MainActivity.this, CompleteFormActivity.class));
        else
            Toast.makeText(MainActivity.this, "Please configure the application", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.button2)
    void onOpenSettings(){
        if (!Helper.isAppConfigured(this))
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        else {
            Toast.makeText(MainActivity.this, "Application is already configured", Toast.LENGTH_SHORT).show();
        }
    }

    private void routeActivities() {

        Class activityClass = null;

        if(!Helper.isAppConfigured(this)){
            activityClass = SettingsActivity.class;
        }

        if(DEBUG_MODE && DEBUGGABLE_ACTIVITY != null){
            activityClass = DEBUGGABLE_ACTIVITY;
        }

        if (activityClass != null){
            startActivity(new Intent(this, activityClass));
        }

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
