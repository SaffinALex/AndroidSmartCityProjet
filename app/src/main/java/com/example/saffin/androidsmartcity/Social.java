package com.example.saffin.androidsmartcity;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.saffin.androidsmartcity.auth.BaseActivity;
import com.example.saffin.androidsmartcity.auth.Profil;
import com.google.android.material.navigation.NavigationView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class Social extends BaseActivity {

    private Button creerGroup;
    private Button mesGroupes;
    private Button groupes;

    private Social instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);

        instance = this;
        // 6 - Configure all views

        this.configureToolBar();

        this.configureDrawerLayout();

        this.configureNavigationView();

        creerGroup = (Button ) findViewById(R.id.button9);
        mesGroupes = (Button) findViewById(R.id.button10);
        groupes = (Button) findViewById(R.id.button7);

        creerGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(instance, GroupCreate.class));
            }
        });

        mesGroupes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(instance, MyGroup.class));
            }
        });

        groupes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(instance, SocialGroup.class));
            }
        });
    }


}
