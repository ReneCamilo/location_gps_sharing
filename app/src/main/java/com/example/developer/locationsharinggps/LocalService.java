package com.example.developer.locationsharinggps;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.Random;

/**
 * Created by developer on 7/27/17.
 */

public class LocalService extends Service implements LocationListener {
    int mStartMode;       // indicates how to behave if the service is killed
    boolean mAllowRebind = true; // indicates whether onRebind should be used
    private boolean isServiceRunning;
    public String lat, lon;
    private LocationManager locationManager;
    private String provider;
    private String phoneNumber;

    private static String TAG = ".//LocalService";
    private final IBinder mBinder = new LocalBinder();
    // Random number generator
    private final Random mGenerator = new Random();

    @Override
    public void onLocationChanged(Location location) {
        lat = Double.toString(location.getLatitude());//int lat = (int) (location.getLatitude());
        lon = Double.toString(location.getLongitude());
        Log.i(TAG, "LAT: " + lat);
        Log.i(TAG, "LON: " + lon);

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


    public class LocalBinder extends Binder {
        LocalService getService() {
            // Return this instance of LocalService so clients can call public methods
            return LocalService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        isServiceRunning = true;
        Log.d(TAG, "onBind");

        return mBinder;
    }

    /** method for clients */
    public int getRandomNumber() {

        int random = mGenerator.nextInt(100);
        Log.d(TAG, "random service: " + random);
        return random;
    }




    @Override
    public void onCreate() {

        isServiceRunning = true;
        // The service is being created
        Log.d(TAG, "onCreate");
        new Thread(new Runnable() {
            @Override
            public void run() {

                while (isServiceRunning) {
                    try {
                        Thread.sleep(4000);
                        Log.d(TAG, "hello service: lat: "+lat+"   lon: "+lon+ "    phone:" +phoneNumber);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }


            }
        }).start();


    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // The service is starting, due to a call to startService()
        Log.d(TAG, "onStartCommand");
        this.phoneNumber =  MainActivity.phoneNumber;


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);

        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        } else {
            Log.i(TAG, "Location not available latitude");
            Log.i(TAG, "Location not available longitude");
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        locationManager.requestLocationUpdates(provider, 400, 1, this);
        return mStartMode;
    }



    @Override
    public boolean onUnbind(Intent intent) {
        // All clients have unbound with unbindService()
        Log.d(TAG, "onUnbind");

        return mAllowRebind;
    }


    @Override
    public void onRebind(Intent intent) {
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
        Log.d(TAG, "onRebind");

    }
    @Override
    public void onDestroy() {
        // The service is no longer used and is being destroyed
        Log.d(TAG, "onDestroy");
        isServiceRunning = false;

    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;

    }
}
