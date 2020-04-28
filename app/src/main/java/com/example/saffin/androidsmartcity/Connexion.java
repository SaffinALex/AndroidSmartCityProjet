package com.example.saffin.androidsmartcity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.design.widget.NavigationView;
import android.view.MenuItem;

import android.os.Bundle;
import android.view.View;

import java.io.IOException;

public class Connexion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);
    }

    public void goHome(View v) throws IOException {
        Intent intent = new Intent(Connexion.this,Home.class);
        startActivity(intent);
    }
}
