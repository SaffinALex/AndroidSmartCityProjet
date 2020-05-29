package com.example.saffin.androidsmartcity.map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Th0ma on 25/05/2020
 */
public class DataParser {

    private HashMap<String, String> getNearbyPlace(JSONObject googlePlaceJSON)
    {
        HashMap<String,String> googlePlaceMap = new HashMap<>();
        String placeName = "-NA-";
        String vicinity = "-NA-";
        String latitude = "";
        String longitude = "";
        String rating = "";
        String nb_votes = "";
        String reference = "";
        try{
            if(!googlePlaceJSON.isNull("name")){
                placeName = googlePlaceJSON.getString("name");
            }
            if(!googlePlaceJSON.isNull("vicinity")){
                vicinity = googlePlaceJSON.getString("vicinity");
            }
            latitude = googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lng");
            rating = Double.toString(googlePlaceJSON.getDouble("rating"));
            nb_votes = Integer.toString(googlePlaceJSON.getInt("user_ratings_total"));
            reference = googlePlaceJSON.getString("reference");

            googlePlaceMap.put("place_name",placeName);
            googlePlaceMap.put("vicinity",vicinity);
            googlePlaceMap.put("Lat",latitude);
            googlePlaceMap.put("Lng",longitude);
            googlePlaceMap.put("rating",rating);
            googlePlaceMap.put("nb_votes",nb_votes);
            googlePlaceMap.put("reference",reference);

            if(!googlePlaceJSON.isNull("opening_hours")){
                if(googlePlaceJSON.getJSONObject("opening_hours").getBoolean("open_now")){
                    googlePlaceMap.put("isOpen","open");
                }
                else{
                    googlePlaceMap.put("isOpen","closed");
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return googlePlaceMap;
    }

    private List<HashMap<String, String>> getAllNearbyPlaces(JSONArray jsonArray){

        int counter = jsonArray.length();

        List<HashMap<String, String>> places_list = new ArrayList<>();

        HashMap<String,String> NearbyPlaceMap = null;

        for(int i = 0; i < counter ; i++){
            try {
                NearbyPlaceMap = getNearbyPlace((JSONObject) jsonArray.get(i));
                places_list.add(NearbyPlaceMap);

            }catch (JSONException e){
                e.printStackTrace();
            }

        }

        return places_list;
    }

    public  HashMap<String, String> getPlaceDetail(JSONObject googlePlaceJSON){
        HashMap<String,String> placeDetails = new HashMap<>();

        String imageURL = "";
        String adresse = "";
        String horaires = "";
        String siteWeb = "";



        return null;
    }

    public List<HashMap<String,String>> parse(String JSONData){
        JSONArray jsonArray = null;
        JSONObject jsonObject;


        try {
            jsonObject = new JSONObject(JSONData);
            jsonArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getAllNearbyPlaces(jsonArray);

    }
}
