package com.example.saffin.androidsmartcity;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.os.Bundle;
import android.system.Os;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.Toast;

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
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

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
    private EditText input;
    private Location currentLocation = null;

    FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.instance = this;
        setContentView(R.layout.activity_maps);

        input = (EditText) findViewById(R.id.map_input);

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                System.out.println("current input: "+charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                System.out.println("current editable: "+editable.toString());
            }
        });



        /** Places API **/
        // Initialize the SDK
        Places.initialize(getApplicationContext(), "AIzaSyBMsEY0ZSZu5-CI_R-cuMaM8vXx5mKeLHQ"); // TODO : Secure the API KEY !! AIzaSyBMsEY0ZSZu5-CI_R-cuMaM8vXx5mKeLHQ

        // Create a new Places client instance
        PlacesClient placesClient = Places.createClient(this);

        /** Firebase Firestore Database **/

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        CollectionReference locationsCollectionRef = database.collection("locations");
        locationsCollectionRef.document("Montpellier");

        /** Launches the map **/
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        while(hasLocationPermission != PackageManager.PERMISSION_GRANTED){
            hasLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            permissions.clear();
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            if (!permissions.isEmpty()) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), 0);
            }

            onRequestPermissionsResult(0, permissions.toArray(new String[permissions.size()]), new int[permissions.size()]);
        }

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
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(currentLocation != null){
            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,12));
        }
        else{
            /** Moves the camera on Sydney **/
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(-34, 151)));
        }


        /** Allows to add Markers **/
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Marker mark = mMap.addMarker(new MarkerOptions().position(latLng));
                mark.setSnippet(latLng.toString());
                mark.setTitle("Marker_" + counter++);
                mark.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            }
        });




        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                LatLng latLng = marker.getPosition();
                PlacesClient mPlacesClient = Places.createClient(getApplicationContext());

                String key = "&key=AIzaSyBMsEY0ZSZu5-CI_R-cuMaM8vXx5mKeLHQ";

                // Use fields to define the data types to return.
                List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS,
                        Place.Field.LAT_LNG);

                // Use the builder to create a FindCurrentPlaceRequest.
                FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);

                // Get the likely places - that is, the businesses and other points of interest that
                // are the best match for the device's current location.
                assert mPlacesClient != null;
                @SuppressWarnings("MissingPermission") final
                Task<FindCurrentPlaceResponse> placeResult =
                        mPlacesClient.findCurrentPlace(request);
                placeResult.addOnCompleteListener (new OnCompleteListener<FindCurrentPlaceResponse>() {
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
                            String [] mLikelyPlaceNames = new String[count];
                            String [] mLikelyPlaceAddresses = new String[count];
                            List [] mLikelyPlaceAttributions = new List[count];
                            LatLng [] mLikelyPlaceLatLngs = new LatLng[count];

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

                            for(int j = 0; j < mLikelyPlaceNames.length;j++){
                                System.err.println("PlaceName: " + mLikelyPlaceNames[j].toString());
                            }
                            for(int j = 0; j < mLikelyPlaceAddresses.length;j++){
                                System.err.println("PlaceAdresse: " + mLikelyPlaceAddresses[j].toString());
                            }
                            for(int j = 0; j < mLikelyPlaceAttributions.length;j++){
                                System.err.println("PlaceAttribution: " + mLikelyPlaceAttributions[j].toString());
                            }
                            for(int j = 0; j < mLikelyPlaceLatLngs.length;j++){
                                System.err.println("PlaceCoordinates: " + mLikelyPlaceLatLngs[j].toString());
                            }

                            // Show a dialog offering the user the list of likely places, and add a
                            // marker at the selected place.
                            //instance.openPlacesDialog();
                        }
                        else {
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
