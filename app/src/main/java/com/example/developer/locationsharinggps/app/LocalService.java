package com.example.developer.locationsharinggps.app;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.developer.locationsharinggps.models.Coordinate;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.Random;

/**
 * Created by developer on 7/27/17.
 */

public class LocalService extends Service implements LocationListener {
    int mStartMode;       // indicates how to behave if the service is killed
    boolean mAllowRebind = true; // indicates whether onRebind should be used
    private boolean isServiceRunning;
    private String latitude, longitud;
    private LocationManager locationManager;
    private String provider;
    private String phoneNumber ="";
    private SharedPreferences preferences;
    private final String PREF_NAME = "gps_data";
    int PRIVATE_MODE = 0;
    private final String PHONE_NUMBER = "phoneActivity";
    private Coordinate coordinate;
    private static String TAG = "./LocalService";
    private final IBinder mBinder;
    //ksoap
    private final String NAMESPACE = "http://carriers.uslfreight.com";
    private final String URL = "http://carriers.uslfreight.com/ws/index.asmx?op=ReportLocation";  //"http://www.w3schools.com/webservices/tempconvert.asmx";
    private final String SOAP_ACTION = "http://carriers.uslfreight.com/ReportLocation";
    private final String METHOD_NAME = "ReportLocation";
    private static String celcius ="100";
    private static String fahren;
    Activity activity;
    Context context ;

    public LocalService() {
        this.coordinate = new Coordinate("","","");
        mBinder = new LocalBinder();
    }

    @Override
    public void onLocationChanged(Location location) {
        coordinate.setLatitude(Double.toString(location.getLatitude()));
        coordinate.setLongitude(Double.toString(location.getLongitude()));
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

    @Override
    public void onCreate() {
        preferences =  getApplicationContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        coordinate.setPhoneNumber(preferences.getString(PHONE_NUMBER, ""));
        this.activity = activity;
        isServiceRunning = true;
        // The service is being created
        Log.d(TAG, "onCreate");
        //Create instance for AsyncCallWS
         AsyncCallWS task = new AsyncCallWS();
        //Call execute
        task.execute();

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (isServiceRunning) {
                    try {
                        Thread.sleep(5000);
                        Log.d(TAG, "hello service: lat: "+coordinate.getLatitude()+"   lon: "+coordinate.getLongitude()+ "    phone:" +coordinate.getPhoneNumber());
                        AsyncCallWS task = new AsyncCallWS();
                        //Call execute
                        task.execute();
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
        initLocationSettings();
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
        // A client is binding to the service with bindService(), after onUnbind() has already been called
        Log.d(TAG, "onRebind");
    }

    @Override
    public void onDestroy() {
        // The service is no longer used and is being destroyed
        Log.d(TAG, "onDestroy");
        isServiceRunning = false;

    }


    public void initLocationSettings(){
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);

        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        } else {
            Log.i(TAG, "Location not available latitude and longitude");
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "permission denied");
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;

        }else {
            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    0);

        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;

        }
        locationManager.requestLocationUpdates(provider, 400, 1, this);

    }

    public void getFahrenheit(String celsius) {
        //Create request
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        //Property which holds input parameters
        PropertyInfo celsiusPI = new PropertyInfo();
        //Set Name
        celsiusPI.setName("Phone");
        //Set Value
        celsiusPI.setValue(coordinate.getPhoneNumber());
        //Set dataType
        celsiusPI.setType(String.class);
        request.addProperty(celsiusPI);

        //Add the property to request object
        PropertyInfo latitude = new PropertyInfo();
        latitude.setName("Latitude");
        //Set Value
        latitude.setValue(coordinate.getLatitude());
        //Set dataType
        latitude.setType(String.class);
        request.addProperty(latitude);

        //Add the property to request object
        PropertyInfo longitude = new PropertyInfo();
        longitude.setName("Longitude");
        //Set Value
        longitude.setValue(coordinate.getLongitude());
        //Set dataType
        longitude.setType(String.class);
        //Add the property to request object
        request.addProperty(longitude);




        //Create envelope
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;
        //Set output SOAP object
        envelope.setOutputSoapObject(request);
        //Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

        try {
            //Invole web service
            androidHttpTransport.call(SOAP_ACTION, envelope);
            //Get the response
            SoapObject response = (SoapObject) envelope.getResponse();
            //Assign it to fahren static variable
            fahren = response.toString();
            Log.i(TAG, "response: "+fahren);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public class AsyncCallWS extends AsyncTask<String, Void, Void> {
    @Override
    protected Void doInBackground(String... params) {
        Log.i(TAG, "doInBackground");
        getFahrenheit(celcius);
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        Log.i(TAG, "onPostExecute");
        Log.d(TAG, "° F"+fahren);
    }

    @Override
    protected void onPreExecute() {
        Log.i(TAG, "onPreExecute");
        Log.d(TAG, "Calculating...");
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        Log.i(TAG, "onProgressUpdate");
    }

    }
}
