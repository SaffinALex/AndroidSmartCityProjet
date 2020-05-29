package com.example.saffin.androidsmartcity.agenda;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.saffin.androidsmartcity.Advertisement;
import com.example.saffin.androidsmartcity.home.Home;
import com.example.saffin.androidsmartcity.News;
import com.example.saffin.androidsmartcity.R;
import com.example.saffin.androidsmartcity.Settings;
import com.example.saffin.androidsmartcity.commerces.CommerceActivity;
import com.example.saffin.androidsmartcity.Social;
import com.example.saffin.androidsmartcity.auth.Profil;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Th0ma on 28/05/2020
 */
public class Agenda extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private Agenda instance;

    private CalendarView cv;
    private String calendarCurrentDay;
    private Button addEventBtn;

    private String UID;

    private RecyclerView agendaRecyclerView;
    private RecyclerView.Adapter agendaAdapter;
    private RecyclerView.LayoutManager agendaLayoutManager;
    private ArrayList<Event> mEvents;

    private FirebaseFirestore mFirestore_Database;
    private DateFormatManipulator mDateFormatManipulator;

    private EventDatabase mEventDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda);

        this.instance = this;

        this.configureToolBar();

        this.configureDrawerLayout();

        this.configureNavigationView();

        mEventDatabase = new EventDatabase(instance);

        mFirestore_Database = FirebaseFirestore.getInstance();

        mDateFormatManipulator = new DateFormatManipulator();

        linkToLayout();

        retrieveUserPreferences();

        mEvents = new ArrayList<>();

        calendarManagement();

        initRecyclerView();

        calendarCurrentDay = mDateFormatManipulator.dateToStringDay(new Date(cv.getDate()));

        if(loadEventsOfTheDayFromDB() == -1){
            System.out.println("Pas de DAILY DATA dans DB");
            sneakySaveEventInLocalDB(new Event(calendarCurrentDay,"default","default","default"));
        }

        updateRecyclerView();
    }

    private int loadEventsOfTheDayFromDB(){
        mEvents.removeAll(mEvents);

        Cursor data = mEventDatabase.selectRecordsOfTheDay(calendarCurrentDay);

        if(data.getCount() == 0){
            return -1;
        }

        while(data.moveToNext()){
            Event event = new Event(data.getString(1),data.getString(2),data.getString(3),data.getString(4));
            System.out.println(event.toString());
            mEvents.add(event);
        }

        System.out.println("Il y a " + mEvents.size() + " events dans la DB locale");

        for(int i = 0; i < mEvents.size();i++){
            for(int j = i +1; j< mEvents.size();j++){
                EventComparator comparator = new EventComparator();
                if(comparator.compare(mEvents.get(i),mEvents.get(j)) == 1){
                    Event temp = mEvents.get(i);
                    mEvents.set(i,mEvents.get(j));
                    mEvents.set(j,temp);
                }
            }
            System.out.println(mEvents.get(i).toString());
        }
        return 0;
    }

    private int  loadEventsFromDB(){

        mEvents.removeAll(mEvents);

        Cursor data = mEventDatabase.selectRecords();

        if(data.getCount() == 0){
            return -1;
        }

        while(data.moveToNext()){
            Event event = new Event(data.getString(1),data.getString(2),data.getString(3),data.getString(4));
            System.out.println(event.toString());
            mEvents.add(event);
        }

        System.out.println("Il y a " + mEvents.size() + " events dans la DB locale");

        for(int i = 0; i < mEvents.size();i++){
            for(int j = i +1; j< mEvents.size();j++){
                EventComparator comparator = new EventComparator();
                if(comparator.compare(mEvents.get(i),mEvents.get(j)) == 1){
                    Event temp = mEvents.get(i);
                    mEvents.set(i,mEvents.get(j));
                    mEvents.set(j,temp);
                }
            }
            System.out.println(mEvents.get(i).toString());
        }
        return 0;
    }

    private void sneakySaveEventInLocalDB(Event event){
        boolean insertData = mEventDatabase.createRecords(event.getJour(),event.getHeures(),event.getTitre(),event.getDetails());
        if(insertData){
            System.out.println("Evenement ajouté avec succés !");
        }
        else{
            System.out.println("Erreur durant l'ajout de l'évenement");
        }
    }

    private void saveEventInLocalDB(Event event){
        boolean insertData = mEventDatabase.createRecords(event.getJour(),event.getHeures(),event.getTitre(),event.getDetails());
        if(insertData){
            Toast.makeText(instance,"Evenement ajouté avec succés !",Toast.LENGTH_LONG).show();
            System.out.println("Evenement ajouté avec succés !");
        }
        else{
            Toast.makeText(instance,"Erreur durant l'ajout de l'évenement",Toast.LENGTH_LONG).show();
            System.out.println("Erreur durant l'ajout de l'évenement");
        }
    }

    private void initRecyclerView(){
        // use a linear layout manager
        agendaLayoutManager = new LinearLayoutManager(instance);
        agendaRecyclerView.setLayoutManager(agendaLayoutManager);

        // specify an adapter (see also next example)
        agendaAdapter= new AgendaAdapter(mEvents);
        agendaRecyclerView.setAdapter(agendaAdapter);
    }

    private void retrieveUserPreferences(){
        // TODO put these strings in variables if needed
        SharedPreferences settings = getSharedPreferences("preferences",MODE_PRIVATE);
        settings.getString("First_Name","");
        settings.getString("Last_Name","");
        settings.getString("Pseudo","");
        settings.getString("Password","");
        settings.getString("E_Mail","");
        settings.getString("City_Name","");
        settings.getString("City_Coordinates","");

        UID = "crlqKmbeD6dJMnFbzwXiBNqvAcP2";

        settings.getString("UID","");
        settings.getInt("Age",0);
    }

    private void calendarManagement(){
        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {

                System.out.println("Day Changed");

                month++;

                calendarCurrentDay =  Integer.toString(dayOfMonth) + '/' + Integer.toString(month) + '/' + Integer.toString(year);

                if(loadEventsOfTheDayFromDB() == -1){
                    sneakySaveEventInLocalDB(new Event(calendarCurrentDay,"default","default","default"));
                }

                updateRecyclerView();
            }
        });
        cv.getDate();

        addEventManagement();
    }

    private void retrieveEventFromFireStore(){

        mFirestore_Database.collection("events").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int counter = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String firestore_uid = document.getString("uid");
                                Date event_date = document.getDate("date");

                                if(mDateFormatManipulator.dateToStringDay(event_date).equals(calendarCurrentDay)){

                                    assert firestore_uid != null;
                                    if(firestore_uid.equals(UID)){
                                        counter++;

                                        /** Building CardView **/
                                        String [] data = new String[4];

                                        data[0] = mDateFormatManipulator.dateToStringDay(event_date);
                                        data[1] = mDateFormatManipulator.stringHoursFromDate(event_date);
                                        data[2] = document.getString("title");
                                        data[3] = document.getString("more_info");

                                        try {
                                            mEvents.add(new Event(data));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                            if(counter == 0){
                                String [] data = new String[4];

                                data[0] = calendarCurrentDay;
                                data[1] = "Aujourd'hui";
                                data[2] = "Aucun Evenement";
                                data[3] = "";

                                try {
                                    mEvents.add(new Event(data));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            System.out.println( "Error getting documents." + task.getException());
                        }
                    }
                });
    }

    private void updateRecyclerView(){
        agendaRecyclerView.removeAllViews();
        agendaAdapter.notifyItemRangeRemoved(0,agendaAdapter.getItemCount());
        agendaAdapter.notifyDataSetChanged();
    }

    private void addEventManagement(){
        addEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(instance);
                final View mView = getLayoutInflater().inflate(R.layout.add_event_dialog,null);

                final EditText titre_input = (EditText) mView.findViewById(R.id.et_titre_event);
                final EditText infos_input = (EditText) mView.findViewById(R.id.et_infos_event);
                TextView date_choisie = (TextView) mView.findViewById(R.id.tv_date);
                final NumberPicker hourPicker = (NumberPicker) mView.findViewById(R.id.hourPicker);
                final NumberPicker minutePicker = (NumberPicker) mView.findViewById(R.id.minutePicker);

                hourPicker.setMinValue(0); hourPicker.setMaxValue(23);
                hourPicker.setValue(8);
                minutePicker.setMinValue(0); minutePicker.setMaxValue(59);

                date_choisie.setText(" " + calendarCurrentDay + " ");

                mBuilder.setTitle(R.string.ajouterEvenement);

                mBuilder.setPositiveButton(R.string.valider, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        ArrayList<Integer> date_values = mDateFormatManipulator.stringDateToIntDate(calendarCurrentDay);

                        Date date_to_send = mDateFormatManipulator.integerToDate(date_values.get(0), date_values.get(1), date_values.get(2), hourPicker.getValue(),minutePicker.getValue());

                        /** Building Cardview **/
                        String [] data = new String[4];

                        data[0] = calendarCurrentDay;
                        data[1] = mDateFormatManipulator.stringHoursFromDate(date_to_send);
                        data[2] = titre_input.getText().toString();
                        data[3] = infos_input.getText().toString();

                        try {
                            saveEventInLocalDB(new Event(data));

                            loadEventsOfTheDayFromDB();

                            updateRecyclerView();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        addEventToFirestore(titre_input.getText().toString(), date_to_send, infos_input.getText().toString());
                    }
                });

                mBuilder.setNegativeButton(R.string.annuler, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();
            }
        });
    }

    private void linkToLayout(){
        cv = (CalendarView) findViewById(R.id.calendarView);
        addEventBtn = (Button) findViewById(R.id.addEventBtn);
        agendaRecyclerView = (RecyclerView) findViewById(R.id.agenda_recycler_view);
    }

    private LatLng retrieveLocation(String ville){
        final LatLng[] location = {null};
        CollectionReference locationsCollectionRef = mFirestore_Database.collection("locations");
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

    private void addEventToFirestore(String title, Date newEventDate, String more_info){

        Map<String, Object> event = new HashMap<>();

        Timestamp date = new Timestamp(newEventDate);

        event.put("title", title);
        event.put("date", date);
        event.put("uid", UID);
        event.put("more_info", more_info);

        /** It's possible to add other categories to objects of the same collection **/

        CollectionReference events_ref = mFirestore_Database.collection("events");
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
                startActivity(new Intent(this, Home.class));
                break;
            case R.id.activity_main_drawer_agenda:
                //startActivity(new Intent(this,Agenda.class));
                break;
            case R.id.activity_main_drawer_news :
                startActivity(new Intent(this, News.class));
                break;
            case R.id.activity_main_drawer_shops :
                startActivity(new Intent(this, CommerceActivity.class));
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
}