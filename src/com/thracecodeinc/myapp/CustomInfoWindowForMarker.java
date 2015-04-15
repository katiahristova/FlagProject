package com.thracecodeinc.myapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.thracecodeinc.guessTheFlag.R;

import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by katiahristova on 4/15/15.
 */
    class CustomInfoWindowForMarker implements GoogleMap.InfoWindowAdapter {
        private final View markerView;

        CustomInfoWindowForMarker(Activity a, String country, Integer population, String filenameOld) {

            markerView = a.getLayoutInflater()
                    .inflate(R.layout.custom_marker_layout, null);

            Drawable flag_old = null;
            InputStream stream;
            try {
                Log.d("Opening file", filenameOld);
                stream = a.getAssets().open(filenameOld);

                flag_old = Drawable.createFromStream(stream, country);
            } catch (IOException e) {
                //Log.e(TAG, "Error loading " + nextImageName, e);
            }

            Bitmap bitmap = ((BitmapDrawable) flag_old).getBitmap();

            final ImageView image = ((ImageView) markerView.findViewById(R.id.badge));
            image.setImageBitmap(bitmap);

            final TextView titleUi = ((TextView) markerView.findViewById(R.id.title));
            titleUi.setText(country);

            final TextView snippetUi = ((TextView) markerView
                    .findViewById(R.id.snippet));
            snippetUi.setText("Population: " + NumberFormat.getNumberInstance(Locale.US).format(population));
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
}
