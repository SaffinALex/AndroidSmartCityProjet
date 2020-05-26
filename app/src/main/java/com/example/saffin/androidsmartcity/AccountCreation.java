package com.example.saffin.androidsmartcity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.saffin.androidsmartcity.auth.BaseActivity;
import com.example.saffin.androidsmartcity.models.UserHelper;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public class AccountCreation extends BaseActivity {
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(this.isCurrentUserLogged()) {
            startActivity(new Intent(AccountCreation.this, Home.class));
        }
        else{
            setContentView(R.layout.activity_account_creation);
        }
    }
    protected void onResume() {
        super.onResume();
        // 5 - Update UI when activity is resuming
        this.updateUIWhenResuming();
    }
    private void updateUIWhenResuming(){
        if (this.isCurrentUserLogged()){
            this.startHomeActivity();
        }
    }
    private void startHomeActivity(){
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }

    public void onCreateAccount(View v) throws IOException {
        firebaseAuth = FirebaseAuth.getInstance();
        EditText psw = (EditText) findViewById(R.id.password);
        EditText mail = (EditText) findViewById(R.id.email);
        String userEmail = mail.getText().toString();
        String userPaswd = psw.getText().toString();
        EditText prenom = (EditText) findViewById(R.id.prenom);
        EditText nom = (EditText) findViewById(R.id.nom);
        String userNom = nom.getText().toString();
        String userPrenom = prenom.getText().toString();
        EditText ville = (EditText) findViewById(R.id.city);
        String userVille = ville.getText().toString();
        if (userNom.isEmpty()) {
            nom.setError("Enter First Name");
            nom.requestFocus();
        }else if (userPrenom.isEmpty()) {
            prenom.setError("Enter Second Name");
            prenom.requestFocus();
        } else if (userVille.isEmpty()) {
            ville.setError("Enter City");
            ville.requestFocus();
        }else if (userEmail.isEmpty()) {
            mail.setError("Enter mail");
            mail.requestFocus();
        }else if (userPaswd.isEmpty()) {
            psw.setError("Enter Password");
            psw.requestFocus();
        }else if (userEmail.isEmpty() && userPaswd.isEmpty()) {
            Toast.makeText(AccountCreation.this, "Fields Empty!", Toast.LENGTH_SHORT).show();
        } else if (!(userEmail.isEmpty() && userPaswd.isEmpty())) {
            firebaseAuth.createUserWithEmailAndPassword(userEmail, userPaswd).addOnCompleteListener(AccountCreation.this, new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(AccountCreation.this.getApplicationContext(),
                                "SignUp unsuccessful: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                    else{
                        createUsersFireBase();
                        startActivity(new Intent(AccountCreation.this, Home.class));
                    }
                }
            });
        } else {
            Toast.makeText(AccountCreation.this, "Error", Toast.LENGTH_SHORT).show();
        }
    }
    private void createUsersFireBase(){
        if(this.getCurrentUser() != null){
            EditText firstName = (EditText) findViewById(R.id.prenom);
            EditText secondName = (EditText) findViewById(R.id.nom);
            EditText cityName = (EditText) findViewById(R.id.city);
            EditText passwordText = (EditText) findViewById(R.id.password);
            EditText mail = (EditText) findViewById(R.id.email);
            String userEmail = mail.getText().toString();
            String psw = passwordText.getText().toString();
            String urlPicture = (this.getCurrentUser().getPhotoUrl() != null) ? this.getCurrentUser().getPhotoUrl().toString() : null;
            String username =  this.getCurrentUser().getDisplayName();
            String uid = this.getCurrentUser().getUid();
            String city = cityName.getText().toString();
            String firstNameText = firstName.getText().toString();
            String secondNameText = secondName.getText().toString();
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location;
            final double longitude;
            final double latitude;
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
                location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                longitude = location.getLongitude();
                latitude = location.getLatitude();
            }
            else{
                longitude = 0.0;
                latitude = 0.0;
            }
            LatLng loc = new LatLng(latitude, longitude);
            Geocoder gcd = new Geocoder(AccountCreation.this, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = gcd.getFromLocation(latitude, longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addresses.size() > 0) {
                city = addresses.get(0).getLocality();
            }
            Log.d("mycity", city);
            SharedPreferences settings = getSharedPreferences("preferences",MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("First_Name",firstNameText);
            editor.putString("Last_Name",secondNameText);
            editor.putString("Pseudo",username);
            editor.putString("Password",psw);
            editor.putString("E_Mail",userEmail);
            editor.putString("City_Name",city);
            editor.putString("City_Coordinates",loc.toString());
            editor.putString("UID",uid);
            editor.putInt("Age",12);
            editor.commit();
            UserHelper.createUser(uid, username, urlPicture, "99", firstNameText, secondNameText,city).addOnFailureListener(this.onFailureListener());

        }
    }
}
