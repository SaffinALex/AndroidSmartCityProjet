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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.saffin.androidsmartcity.auth.BaseActivity;
import com.example.saffin.androidsmartcity.models.User;
import com.example.saffin.androidsmartcity.models.UserHelper;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public class Connexion extends BaseActivity {
    FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.isCurrentUserLogged()) {
            startActivity(new Intent(Connexion.this, Home.class));
        } else {
            setContentView(R.layout.activity_connexion);
        }
    }

    protected void onResume() {
        super.onResume();
        // 5 - Update UI when activity is resuming
        this.updateUIWhenResuming();
    }

    private void updateUIWhenResuming() {
        if (this.isCurrentUserLogged()) {
            this.startHomeActivity();
        }
    }

    private void startHomeActivity() {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }

    public String getUserId(){
        return this.getCurrentUser().getUid();
    }

    public void goHome(View v) throws IOException {
        Log.d("heycc", "coucou");
        firebaseAuth = FirebaseAuth.getInstance();
        EditText mail = (EditText) findViewById(R.id.email);
        final EditText psw = (EditText) findViewById(R.id.password);
        final String userEmail = mail.getText().toString();
        String userPaswd = psw.getText().toString();
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
        Log.d("heycc", "coucou");
        if (userEmail.isEmpty()) {
            mail.setError("Remplir Mail");
            mail.requestFocus();
        } else if (userPaswd.isEmpty()) {
            psw.setError("Remplir Password");
            psw.requestFocus();
        } else if (userEmail.isEmpty() && userPaswd.isEmpty()) {
            Toast.makeText(Connexion.this, R.string.emptyField, Toast.LENGTH_SHORT).show();
        } else if (!(userEmail.isEmpty() && userPaswd.isEmpty())) {
            firebaseAuth.signInWithEmailAndPassword(userEmail, userPaswd).addOnCompleteListener(Connexion.this, new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(Connexion.this, R.string.badUser, Toast.LENGTH_SHORT).show();
                    } else {
                        UserHelper.getUser(getUserId()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                User currentUser = documentSnapshot.toObject(User.class);
                                String city = TextUtils.isEmpty(currentUser.getAge()) ? getString(R.string.no_username) : currentUser.getCity();
                                LatLng loc = new LatLng(latitude, longitude);
                                Geocoder gcd = new Geocoder(Connexion.this, Locale.getDefault());
                                List<Address> addresses = null;
                                try {
                                    addresses = gcd.getFromLocation(latitude, longitude, 1);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if (addresses.size() > 0) {
                                    city = addresses.get(0).getLocality();
                                    Log.d("mycity", city);
                                }
                                Log.d("mycity", city);
                                SharedPreferences settings = getSharedPreferences("preferences", MODE_PRIVATE);
                                SharedPreferences.Editor editor = settings.edit();
                                String firstName = TextUtils.isEmpty(currentUser.getFirstName()) ? getString(R.string.no_username) : currentUser.getFirstName();
                                String age = TextUtils.isEmpty(currentUser.getAge()) ? getString(R.string.no_username) : currentUser.getAge();
                                String secondName = TextUtils.isEmpty(currentUser.getSecondName()) ? getString(R.string.no_username) : currentUser.getSecondName();
                                editor.putString("First_Name", firstName);
                                editor.putString("Last_Name", secondName);
                                editor.putString("Pseudo", "noUsername");
                                editor.putString("Password", "HiddenPsw");
                                editor.putString("E_Mail", getCurrentUser().getEmail());
                                editor.putString("City_Name", city);
                                editor.putString("City_Coordinates", loc.toString());
                                editor.putString("UID", getCurrentUser().getUid());
                                editor.putInt("Age", Integer.parseInt(age));
                                editor.commit();
                                startActivity(new Intent(Connexion.this, Home.class));
                            }
                        });
                    }
                }
            });
        } else {
            Toast.makeText(Connexion.this, "Error", Toast.LENGTH_SHORT).show();
        }
    }

    public void onForgetPassword(View v){
        EditText mail = (EditText)findViewById(R.id.email);
        String userEmail = mail.getText().toString();
        if(!userEmail.isEmpty()) {
            FirebaseAuth.getInstance().sendPasswordResetEmail(userEmail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Log.d("ici","coucou");
                                Toast.makeText(Connexion.this, "Email envoyé", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(Connexion.this, "Pas de mail", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
