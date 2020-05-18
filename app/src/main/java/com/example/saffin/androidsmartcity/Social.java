package com.example.saffin.androidsmartcity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.saffin.androidsmartcity.auth.BaseActivity;
import com.example.saffin.androidsmartcity.auth.Profil;
import com.example.saffin.androidsmartcity.models.Group;
import com.example.saffin.androidsmartcity.models.GroupHelper;
import com.example.saffin.androidsmartcity.models.UserHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Social extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener{
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);

        // 6 - Configure all views

        this.configureToolBar();

        this.configureDrawerLayout();

        this.configureNavigationView();
    }

    public void onClickGoCreateGroups(View v){
        Intent intent = new Intent(this, GroupCreate.class);
        startActivity(intent);
    }

    public void onClickGoMyGroups(View v){
        startActivity(new Intent(this, MyGroup.class));
    }

    public void onClickGoAllGroups(View v){
        startActivity(new Intent(this, SocialGroup.class));
    }




}
