package com.smehsn.compassinsurance.view.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.smehsn.compassinsurance.R;
import com.smehsn.compassinsurance.data.pref.AdminConfig;
import com.smehsn.compassinsurance.util.Helper;
import com.smehsn.compassinsurance.view.dialog.LoginDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements LoginDialog.OnLoginCompleteListener{
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String LOGIN_DIALOG_TAG = "login_dialog";
    private static final int    REQUEST_PERMISSION_CODE = 123;
    public  static final boolean DEBUG_MODE = false;
    private static final Class  DEBUGGABLE_ACTIVITY = MainActivity.class;




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

    }



    @OnClick(R.id.button)
    void onOpenFormActivity(){
        if(Helper.isAppConfigured(this))
            startActivity(new Intent(MainActivity.this, CompleteFormActivity.class));
        else
            Toast.makeText(MainActivity.this, "Please configure the application", Toast.LENGTH_SHORT).show();
    }



    private void routeActivities() {

        Class activityClass = null;


        if(DEBUG_MODE && DEBUGGABLE_ACTIVITY != null){
            activityClass = DEBUGGABLE_ACTIVITY;
        }

        if (activityClass != null && activityClass != MainActivity.class){
            startActivity(new Intent(this, activityClass));
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings:
                openLoginDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openLoginDialog() {
        LoginDialog dialog = new LoginDialog();
        dialog.show(getSupportFragmentManager(), LOGIN_DIALOG_TAG);
    }

    @Override
    public void onLoginComplete(String username, String password) {
        AdminConfig adminConfig = AdminConfig.getInstance(this);
        if (adminConfig.isAuthenticated(username, password)){
            SettingsActivity.launch(this, username, password);
        }else{
            Toast.makeText(MainActivity.this, "Wrong admin credentials", Toast.LENGTH_SHORT).show();
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
