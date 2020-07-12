package com.lambton.maps_anmol_c0777245.volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VolleyParser {

    /**
     * parse method to parse the json data retrieved from direction api
     * @param jsonObject
     * @return a dictionary of distance and duration
     */
    public HashMap<String, String> parseDistance(JSONObject jsonObject){
        JSONArray jsonArray = null;
        try {
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


    public  String[] parseDirection (JSONObject jsonObject){
        JSONArray jsonArray = null;
        try {

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

    /**
     * parse method for getting nearby places from place api
     * @param jsonObject
     * @return
     */

    public List<HashMap<String, String>> parsePlace(JSONObject jsonObject) {
        JSONArray jsonArray = null;
        try {

            jsonArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getPlaces(jsonArray);
    }

    private List<HashMap<String, String>> getPlaces(JSONArray jsonArray) {
        int count = jsonArray.length();
        List<HashMap<String, String>> placeList = new ArrayList<>();
        HashMap<String, String> placeDictionary = null;
        for (int i=0; i<count; i++){
            try{
                placeDictionary = getPlace((JSONObject)jsonArray.get(i));
                placeList.add(placeDictionary);
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }

        return placeList;
    }

    private HashMap<String, String> getPlace(JSONObject jsonObject) {
        HashMap<String,String> googlePlaceDictionary = new HashMap<>();
        String placeName = "-NA-";
        String vicinity = "-NA-";
        String latitude = "";
        String longitude = "";
        String reference = "";
        try {
            if(!jsonObject.isNull("name")){
                placeName = jsonObject.getString("name");
            }
            if(!jsonObject.isNull("vicinity")){
                vicinity = jsonObject.getString("vicinity");
            }
            if(!jsonObject.isNull("reference")){
                reference = jsonObject.getString("reference");
            }
            latitude = jsonObject.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = jsonObject.getJSONObject("geometry").getJSONObject("location").getString("lng");

            googlePlaceDictionary.put("placeName",placeName);
            googlePlaceDictionary.put("vicinity",vicinity);
            googlePlaceDictionary.put("latitiude",latitude);
            googlePlaceDictionary.put("longitude",longitude);
            googlePlaceDictionary.put("reference",reference);
        }catch (JSONException e){
            e.printStackTrace();
        }

        return googlePlaceDictionary;

    }

}
