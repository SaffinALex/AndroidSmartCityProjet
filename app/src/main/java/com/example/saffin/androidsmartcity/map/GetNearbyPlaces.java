package com.example.saffin.androidsmartcity.map;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.saffin.androidsmartcity.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Th0ma on 25/05/2020
 */
public class GetNearbyPlaces extends AsyncTask<Object,String, String> {
    private String placesData, url;
    private GoogleMap mMap;
    private MapsActivity mMapsActivity;

    GetNearbyPlaces(MapsActivity mapsActivity){
        mMapsActivity = mapsActivity;
    }

    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        url = (String) objects[1];

        DownloadURL downloadURL = new DownloadURL();

        try {
            placesData = downloadURL.URL_Reader(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return placesData;
    }

    @Override
    protected void onPostExecute(String s) {
        List<HashMap<String,String>> nearbyPlacesList = null;
        DataParser dataParser = new DataParser();
        nearbyPlacesList = dataParser.parse(s);

        displayNearbyPlaces(nearbyPlacesList);
    }

    private void displayNearbyPlaces(List<HashMap<String,String>> nearbyPlacesList){
        for(int i = 0; i < nearbyPlacesList.size();i++){
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String,String> googleNearbyPlaces = nearbyPlacesList.get(i);
            String placeName = googleNearbyPlaces.get("place_name");
            String vicinity = googleNearbyPlaces.get("vicinity");
            LatLng coordinates = new LatLng(Double.parseDouble(googleNearbyPlaces.get("Lat")), Double.parseDouble(googleNearbyPlaces.get("Lng")));
            String reference = googleNearbyPlaces.get("reference");
            String rating = googleNearbyPlaces.get("rating");
            String isOpen = googleNearbyPlaces.get("isOpen");
            String nb_votes = googleNearbyPlaces.get("nb_votes");


            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    LinearLayout info = new LinearLayout(mMapsActivity);
                    info.setOrientation(LinearLayout.VERTICAL);

                    TextView title = new TextView(mMapsActivity);
                    title.setTextColor(Color.BLACK);
                    title.setGravity(Gravity.CENTER);
                    title.setTypeface(null, Typeface.BOLD);
                    title.setText(marker.getTitle());

                    TextView snippet = new TextView(mMapsActivity);
                    snippet.setTextColor(Color.GRAY);
                    snippet.setText(marker.getSnippet());

                    Button favorite = new Button(mMapsActivity);
                    favorite.setText(R.string.add_favorite);

                    favorite.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            System.out.println("The button is working");
                        }
                    });

                    info.addView(title);
                    info.addView(snippet);
                    info.addView(favorite);

                    return info;
                }
            });

            markerOptions.position(coordinates);
            markerOptions.title(placeName + " : " + vicinity);
            if(isOpen != null){
                markerOptions.snippet("This place is " + isOpen
                        + "\nrating: " + rating
                        + "\nnumber of votes: " + nb_votes
                        + "\nplace_id: " + reference);
            }
            else{
                markerOptions.snippet("rating: " + rating
                        + "\nnumber of votes: " + nb_votes
                        + "\nplace_id: " + reference);
            }


            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(coordinates));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        }

    }
}
