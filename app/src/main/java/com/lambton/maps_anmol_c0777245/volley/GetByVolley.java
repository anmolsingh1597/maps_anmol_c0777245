package com.lambton.maps_anmol_c0777245.volley;

import android.graphics.Color;
import android.location.Location;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import org.json.JSONObject;

import java.util.HashMap;

public class GetByVolley {
    public static String getTotalDistance(JSONObject jsonObject, GoogleMap googleMap){
        HashMap<String, String> distances = null;
        boolean status;
        VolleyParser directionParser = new VolleyParser();
        status = directionParser.statusCheck(jsonObject);
        String distance = "";
        if(!status){
            distance = "errorValue";
        }else {
        distances = directionParser.parseDistance(jsonObject);
        distance = distances.get("distance");
        }
        return distance;
    }



    public static void getDirection(JSONObject jsonObject, GoogleMap googleMap, Location location){
        HashMap<String, String> distances = null;
        VolleyParser directionParser = new VolleyParser();
        distances = directionParser.parseDistance(jsonObject);

        String distance = distances.get("distance");
        String duration = distances.get("duration");

        String[] directionsList;
        directionsList = directionParser.parseDirection(jsonObject);
        displayDirection(directionsList, distance, duration, googleMap, location);
    }

    private static void displayDirection(String[] directionsList, String distance, String duration,  GoogleMap googleMap, Location location) {
        googleMap.clear();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions options = new MarkerOptions().position(latLng)
                .draggable(true);
        googleMap.addMarker(options);
        for (String s : directionsList) {
            PolylineOptions polygonOptions = new PolylineOptions()
                    .color(Color.RED)
                    .width(18)
                    .addAll(PolyUtil.decode(s));
            googleMap.addPolyline(polygonOptions);
        }
    }
/*
    public static void nearByPlaces(JSONObject jsonObject, GoogleMap googleMap){
        List<HashMap<String,String>> nearbyPlaces = null;
        VolleyParser dataParser = new VolleyParser();
        nearbyPlaces = dataParser.parsePlace(jsonObject);
        showNearbyPlaces(nearbyPlaces, googleMap);

    }

    private static void showNearbyPlaces(List<HashMap<String, String>> nearbyPlaces,  GoogleMap googleMap) {
        googleMap.clear();
        for(int i=0; i<nearbyPlaces.size(); i++){
            HashMap<String,String> googlePlace = nearbyPlaces.get(i);
            String placeName = googlePlace.get("placeName");
            String vicinity = googlePlace.get("vicinity");
            String refernce = googlePlace.get("reference");
            double lat = Double.parseDouble(googlePlace.get("latitiude"));
            double lng = Double.parseDouble(googlePlace.get("longitude"));

            LatLng latLng = new LatLng(lat, lng);

            MarkerOptions options = new MarkerOptions().position(latLng)
                    .title(placeName + " : " + vicinity)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            googleMap.addMarker(options);
        }
    }*/
}
