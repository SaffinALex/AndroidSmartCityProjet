package com.example.saffin.androidsmartcity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.design.widget.NavigationView;
import android.view.MenuItem;

import android.support.v7.widget.Toolbar;
import android.support.v4.view.GravityCompat;

import android.os.Bundle;
import android.view.View;

import com.example.saffin.androidsmartcity.auth.BaseActivity;
import com.example.saffin.androidsmartcity.auth.Profil;

    public class Home extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        this.configureToolBar();

        this.configureDrawerLayout();

        this.configureNavigationView();

    }



    public void onClickGoGroups(View v){
        startActivity(new Intent(Home.this, Social.class));
    }
}
