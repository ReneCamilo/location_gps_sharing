package com.example.developer.locationsharinggps.app;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.developer.locationsharinggps.R;

/**
 * Created by Rene Contreras on 7/25/17.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public String TAG = "./MainActivity";
    LocalService mService;
    public String lat, lon;
    boolean mBound = false;
    Button startStopServiceBtn;
    EditText phoneNumberEt;
    Intent intent;
    SharedPreferences.Editor editor;
    SharedPreferences sharedPref;
    Context context;
    int PRIVATE_MODE = 0;
    private final String PREF_NAME = "gps_data";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startStopServiceBtn = (Button) findViewById(R.id.start_tracking_button);
        phoneNumberEt = (EditText)findViewById(R.id.phone_number_et);
        startStopServiceBtn.setOnClickListener(this);
        this.context = getApplicationContext();
        sharedPref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Log.d(TAG, "onStart");
        if(!(sharedPref.getString("phoneActivity", "").isEmpty())){
            phoneNumberEt.setText(sharedPref.getString("phoneActivity", ""));

        }
        if (isMyServiceRunning(LocalService.class)){
            Log.d(TAG, "service is running called onStart   ");
            startStopServiceBtn.setText("Stop Tracking");
            Intent intent = new Intent(this, LocalService.class);
            bindService(intent, mConnection, 0);

        }
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LocalService.LocalBinder binder = (LocalService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        Log.d(TAG, "onSaveInstanceState");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        Log.d(TAG, "onStop");
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
            Log.d(TAG, "on Stop calling to unbindService ...");
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.start_tracking_button:
                {
                    if (isMyServiceRunning(LocalService.class)){
                        Log.d(TAG, "service is running user clicked button ");
                        intent = new Intent(this, LocalService.class);
                        stopService(intent);
                        startStopServiceBtn.setText("Start Tracking");
                    }
                else {
                        if(isPhoneNumberEmpty()){
                            context= getApplicationContext();
                            CharSequence text = "Please complete 10 digits phone number";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }else{
                            editor = sharedPref.edit();
                            editor.putString("phoneActivity", phoneNumberEt.getText().toString());
                            editor.commit();
                            startStopServiceBtn.setText("Stop Tracking");
                            Log.d(TAG, "Bind to LocalService and service is being created");
                            intent = new Intent(this, LocalService.class);
                            startService(intent);
                            bindService(intent, mConnection, 0);
                        }
                    }
                }
                break;
            default:
                break;
        }

    }

    boolean isPhoneNumberEmpty(){
        if(phoneNumberEt.getText().toString().length() == 0 | phoneNumberEt.getText().toString().length() <10){
            return true;
        }else{
            return false;
        }
    }


}
