package com.example.saffin.androidsmartcity;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.saffin.androidsmartcity.map.MapsActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

import android.text.method.Touch;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;

import com.example.saffin.androidsmartcity.auth.Profil;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Home instance;
    private CalendarView cv;
    private TextView eventDate;
    private Button addEventBtn;
    private String UID;

    FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        this.instance = this;

        this.configureToolBar();

        this.configureDrawerLayout();

        this.configureNavigationView();

        database = FirebaseFirestore.getInstance();

        linkToLayout();

        initUserPreferences();

        initAgendaDate();

        agendaManagement();
    }

    private void initUserPreferences(){
        UID = "crlqKmbeD6dJMnFbzwXiBNqvAcP2";
        // TODO put these strings in variables if needed
        SharedPreferences settings = getSharedPreferences("preferences",MODE_PRIVATE);
        settings.getString("First_Name","");
        settings.getString("Last_Name","");
        settings.getString("Pseudo","");
        settings.getString("Password","");
        settings.getString("E_Mail","");
        settings.getString("City_Name","");
        settings.getString("City_Coordinates","");
        settings.getString("UID","");
        settings.getInt("Age",0);
    }

    private void agendaManagement(){
        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {

                month++;

                //addEventToAgenda("coucou",integerToDate(dayOfMonth,month,year,8,30),"no more");

                String date =  Integer.toString(dayOfMonth) + '/' + Integer.toString(month) + '/' + Integer.toString(year);

                eventDate.setText(date);
            }
        });
        cv.getDate();

        addEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void initAgendaDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String selectedDate = sdf.format(new Date(cv.getDate()));
        eventDate.setText(selectedDate);
    }

    private void linkToLayout(){
        cv = (CalendarView) findViewById(R.id.calendarView);
        eventDate = (TextView) findViewById(R.id.displayEventDate);
        addEventBtn = (Button) findViewById(R.id.addEventBtn);
    }

    private LatLng retrieveLocation(String ville){
        final LatLng[] location = {null};
        CollectionReference locationsCollectionRef = database.collection("locations");
        DocumentReference dr = locationsCollectionRef.document(ville);
        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        GeoPoint location_ville = document.getGeoPoint("LatLng");
                        location[0] = new LatLng(location_ville.getLatitude(),location_ville.getLongitude());
                    } else {
                        System.out.println("No such document");
                    }
                } else {
                    System.out.println("get failed with " + task.getException());
                }
            }
        });
        return location[0];
    }

    private Date integerToDate(int dayOfMonth, int month, int year, int hour, int minute){

        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy/HH/mm");

        if(dayOfMonth < 1 || dayOfMonth > 31){
            dayOfMonth = 1;
        }
        if(month < 1 || month > 12){
            month = 1;
        }
        if(hour < 0 || hour > 23){
            hour = 0;
        }
        if(minute < 0 || minute > 59){
            minute = 0;
        }

        String month_str;
        String day_str;
        String hour_str;
        String minute_str;

        if(month < 10){
            month_str = "0" + Integer.toString(month);
        }else{
            month_str = Integer.toString(month);
        }

        if(dayOfMonth < 10){
            day_str = "0" + Integer.toString(dayOfMonth);
        }else{
            day_str = Integer.toString(dayOfMonth);
        }

        if(hour < 10){
            hour_str = "0" + Integer.toString(hour);
        }else{
            hour_str = Integer.toString(hour);
        }

        if(minute < 10){
            minute_str = "0" + Integer.toString(minute);
        }else{
            minute_str = Integer.toString(minute);
        }

        String parcelable = month_str + "/" + day_str + "/" + Integer.toString(year) + "/" + hour_str + "/" + minute_str;
        Date dae = new Date();
        try {
            dae = formatter.parse(parcelable);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return dae;
    }

    private void addEventToAgenda(String title, Date newEventDate, String more_info){

        Map<String, Object> event = new HashMap<>();

        Timestamp date = new Timestamp(newEventDate);

        event.put("title", title);
        event.put("date", date);
        event.put("uid", UID);
        event.put("more_info", more_info);

        /** It's possible to add other categories to objects of the same collection **/

        CollectionReference events_ref = database.collection("events");
        events_ref.add(event)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    System.out.println("DocumentSnapshot added with ID: " + documentReference.getId());
                }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
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
                //startActivity(new Intent(this, Home.class));
                break;
            case R.id.activity_main_drawer_news :
                startActivity(new Intent(this, News.class));
                break;
            case R.id.activity_main_drawer_shops :
                startActivity(new Intent(this, Shop.class));
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

    // 1 - Configure Toolbar
    private void configureToolBar(){
        this.toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);
    }

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

    public void goNews(View view) {

    }

    public void goShop(View view) {

        startActivity(new Intent(instance, MapsActivity.class));
    }

    public void goGroups(View view) {

    }

    public void goAd(View view) {
    }
}
