package com.example.saffin.androidsmartcity.map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.saffin.androidsmartcity.Advertisement;
import com.example.saffin.androidsmartcity.News;
import com.example.saffin.androidsmartcity.R;
import com.example.saffin.androidsmartcity.Settings;
import com.example.saffin.androidsmartcity.Social;
import com.example.saffin.androidsmartcity.agenda.Home;
import com.example.saffin.androidsmartcity.auth.Profil;
import com.example.saffin.androidsmartcity.home.Home_temporary;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,NavigationView.OnNavigationItemSelectedListener {

    private static class justForToolBar extends AppCompatActivity{

    }
    private GoogleMap mMap;
    private MapsActivity instance;
    private PlacesClient placesClient;
    private LatLng cityCoordinates = null;
    private String API_KEY;
    private EditText search_field;
    private FirebaseFirestore database;
    private RecyclerView tempDetailRecyclerView;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.instance = this;
        setContentView(R.layout.activity_maps);

        this.configureDrawerLayout();

        this.configureNavigationView();

        API_KEY = getString(R.string.maps_api_key); // Retrieves API KEY From Graddle Properties

        retrieveCoordinatesFromPreferences();

        initializePlacesAPI();

        tempDetailRecyclerView = (RecyclerView) findViewById(R.id.places_details_recycler_view);

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
                    GetNearbyPlaces nearbyPlaces = new GetNearbyPlaces(instance,placesClient,tempDetailRecyclerView);

                    String research_keyword = search_field.getText().toString();

                    research_keyword = Normalizer.normalize(research_keyword, Normalizer.Form.NFD);
                    research_keyword = research_keyword.replaceAll("[^\\p{ASCII}]", "");

                    String url = getNearbyPlaceURL(cityCoordinates,research_keyword);

                    transferData[0] = mMap;
                    transferData[1] = url;

                    System.out.println("Searching for nearby " + research_keyword);

                    nearbyPlaces.execute(transferData);

                    hideKeyboard(instance);
                    search_field.clearFocus();
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

    private String getNearbyPlaceURL(LatLng coordinates, String input){
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

    private String getPlaceDetailsURL(String place_id, String fields){
        StringBuilder googleURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?place_id=");
        googleURL.append(place_id);
        googleURL.append("&fields=" + fields);
        googleURL.append("&key=" + API_KEY);
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

    @Override
    public void onBackPressed() {
        // 5 - Handle back click to close menu
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // 4 - Handle Navigation Item Click
        int id = item.getItemId();

        switch (id){
            case R.id.activity_main_drawer_home :
                startActivity(new Intent(this, Home_temporary.class));
                break;
            case R.id.activity_main_drawer_agenda:
                startActivity(new Intent(this, Home.class));
                break;
            case R.id.activity_main_drawer_news :
                startActivity(new Intent(this, News.class));
                break;
            case R.id.activity_main_drawer_shops :
                //startActivity(new Intent(this, Shop.class));
                break;
            case R.id.activity_main_drawer_social :
                startActivity(new Intent(this, Social.class));
                break;
            case R.id.activity_main_drawer_ads :
                startActivity(new Intent(this, Advertisement.class));
                break;
            case R.id.activity_main_drawer_profile:
                Intent intent = new Intent(this, Profil.class);
                startActivity(intent);
                break;
            case R.id.activity_main_drawer_settings:
                startActivity(new Intent(this, Settings.class));
                break;
            default:
                break;
        }

        this.drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    // ---------------------
    // CONFIGURATION
    // ---------------------

    // 2 - Configure Drawer Layout
    private void configureDrawerLayout(){
        this.drawerLayout = (DrawerLayout) findViewById(R.id.activity_main_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    // 3 - Configure NavigationView
    private void configureNavigationView(){
        this.navigationView = (NavigationView) findViewById(R.id.activity_main_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
}
