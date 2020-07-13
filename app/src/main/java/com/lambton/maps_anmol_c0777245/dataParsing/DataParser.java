package com.lambton.maps_anmol_c0777245.dataParsing;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParser {
    /**
     * parse method to parse the json data retrieved from direction api
     * @param jsonData
     * @return a dictionary of distance and duration
     */
    public HashMap<String, String> parseDistance(String jsonData){
        JSONArray jsonArray = null;
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");
        }catch (JSONException e){
            e.printStackTrace();
        }
        return getDuration(jsonArray);

    }

    private HashMap<String, String> getDuration(JSONArray jsonArray) {
        HashMap<String, String> distanceDurationDict = new HashMap<>();
        String distance = "";
        String duration = "";

        try {
            duration = jsonArray.getJSONObject(0).getJSONObject("duration").getString("text");
            distance = jsonArray.getJSONObject(0).getJSONObject("distance").getString("text");
            distanceDurationDict.put("duration", duration);
            distanceDurationDict.put("distance", distance);
        }catch (JSONException e){
            e.printStackTrace();
        }

        return distanceDurationDict;
    }

    public  String[] parseDirection (String jsonData){
        JSONArray jsonArray = null;
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs")
                        .getJSONObject(0).getJSONArray("steps");
        }catch (JSONException e){
            e.printStackTrace();
        }

        return getPaths(jsonArray);
    }

    private String[] getPaths(JSONArray jsonArray) {
        int count = jsonArray.length();
        String[] polylinesPoints = new String[count];

        for(int i=0; i<count; i++){
            try {
                polylinesPoints[i] = getPath(jsonArray.getJSONObject(i));
            }catch (JSONException e){
                e.printStackTrace();
            }

        }
        return polylinesPoints;
    }

    private String getPath(JSONObject jsonObject) {
        String polyLinePoint = "";
       try {
           polyLinePoint = jsonObject.getJSONObject("polyline").getString("points");
       }catch (JSONException e){
           e.printStackTrace();
       }

       return polyLinePoint;
    }
}
