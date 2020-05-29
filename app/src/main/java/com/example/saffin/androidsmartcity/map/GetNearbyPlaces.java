package com.example.saffin.androidsmartcity.map;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Th0ma on 25/05/2020
 */
public class GetNearbyPlaces extends AsyncTask<Object,String, String> {
    private String placesData, url, placeID;
    private GoogleMap mMap;
    private Context mContext;
    private PlacesClient mPlacesClient;
    private ArrayList<String> nearbyPlacesID;
    private ArrayList<String> nearbyPlacesName;
    private ArrayList<String> nearbyPlacesRating;
    private int nb_places = 0;
    private ArrayList<PlaceDetails> mPlaceDetailsList;
    private RecyclerView placeDetailsRecyclerView;
    private RecyclerView.Adapter placeDetailsAdapter;


    GetNearbyPlaces(MapsActivity context, PlacesClient placesClient, RecyclerView recyclerView){
        mContext = context;
        mPlacesClient = placesClient;
        mPlaceDetailsList = new ArrayList<>();
        placeDetailsRecyclerView = recyclerView;
        nearbyPlacesID = new ArrayList<>();
        nearbyPlacesName = new ArrayList<>();
        nearbyPlacesRating = new ArrayList<>();
        initRecyclerView();
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
        if(s.isEmpty()){
            System.err.println("ERROR didn't receive data");
        }
        nearbyPlacesList = dataParser.parse(s);

        displayNearbyPlaces(nearbyPlacesList);
    }

    private void displayNearbyPlaces(List<HashMap<String,String>> nearbyPlacesList){
        String placeName = "";
        String rating = "";
        for(int i = 0; i < nearbyPlacesList.size();i++){
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String,String> googleNearbyPlaces = nearbyPlacesList.get(i);
            placeName = googleNearbyPlaces.get("place_name");
            //String vicinity = googleNearbyPlaces.get("vicinity");
            LatLng coordinates = new LatLng(Double.parseDouble(googleNearbyPlaces.get("Lat")), Double.parseDouble(googleNearbyPlaces.get("Lng")));
            placeID = googleNearbyPlaces.get("reference");
            rating = googleNearbyPlaces.get("rating");
            //String isOpen = googleNearbyPlaces.get("isOpen");
            //String nb_votes = googleNearbyPlaces.get("nb_votes");

            nearbyPlacesID.add(placeID);
            nearbyPlacesName.add(placeName);
            nearbyPlacesRating.add(rating);

            markerOptions.position(coordinates);
            markerOptions.title(placeName);
            markerOptions.snippet("note: " + rating);

            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(coordinates));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        }

        fetchAllPlacesDetails();

