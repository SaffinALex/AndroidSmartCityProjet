package com.example.saffin.androidsmartcity;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.saffin.androidsmartcity.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * Created by Th0ma on 25/05/2020
 */
public class CodeSamples extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener{

    private FirebaseFirestore database;
    private LatLng cityCoordinates = null;
    private AutocompleteSupportFragment mAutocompleteFragment;
    private GoogleMap mMap;
    private int counter = 0;

    private void addMarkerOnMapClicked(){
        /** Allows to add Markers **/
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Marker mark = mMap.addMarker(new MarkerOptions().position(latLng));
                mark.setSnippet(latLng.toString());
                mark.setTitle("Marker_" + counter++);
                mark.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            }
        });
    }

    private void findMarkerLocation(){
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                LatLng latLng = marker.getPosition();
                PlacesClient mPlacesClient = Places.createClient(getApplicationContext());

                // Use fields to define the data types to return.
                List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS,
                        Place.Field.LAT_LNG);

                // Use the builder to create a FindCurrentPlaceRequest.
                FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);

                // Get the likely places - that is, the businesses and other points of interest that
                // are the best match for the device's current location.
                assert mPlacesClient != null;
                @SuppressWarnings("MissingPermission") final Task<FindCurrentPlaceResponse> placeResult =
                        mPlacesClient.findCurrentPlace(request);
                placeResult.addOnCompleteListener(new OnCompleteListener<FindCurrentPlaceResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<FindCurrentPlaceResponse> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            FindCurrentPlaceResponse likelyPlaces = task.getResult();

                            // Set the count, handling cases where less than 5 entries are returned.
                            int count;
                            if (likelyPlaces.getPlaceLikelihoods().size() < 5) {
                                count = likelyPlaces.getPlaceLikelihoods().size();
                            } else {
                                count = 5;
                            }

                            int i = 0;
                            String[] mLikelyPlaceNames = new String[count];
                            String[] mLikelyPlaceAddresses = new String[count];
                            List[] mLikelyPlaceAttributions = new List[count];
                            LatLng[] mLikelyPlaceLatLngs = new LatLng[count];

                            for (PlaceLikelihood placeLikelihood : likelyPlaces.getPlaceLikelihoods()) {
                                // Build a list of likely places to show the user.
                                mLikelyPlaceNames[i] = placeLikelihood.getPlace().getName();
                                mLikelyPlaceAddresses[i] = placeLikelihood.getPlace().getAddress();
                                mLikelyPlaceAttributions[i] = placeLikelihood.getPlace()
                                        .getAttributions();
                                mLikelyPlaceLatLngs[i] = placeLikelihood.getPlace().getLatLng();

                                i++;
                                if (i > (count - 1)) {
                                    break;
                                }
                            }

                            for (int j = 0; j < mLikelyPlaceNames.length; j++) {
                                System.err.println("PlaceName: " + mLikelyPlaceNames[j].toString());
                            }
                            for (int j = 0; j < mLikelyPlaceAddresses.length; j++) {
                                System.err.println("PlaceAdresse: " + mLikelyPlaceAddresses[j].toString());
                            }
                            for (int j = 0; j < mLikelyPlaceAttributions.length; j++) {
                                System.err.println("PlaceAttribution: " + mLikelyPlaceAttributions[j].toString());
                            }
                            for (int j = 0; j < mLikelyPlaceLatLngs.length; j++) {
                                System.err.println("PlaceCoordinates: " + mLikelyPlaceLatLngs[j].toString());
                            }

                            // Show a dialog offering the user the list of likely places, and add a
                            // marker at the selected place.
                            //instance.openPlacesDialog();
                        } else {
                            System.err.println("Exception: " + task.getException());
                        }
                    }
                });

                // Return false to indicate that we have not consumed the event and that we wish
                // for the default behavior to occur (which is for the camera to move such that the
                // marker is centered and for the marker's info window to open, if it has one).
                return false;
            }


        });
    }


    /*** *****************
     * Places AutoComplete
     * ******************* **/

    /***
     *  XML for AutocompleteFragment
     *
     *    <androidx.cardview.widget.CardView
     *             android:layout_width="wrap_content"
     *             android:layout_height="wrap_content"
     *             android:layout_alignBottom="@id/map_frame_layout"
     *             android:layout_marginBottom="-26dp">
     *             <fragment
     *                 android:id="@+id/autocomplete_fragment"
     *                 android:name="com.google.android.libraries.places.widget.AutocompleteSupportFragment"
     *                 android:layout_width="300dp"
     *                 android:layout_height="wrap_content"
     *                 />
     *
     *         </androidx.cardview.widget.CardView>
     */

    /***
     *  JAVA For AutocompleteFragment
     *
     *  // Initialize the AutocompleteSupportFragment.
     *         AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
     *                 getSupportFragmentManager().findFragmentById(autocomplete_fragment);
     *
     *         // Specify the types of place data to return.
     *         autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
     *
     *         // Set up a PlaceSelectionListener to handle the response.
     *         autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
     *             @Override
     *             public void onPlaceSelected(Place place) {
     *                System.out.println("Place: " + place.getName() + ", " + place.getId());
     *             }
     *
     *             @Override
     *             public void onError(Status status) {
     *                 System.out.println("An error occurred: " + status);
     *             }
     *         });
     */


    private void displayUser(){
        SharedPreferences settings = getSharedPreferences("preferences", MODE_PRIVATE);

        System.out.println("\n" + settings.getString("First_Name",""));
        System.out.println(settings.getString("Last_Name",""));
        System.out.println(settings.getString("Pseudo",""));
        System.out.println(settings.getString("E_Mail",""));
        System.out.println(settings.getString("City_Name",""));
        System.out.println(settings.getString("City_Coordinates",""));
        System.out.println(settings.getString("UID",""));
        System.out.println(settings.getInt("Age",0));
    }

    private void retrieveDataFromFirestore(){
        /** Firebase Firestore Database **/

        database = FirebaseFirestore.getInstance();

        /*** Retrieves City Coordinates From Firestore ***/
        System.out.println("Retrieving city coordinates");
        CollectionReference locationsCollectionRef = database.collection("locations");
        locationsCollectionRef.document("Montpellier").get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                System.out.println("DocumentSnapshot data: " + document.getData());
                                cityCoordinates = new LatLng(document.getGeoPoint("LatLng").getLatitude(),document.getGeoPoint("LatLng").getLongitude());
                            } else {
                                System.out.println("No such document");
                            }
                        } else {
                            System.out.println("get failed with "+ task.getException());
                        }
                    }
                });
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
