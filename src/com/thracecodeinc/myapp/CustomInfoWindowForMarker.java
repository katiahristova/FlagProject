package com.thracecodeinc.myapp;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.wearable.Asset;
import com.thracecodeinc.guessTheFlag.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by katiahristova on 4/15/15.
 */
    class CustomInfoWindowForMarker implements GoogleMap.InfoWindowAdapter {
        private final View markerView;
        public static Map<String, Integer> populationMap;

    CustomInfoWindowForMarker() {
        this.markerView = null;
        populationMap = new HashMap<String, Integer>();
    }

    CustomInfoWindowForMarker(Activity a, String country, Drawable flag_old, String countryName) {

            markerView = a.getLayoutInflater()
                    .inflate(R.layout.custom_marker_layout, null);

            Bitmap bitmap = ((BitmapDrawable) flag_old).getBitmap();

            final ImageView image = ((ImageView) markerView.findViewById(R.id.badge));
            image.setImageBitmap(bitmap);

            final TextView titleUi = ((TextView) markerView.findViewById(R.id.title));
            titleUi.setText(country);

            final TextView snippetUi = ((TextView) markerView
                    .findViewById(R.id.snippet));

            //String countryName = filenameOld.substring(filenameOld.indexOf("-")+1, filenameOld.indexOf("."));

            int pop = 0;
            if (populationMap.containsKey(countryName))
                pop = populationMap.get(countryName);

            Log.d("Country info", "Country: " + countryName + " Population: " + pop);

            if (pop!=0)
                snippetUi.setText("Population: " + NumberFormat.getNumberInstance(Locale.US).format(pop));
        }




    public View getInfoWindow(Marker marker) {
            render(marker, markerView);
            return markerView;
        }

        public View getInfoContents(Marker marker) {
            return null;
        }

        private void render(Marker marker, View view) {
            // Add the code to set the required values
            // for each element in your custominfowindow layout file
        }

    public void getPopulations(AssetManager assetManager)
    {
        InputStream is = null;
        try {

            is = assetManager.open("countriesData.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null)
            {
                String[] RowData = line.split(",");
                String name = RowData[0];
                String capital = RowData[1];
                String population = RowData[2];
                String territory = RowData[3];
                populationMap.put(name, Integer.valueOf(population));
                //Log.d("Reading Info", "name: " + name + " population: " + population);
            }

        }
        catch (IOException ex) {
            // handle exception
        }
        finally {
            try {
                is.close();
            }
            catch (IOException e) {
                // handle exception
            }
        }

    }
}
