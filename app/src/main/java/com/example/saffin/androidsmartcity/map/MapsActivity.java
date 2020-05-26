package com.example.saffin.androidsmartcity.map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener {

    private GoogleMap mMap;
    private MapsActivity instance;
    private PlacesClient placesClient;
    private LatLng cityCoordinates = null;
    private String API_KEY;
    private EditText search_field;
    private FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.instance = this;
        setContentView(R.layout.activity_maps);

        API_KEY = getString(R.string.maps_api_key); // Retrieves API KEY From Graddle Properties

        retrieveCoordinatesFromPreferences();

        initializePlacesAPI();

        // Obtain the SupportMapFragment and get notified when the com.example.saffin.androidsmartcity.map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the com.example.saffin.androidsmartcity.map once available.
     * This callback is triggered when the com.example.saffin.androidsmartcity.map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        permissionManagement();
        mapSetup(googleMap);
        researchSetup();

    }

    private void researchSetup(){
        search_field = (EditText) findViewById(R.id.search_field);

        search_field.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    mMap.clear();

                    Object transferData[] = new Object[2];
                    GetNearbyPlaces nearbyPlaces = new GetNearbyPlaces(instance);

                    String research_keyword = search_field.getText().toString();

                    research_keyword = Normalizer.normalize(research_keyword, Normalizer.Form.NFD);
                    research_keyword = research_keyword.replaceAll("[^\\p{ASCII}]", "");

                    String url = getURL(cityCoordinates,research_keyword);

                    transferData[0] = mMap;
                    transferData[1] = url;

                    System.out.println("Searching for nearby " + research_keyword);

                    nearbyPlaces.execute(transferData);

                    hideKeyboard(instance);
                    search_field.clearFocus();

                    //placesClient.fetchPlace();

                    handled = true;
                }
                return handled;
            }
        });
    }

    private void permissionManagement(){
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
    }

    private void mapSetup(GoogleMap googleMap){
        mMap = googleMap;
        mMap.clear();

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cityCoordinates,12));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cityCoordinates, 12.0f));

        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
    }

    private void initializePlacesAPI(){
        Places.initialize(getApplicationContext(), API_KEY ); // Initialize the SDK
        placesClient = Places.createClient(instance); // Create a new Places client instance
    }

    private void retrieveCoordinatesFromPreferences(){
        SharedPreferences settings = getSharedPreferences("preferences", MODE_PRIVATE);
        String data = settings.getString("City_Coordinates","");
        String latitude = data.substring(data.indexOf('(') + 1,data.indexOf(','));
        String longitude = data.substring(data.indexOf(',') + 1, data.length() - 1);
        System.out.println(latitude);
        System.out.println(longitude);

        cityCoordinates = new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));
    }

    private String getURL(LatLng coordinates, String input){
        StringBuilder googleURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");

        String location = "location=" + Double.toString(coordinates.latitude) + "," + Double.toString(coordinates.longitude);
        String radius = "radius=" + Integer.toString(10000);
        String keyword = "keyword=" + input;
        String language = "language=fr";
        String key = "key=" + API_KEY;

        googleURL.append(location + "&" + radius + "&" + keyword + "&" + language + "&" + key);

        Log.d("MapsActivity","url: " + googleURL.toString());

        return googleURL.toString();
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
