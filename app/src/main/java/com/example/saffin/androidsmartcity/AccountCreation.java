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
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class AccountCreation extends BaseActivity {
    FirebaseAuth firebaseAuth;
    // 1 - STATIC DATA FOR PICTURE
    private static final String PERMS = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final int RC_IMAGE_PERMS = 100;
    private Uri uriImageSelected;
    private static final int RC_CHOOSE_PHOTO = 200;
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
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 2 - Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 6 - Calling the appropriate method after activity result
        this.handleResponse(requestCode, resultCode, data);
    }
    @AfterPermissionGranted(RC_IMAGE_PERMS)
    public void onClickAddFile(View v) { this.chooseImageFromPhone(); }
    // --------------------
    // FILE MANAGEMENT
    // --------------------

    private void chooseImageFromPhone(){
        if (!EasyPermissions.hasPermissions(this, PERMS)) {
            EasyPermissions.requestPermissions(this, "Demande d'accés", RC_IMAGE_PERMS, PERMS);
            return;
        }
        // 3 - Launch an "Selection Image" Activity
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RC_CHOOSE_PHOTO);
    }

    // 4 - Handle activity response (after user has chosen or not a picture)
    private void handleResponse(int requestCode, int resultCode, Intent data){
        if (requestCode == RC_CHOOSE_PHOTO) {
            if (resultCode == RESULT_OK) { //SUCCESS
                this.uriImageSelected = data.getData();
                ImageView imageViewProfile = findViewById(R.id.imageView2);
                Glide.with(this) //SHOWING PREVIEW OF IMAGE
                        .load(this.uriImageSelected)
                        .into(imageViewProfile);
            } else {
                Toast.makeText(this, "Pas d'image choisis", Toast.LENGTH_SHORT).show();
            }
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
                        finish();
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
