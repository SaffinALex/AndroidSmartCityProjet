package com.example.saffin.androidsmartcity;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.os.Bundle;
import android.os.Parcel;
import android.system.Os;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.LocationBias;
import com.google.android.libraries.places.api.model.LocationRestriction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Console;
import java.security.Provider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener {

    private GoogleMap mMap;
    private MapsActivity instance;
    private int counter = 0;
    private Location currentLocation = null;
    private PlacesClient placesClient;
    private GeoPoint cityGeoPoint = null;
    private String API_KEY;

    FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.instance = this;
        setContentView(R.layout.activity_maps);

        API_KEY = getString(R.string.maps_api_key); // Retrieves API KEY From Graddle Properties


        /** Firebase Firestore Database **/

        database = FirebaseFirestore.getInstance();

        /*** Retrieves City Coordinates ***/
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
                                cityGeoPoint = document.getGeoPoint("LatLng");
                            } else {
                                System.out.println("No such document");
                            }
                        } else {
                            System.out.println("get failed with "+ task.getException());
                        }
                    }
                });

        displayUser();

        /** Places API **/
        // Initialize the SDK
        Places.initialize(getApplicationContext(), API_KEY );

        // Create a new Places client instance
        placesClient = Places.createClient(instance);

        /** Launches the map **/
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

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


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();

        /** Asking For Permission **/
        int hasLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> permissions = new ArrayList<String>();

        while (hasLocationPermission != PackageManager.PERMISSION_GRANTED) {
            hasLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            permissions.clear();
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            if (!permissions.isEmpty()) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), 0);
            }

            onRequestPermissionsResult(0, permissions.toArray(new String[permissions.size()]), new int[permissions.size()]);
        }

        if(cityGeoPoint != null)
         mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(cityGeoPoint.getLatitude(), cityGeoPoint.getLongitude())));

        /***
         * Places AutoComplete
         */
        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
               System.out.println("Place: " + place.getName() + ", " + place.getId());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                System.out.println("An error occurred: " + status);
            }
        });

        /** ***************
         * From here it's the Actual Map manipulation
         * **************** **/

        /** Adds the "MyLocation" button **/
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        mMap.setIndoorEnabled(true);
        mMap.setTrafficEnabled(true);

        /** Gets the current position **/
 /*       LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (currentLocation != null) {
            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
        } else {
            // Moves the camera on Sydney
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(-34, 151)));
        }*/


        /** Allows to add Markers **/
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Marker mark = mMap.addMarker(new MarkerOptions().position(latLng));
                mark.setSnippet(latLng.toString());
                mark.setTitle("Marker_" + counter++);
                mark.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

                /** Tries The Places API **/

               /* List<Place.Field> fields = new ArrayList<>();

                fields.add(Place.Field.NAME);
                fields.add(Place.Field.ADDRESS);
                fields.add(Place.Field.LAT_LNG);

                Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(FindCurrentPlaceRequest.builder(fields).build());

                placeResponse.addOnCompleteListener(new OnCompleteListener<FindCurrentPlaceResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<FindCurrentPlaceResponse> task) {
                        if (task.isSuccessful()) {
                            FindCurrentPlaceResponse response = task.getResult();
                            for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                                System.out.println("Place " + placeLikelihood.getPlace().getName() + " has likelihood: " + placeLikelihood.getLikelihood());
                            }
                        } else {
                            Exception exception = task.getException();
                            if (exception instanceof ApiException) {
                                ApiException apiException = (ApiException) exception;
                                System.out.println("Place not found: " + apiException.getStatusCode());
                            }
                        }
                    }
                });*/

                String nearbySearchRequest = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?&";
                String location = "location=" + Double.toString(43.61092) + "," + Double.toString(3.87723);
                String radius = "radius=" + Integer.toString(10000);
                String keyword = "keyword=restaurant";
                String language = "language=fr";
                nearbySearchRequest += location + "&" + radius + "&" + keyword + "&" + language + "&" + API_KEY;

                placesClient.fetchPlace(FetchPlaceRequest.newInstance(nearbySearchRequest,Arrays.asList(Place.Field.ID, Place.Field.NAME)))
                        .addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                            @Override
                            public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                                System.out.println("Success");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

            }
        });


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


    @Override
    public boolean onMyLocationButtonClick() {
        System.err.println("My Location Button Clicked");
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        System.err.println("Current location:\n" + location);
    }
}
