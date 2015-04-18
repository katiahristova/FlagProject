package com.thracecodeinc.guessTheFlag;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.Toast;

import java.lang.reflect.Method;

/**
 * Created by katiahristova on 4/17/15.
 */
public class startPage extends Activity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity_layout);

        //Flag for LittleHands permissions
        boolean networkAllowed = true;

        //Check if data and wifi are on
        boolean dataOn = dataState();
        boolean wifiOn = wifiState();

        boolean online= (dataOn || wifiOn) && networkAllowed;

        if (online)
        {
            Intent i = new Intent(this, MyActivity.class);
            startActivity(i);
            finish();
        }

        else
        {
            Toast.makeText(this,"No Network",Toast.LENGTH_SHORT).show();
        }

    }

    //Get the DATA state
    public boolean dataState() {
        boolean mobileDataEnabled = false; // Assume disabled
        if (isOnline()) {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            try {
                Class cmClass = Class.forName(cm.getClass().getName());
                Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
                method.setAccessible(true);
                // Make the method callable
                // get the setting for "mobile data"

                mobileDataEnabled = (Boolean) method.invoke(cm);
            } catch (Exception e) { // Some problem accessible private API // TODO do wh
            }
        }
        return mobileDataEnabled;
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    //Get the wifi state
    public boolean wifiState() {
        if (isOnline()){
            WifiManager mng = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            if (mng.isWifiEnabled())
                return true;
        }
        return false;
    }

}