        System.out.println("Number of nearby places : " + nearbyPlacesList.size());
        System.out.println("Number of fetched places : " + mPlaceDetailsList.size());
    }

    public void fetchAllPlacesDetails(){

        mPlaceDetailsList.removeAll(mPlaceDetailsList);
        for(int i = 0; i < nearbyPlacesID.size();i++){
            fetchPlaceDetails(nearbyPlacesID.get(i),nearbyPlacesName.get(i),nearbyPlacesRating.get(i));
        }
    }

    private void fetchPlaceDetails(final String id, final String name, final String rating){

        List<Place.Field> fields = new ArrayList<>();

        fields.add(Place.Field.ADDRESS);
        fields.add(Place.Field.OPENING_HOURS);
        fields.add(Place.Field.WEBSITE_URI);
        fields.add(Place.Field.PHONE_NUMBER);
        fields.add(Place.Field.PHOTO_METADATAS);

        mPlacesClient.fetchPlace(FetchPlaceRequest.builder(id,fields).build()).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
            @Override
            public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                Place placeDetailed = fetchPlaceResponse.getPlace();

                String adresse = (placeDetailed.getAddress() != null) ? placeDetailed.getAddress() : "Pas d'adresse";
                String openingHours = (placeDetailed.getOpeningHours() != null) ? parseOpeningHours(placeDetailed.getOpeningHours().getWeekdayText().toString()): "Pas d'horaires";
                String website = (placeDetailed.getWebsiteUri() != null) ? placeDetailed.getWebsiteUri().toString() : "Pas de site web";
                String phone = (placeDetailed.getPhoneNumber() != null) ? placeDetailed.getPhoneNumber() : "Pas de numéro de téléphone";
                String photo = (placeDetailed.getPhotoMetadatas() != null) ? placeDetailed.getPhotoMetadatas().toString() : "pas de photo";

                PlaceDetails placeDetails =
                        new PlaceDetails(id
                                , name
                                , adresse
                                , openingHours
                                , rating
                                , website
                                , phone
                                , photo);

                System.out.println(placeDetails.toString());

                mPlaceDetailsList.add(placeDetails);
                nb_places++;
                if(nb_places == nearbyPlacesID.size()){
                    System.out.println("All Places Are Fully Gathered");
                    updateRecyclerView();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

    private String parseOpeningHours(String openingHours){
        int indexLundi = openingHours.indexOf("mardi") - 2;
        int indexMardi = openingHours.indexOf("mercredi") - 2;
        int indexMercredi = openingHours.indexOf("jeudi") - 2;
        int indexJeudi = openingHours.indexOf("vendredi") - 2;
        int indexVendredi = openingHours.indexOf("samedi") - 2;
        int indexSamedi = openingHours.indexOf("dimanche") - 2;
        int indexDimanche = openingHours.length() - 1;

        String lundi;
        if(openingHours.contains("lundi")){
            lundi = openingHours.substring(1,indexLundi);
        }
        else{
            lundi = "Fermé";
        }
        System.out.println(lundi);

        String mardi;
        if(openingHours.contains("mardi")){
            mardi = openingHours.substring(indexLundi+2,indexMardi);
        }
        else{
            mardi = "Fermé";
        }
        System.out.println(mardi);

        String mercredi;
        if(openingHours.contains("mercredi")){
            mercredi = openingHours.substring(indexMardi+2,indexMercredi);
        }
        else{
            mercredi = "Fermé";
        }
        System.out.println(mercredi);

        String jeudi;
        if(openingHours.contains("jeudi")){
            jeudi = openingHours.substring(indexMercredi+2,indexJeudi);
        }
        else{
            jeudi = "Fermé";
        }
        System.out.println(jeudi);

        String vendredi;
        if(openingHours.contains("vendredi")){
            vendredi = openingHours.substring(indexJeudi+2,indexVendredi);
        }
        else{
            vendredi = "Fermé";
        }
        System.out.println(vendredi);

        String samedi;
        if(openingHours.contains("samedi")){
            samedi = openingHours.substring(indexVendredi+2,indexSamedi);
        }
        else {
            samedi = "Fermé";
        }
        System.out.println(samedi);

        String dimanche;
        if(openingHours.contains("dimanche")){
            dimanche = openingHours.substring(indexSamedi+2,indexDimanche);
        }
        else{
            dimanche = "Fermé";
        }
        System.out.println(dimanche);

        return "\t" + lundi + "\n\t" + mardi + "\n\t" + mercredi + "\n\t" + jeudi + "\n\t" + vendredi + "\n\t" + samedi + "\n\t" + dimanche;
    }

    private void initRecyclerView(){
        // use a linear layout manager
        RecyclerView.LayoutManager placeDetailsLayoutManager = new LinearLayoutManager(mContext);
        placeDetailsRecyclerView.setLayoutManager(placeDetailsLayoutManager);

        // specify an adapter (see also next example)
        placeDetailsAdapter= new PlaceDetailsAdapter(mPlaceDetailsList);
        placeDetailsRecyclerView.setAdapter(placeDetailsAdapter);
    }

    private void updateRecyclerView(){
        placeDetailsRecyclerView.removeAllViews();
        placeDetailsAdapter.notifyItemRangeRemoved(0,placeDetailsAdapter.getItemCount());
        placeDetailsAdapter.notifyDataSetChanged();
    }
}
