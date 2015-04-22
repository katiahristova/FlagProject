package com.thracecodeinc.guessTheFlag;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Created by katiahristova on 4/17/15.
 */
public class startPage extends Activity{
    HashMap<String, Boolean> regionsMap;
    Button playButton;
    static Button playOfflineButton;
    Button selectRegionsButton;
    int guessRows = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity_layout);

        //Flag for LittleHands permissions
        boolean networkAllowed = true;

        //Check if data and wifi are on
        boolean dataOn = dataState();
        boolean wifiOn = wifiState();

        regionsMap = new HashMap<String, Boolean>();
        String[] regionNames =
                getResources().getStringArray(R.array.regionsList);
        for (String region : regionNames)
            regionsMap.put(region, true);


        final boolean online= (dataOn || wifiOn) && networkAllowed;

        playButton = (Button) findViewById(R.id.buttonPlay);
        playOfflineButton = (Button) findViewById(R.id.buttonPlayOffline);
        selectRegionsButton = (Button) findViewById(R.id.buttonSelectRegions);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (online) {
                    Intent i = new Intent(getApplicationContext(), MyActivity.class);
                    showSelectNumberOfChoicesPopup(i);
                }
                else
                {
                    if (!SharedMethods.isOnline(getApplicationContext()))
                        SharedMethods.networkModePopup(startPage.this);
                }
            }
        });

        playOfflineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), OfflineGameClass.class);
                i.putExtra("startedByUser",true);
                showSelectNumberOfChoicesPopup(i);
            }
        });

        selectRegionsButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                final String[] regionNames =
                        regionsMap.keySet().toArray(new String[regionsMap.size()]);

                boolean[] regionsEnabled = new boolean[regionsMap.size()];
                for (int i = 0; i < regionsEnabled.length; ++i)
                    regionsEnabled[i] = regionsMap.get(regionNames[i]);
                AlertDialog.Builder regionsBuilder =
                        new AlertDialog.Builder(startPage.this);
                regionsBuilder.setTitle(R.string.regions);

                String[] displayNames = new String[regionNames.length];
                for (int i = 0; i < regionNames.length; ++i)
                    displayNames[i] = regionNames[i].replace('_', ' ');


                regionsBuilder.setMultiChoiceItems(
                        displayNames, regionsEnabled,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                regionsMap.put(
                                        regionNames[which].toString(), isChecked);
                            }
                        }
                );

                regionsBuilder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int button) {
                                //go to the appropriate activity
                            }
                        });

                regionsBuilder.setNegativeButton("cancel",

                              new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int button) {

                                    }
                                }
                );
                AlertDialog regionsDialog = regionsBuilder.create();
                regionsDialog.show();
            }
        });
    }

    public void showSelectNumberOfChoicesPopup(final Intent i)
    {

        final String[] possibleChoices =
                getResources().getStringArray(R.array.guessesList);

        AlertDialog.Builder choicesBuilder =
                new AlertDialog.Builder(startPage.this);
        choicesBuilder.setTitle(R.string.choices);

        choicesBuilder.setItems(R.array.guessesList,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        guessRows = Integer.parseInt(
                                possibleChoices[item].toString()) / 2;
                        i.putExtra("guessRows", guessRows);
                        i.putExtra("regionsMap", regionsMap);
                        startActivity(i);
                        finish();
                    }
                }
        );
        AlertDialog choicesDialog = choicesBuilder.create();
        choicesDialog.show();
    }

    //Get the DATA state
    public boolean dataState() {
        boolean mobileDataEnabled = false; // Assume disabled
        if (SharedMethods.isOnline(getApplicationContext())) {
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



    //Get the wifi state
    public boolean wifiState() {
        if (SharedMethods.isOnline(getApplicationContext())){
            WifiManager mng = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            if (mng.isWifiEnabled())
                return true;
        }
        return false;
    }

}
