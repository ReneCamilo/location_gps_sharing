package com.example.developer.locationsharinggps;

import android.Manifest;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by Rene Contreras on 7/25/17.
 */

public class MainActivity extends AppCompatActivity implements LocationListener, View.OnClickListener {
    public String TAG = "./MainActivity";
    LocalService mService;
    public String lat, lon;
    boolean mBound = false;
    Button startStopServiceBtn;
    boolean startStopServiceIsPressed;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startStopServiceBtn = (Button) findViewById(R.id.start_tracking_button);
        startStopServiceBtn.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        if (isMyServiceRunning(LocalService.class)){
            Log.d(TAG, "service is running ");
            startStopServiceBtn.setText("Stop Tracking");
            Intent intent = new Intent(this, LocalService.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        }/*else{
            Log.d(TAG, "Bind to LocalService and service is being created");
            Intent intent = new Intent(this, LocalService.class);
            startService(intent);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        }*/

    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
            Log.d(TAG, "unbindService ...");
        }
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
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
    public void onLocationChanged(Location location) {
        lat = Double.toString(location.getLatitude());//int lat = (int) (location.getLatitude());
        lon = Double.toString(location.getLongitude());
        Log.i(TAG, "LAT: "+ lat);
        Log.i(TAG, "LON: "+ lon);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.start_tracking_button:
                {
                    /*if (isMyServiceRunning(LocalService.class)){
                        startStopServiceBtn.setText("Stop Tracking");
                        Log.d(TAG, "service is running ");
                        Intent intent = new Intent(this, LocalService.class);
                        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);*/
                    if (isMyServiceRunning(LocalService.class)){
                        Log.d(TAG, "service is running ");
                        stopService(new Intent(getApplicationContext(), LocalService.class));
                        startStopServiceBtn.setText("Start Tracking");
                    }


                else {
                        startStopServiceBtn.setText("Stop Tracking");
                        Log.d(TAG, "Bind to LocalService and service is being created");
                        Intent intent = new Intent(this, LocalService.class);
                        startService(intent);
                        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
                    }
                }
                break;
            default:
                break;
        }

    }
}
