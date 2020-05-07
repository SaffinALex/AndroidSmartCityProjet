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
import android.os.Bundle;
import android.system.Os;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Console;
import java.security.Provider;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener {

    private GoogleMap mMap;
    private MapsActivity instance;

    DatabaseReference database_root, database_locations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.instance = this;
        setContentView(R.layout.activity_maps);


        /** Places API **/
        // Initialize the SDK
        Places.initialize(getApplicationContext(), "AIzaSyBMsEY0ZSZu5-CI_R-cuMaM8vXx5mKeLHQ"); // TODO : Secure the API KEY !!

        // Create a new Places client instance
        PlacesClient placesClient = Places.createClient(this);

        /** Firebase Database **/
        database_root = FirebaseDatabase.getInstance().getReference();
        database_locations = database_root.child("locations");


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

        /** Gets the current position **/
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(location != null){
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
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
                database_locations.setValue(latLng);
            }
        });


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                LatLng latLng = marker.getPosition();

                marker.getSnippet();

                String FindPlaceRequest = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?";
                String input = "input=commerce&inputtype=textquery";
                String fields = "&fields=business_status,photos,formatted_address,geometry,name,icon,permanently_closed,place_id,plus_code,types&";
                String location = "locationbias=circle:20000@" + latLng.latitude + "," + latLng.longitude;
                String key = "&key=AIzaSyBMsEY0ZSZu5-CI_R-cuMaM8vXx5mKeLHQ";
                FindPlaceRequest += input + fields + location + key; // The order matters

                String NearbySearchRequest= "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
                location = "-33.8670522,151.1957362&radius=1500";
                String keyword = "&type=restaurant&keyword=cruise";
                key = "&key=AIzaSyBMsEY0ZSZu5-CI_R-cuMaM8vXx5mKeLHQ";

                NearbySearchRequest += location + keyword + key; // The order matters

                // Retrieve the data from the marker.
                Integer clickCount = (Integer) marker.getTag();

                // Check if a click count was set, then display the click count.
                if (clickCount != null) {
                    clickCount = clickCount + 1;
                    marker.setTag(clickCount);
                    System.err.println("\n" + marker.getTitle() + " has been clicked " + clickCount + " times.");
                }
                else{
                    System.err.println("\n\tThis marker had no tag attached\n");
                }

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
