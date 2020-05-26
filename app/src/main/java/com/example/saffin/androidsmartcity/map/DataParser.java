package map;

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
        String reference = "";
        try{
            if(!googlePlaceJSON.isNull("name")){
                placeName = googlePlaceJSON.getString("name");
            }
            if(!googlePlaceJSON.isNull("vicinity")){
                vicinity = googlePlaceJSON.getString("vicinity");
            }
            latitude = googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("latitude");
            longitude = googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("longitude");
            reference = googlePlaceJSON.getString("reference");

            googlePlaceMap.put("place_name",placeName);
            googlePlaceMap.put("vicinity",vicinity);
            googlePlaceMap.put("Lat",latitude);
            googlePlaceMap.put("Lng",longitude);
            googlePlaceMap.put("reference",reference);

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
