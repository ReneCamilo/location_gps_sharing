package com.example.developer.locationsharinggps;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by developer on 7/31/17.
 */

public class SessionManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    private final String PREF_NAME = "gps_data";
    private final String phoneNumber = null ;

    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setPhoneNumber(String val) {
        editor.putString(phoneNumber, val);
        editor.commit();
    }
    public String getPhoneNumber() {
        return pref.getString(phoneNumber, "");
    }
}
