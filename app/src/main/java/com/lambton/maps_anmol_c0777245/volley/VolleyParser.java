package com.lambton.maps_anmol_c0777245.volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class VolleyParser {

    /**
     * status stauts is value is available or not
     * @param jsonObject
     * @return
     */

    public boolean statusCheck(JSONObject jsonObject){
        String statucheck = "";
        boolean status = true;
        try{
            statucheck = jsonObject.getString("status");
            if(statucheck.equals("ZERO_RESULTS")){
                status = false;
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        return status;
    }

    /**
     * parse method to parse the json data retrieved from direction api
     * @param jsonObject
     * @return a dictionary of distance and duration
     */
    public HashMap<String, String> parseDistance(JSONObject jsonObject){
        JSONArray jsonArray = null;
            try {
                jsonArray = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");
            } catch (JSONException e) {
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


}
