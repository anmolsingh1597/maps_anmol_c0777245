package com.lambton.maps_anmol_c0777245.dataParsing;

import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.lambton.maps_anmol_c0777245.volley.FetchURL;

import java.io.IOException;
import java.util.HashMap;

public class GetDirectionsData extends AsyncTask<Object, Void, String> {

    String googleDirectionData;
    GoogleMap mMap;
    String url;
    LatLng latLng;

    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        url = (String) objects[1];
        latLng = (LatLng) objects[2];

        FetchURL fetchURL = new FetchURL();
        try {
            googleDirectionData = fetchURL.readURL(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googleDirectionData;
    }

    @Override
    protected void onPostExecute(String s) {
        HashMap<String, String> distances = null;
        DataParser directionParser = new DataParser();
        distances = directionParser.parseDistance(s);

        String distance = distances.get("distance");
        String duration = distances.get("duration");

        String[] directionsList;
        directionsList = directionParser.parseDirection(s);
        displayDirection(directionsList, distance, duration);
    }

    private void displayDirection(String[] directionsList, String distance, String duration) {
        MarkerOptions options = new MarkerOptions().position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .draggable(true);
        mMap.addMarker(options);
        for(int i=0; i<directionsList.length; i++){
            PolylineOptions polygonOptions = new PolylineOptions()
                    .color(Color.RED)
                    .width(5)
                    .addAll(PolyUtil.decode(directionsList[i]));
            mMap.addPolyline(polygonOptions);
        }
    }
}
