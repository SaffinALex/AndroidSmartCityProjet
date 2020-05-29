package com.example.saffin.androidsmartcity.auth;

import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.saffin.androidsmartcity.Advertisement;
import com.example.saffin.androidsmartcity.News;
import com.example.saffin.androidsmartcity.R;
import com.example.saffin.androidsmartcity.Settings;
import com.example.saffin.androidsmartcity.commerces.CommerceActivity;
import com.example.saffin.androidsmartcity.Social;
import com.example.saffin.androidsmartcity.agenda.Home;
import com.example.saffin.androidsmartcity.map.MapsActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;


public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    @Nullable
    protected FirebaseUser getCurrentUser(){ return FirebaseAuth.getInstance().getCurrentUser(); }
    protected Boolean isCurrentUserLogged(){ return (this.getCurrentUser() != null); }

    // --------------------
    // ERROR HANDLER
    // --------------------

    protected OnFailureListener onFailureListener(){
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), getString(R.string.error_unknown_error), Toast.LENGTH_LONG).show();
            }
        };
    }

    @Override
    public void onBackPressed() {
        // 5 - Handle back click to close menu
        if (this.drawerLayout != null && this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
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
                startActivity(new Intent(this, MapsActivity.class));
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
    protected void configureToolBar(){
        this.toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);
    }

    // 2 - Configure Drawer Layout
    protected void configureDrawerLayout(){
        this.drawerLayout = (DrawerLayout) findViewById(R.id.activity_main_drawer_layout);
        Log.d("coucou", String.valueOf(drawerLayout));
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    // 3 - Configure NavigationView
    protected void configureNavigationView(){
        this.navigationView = (NavigationView) findViewById(R.id.activity_main_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

}
