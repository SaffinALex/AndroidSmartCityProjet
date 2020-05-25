package com.example.saffin.androidsmartcity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.autofill.AutofillValue;
import android.webkit.WebView;

import com.example.saffin.androidsmartcity.auth.Profil;
import com.google.android.material.navigation.NavigationView;

public class News extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private News instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        this.configureToolBar();

        this.configureDrawerLayout();

        this.configureNavigationView();

        this.instance = this;

        // Displays the news for "ville"
        String ville = "Montpellier";
        String url = "https://news.google.com/search?q=" + ville + "&hl=fr&gl=FR&ceid=FR%3Afr";
        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.loadUrl(url);
        myWebView.scrollTo(430,430);

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
            case R.id.activity_main_drawer_news :
                //startActivity(new Intent(this, News.class));
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
        this.toolbar = (Toolbar) findViewById(R.id.activity_news_toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
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
