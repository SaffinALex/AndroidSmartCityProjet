package com.example.saffin.androidsmartcity.home;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.saffin.androidsmartcity.Advertisement;
import com.example.saffin.androidsmartcity.News;
import com.example.saffin.androidsmartcity.R;
import com.example.saffin.androidsmartcity.Settings;
import com.example.saffin.androidsmartcity.Social;
import com.example.saffin.androidsmartcity.agenda.Home;
import com.example.saffin.androidsmartcity.commerces.CommerceActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.navigation.NavigationView;

import android.view.MenuItem;
import android.webkit.WebView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;

import com.example.saffin.androidsmartcity.auth.Profil;

public class Home_temporary extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private String WEATHER_API_KEY;

    private Home_temporary instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_temporary);

        this.instance = this;

        this.configureToolBar();

        this.configureDrawerLayout();

        this.configureNavigationView();

        WEATHER_API_KEY = getString(R.string.weather_api_key);

        //launchCurrentWeather();

        setPreferences();
    }

    private void setPreferences(){
        SharedPreferences settings = getSharedPreferences("preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        String firstName = "Jean_Eude";
        String age = "27";
        String secondName = "Dubarrzeau";
        editor.putString("First_Name", firstName);
        editor.putString("Last_Name", secondName);
        editor.putString("Pseudo", "noUsername");
        editor.putString("Password", "HiddenPsw");
        editor.putString("E_Mail", "e@mail.com");
        editor.putString("City_Name", "Montpellier");
        editor.putString("City_Coordinates", new LatLng(43.61092,3.87723).toString());
        editor.putString("UID", "crlqKmbeD6dJMnFbzwXiBNqvAcP2");
        editor.putInt("Age", Integer.parseInt(age));
        editor.commit();
    }

    private void launchCurrentWeather(){
        String url = "http://api.weatherstack.com/current?access_key=" + WEATHER_API_KEY + "&query=New York";
        WebView myWebView = (WebView) findViewById(R.id.webview_current_weather);
        myWebView.loadUrl(url);
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
            case R.id.activity_main_drawer_agenda:
                startActivity(new Intent(this, Home.class));
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
