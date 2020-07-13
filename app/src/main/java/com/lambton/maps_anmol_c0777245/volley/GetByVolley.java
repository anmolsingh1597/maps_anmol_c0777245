package com.lambton.maps_anmol_c0777245.volley;

import com.google.android.gms.maps.GoogleMap;
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
}
