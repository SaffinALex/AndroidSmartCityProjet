package com.example.saffin.androidsmartcity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.saffin.androidsmartcity.auth.BaseActivity;
import com.example.saffin.androidsmartcity.models.UserHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;

import androidx.annotation.NonNull;

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
        EditText mail = (EditText) findViewById(R.id.email);
        EditText psw = (EditText) findViewById(R.id.password);
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
            String urlPicture = (this.getCurrentUser().getPhotoUrl() != null) ? this.getCurrentUser().getPhotoUrl().toString() : null;
            String username =  this.getCurrentUser().getDisplayName();
            String uid = this.getCurrentUser().getUid();
            String city = cityName.getText().toString();
            String firstNameText = firstName.getText().toString();
            String secondNameText = secondName.getText().toString();
            UserHelper.createUser(uid, username, urlPicture, "99", firstNameText, secondNameText,city).addOnFailureListener(this.onFailureListener());

        }
    }
}
