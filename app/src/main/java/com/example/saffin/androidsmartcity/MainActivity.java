package com.example.saffin.androidsmartcity;

        import android.app.Activity;
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

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reception_screen);
    }

    public void goIdentification(View v) throws IOException {
        Intent intent = new Intent(MainActivity.this,Connexion.class);
        startActivity(intent);
    }
    public void goCreateAccount(View v) throws IOException {
        Intent intent = new Intent(MainActivity.this,AccountCreation.class);
        startActivity(intent);
    }

}
