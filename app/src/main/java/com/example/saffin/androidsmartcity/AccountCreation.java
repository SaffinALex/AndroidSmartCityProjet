package com.example.saffin.androidsmartcity;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.saffin.androidsmartcity.auth.BaseActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;

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
        EditText mail = (EditText)findViewById(R.id.email);
        EditText psw = (EditText)findViewById(R.id.password);
        String userEmail = mail.getText().toString();
        String userPaswd = psw.getText().toString();
        if (userEmail.isEmpty()) {
            mail.setError("m");
            mail.requestFocus();
        } else if (userPaswd.isEmpty()) {
            psw.setError("s");
            psw.requestFocus();
        } else if (userEmail.isEmpty() && userPaswd.isEmpty()) {
            Toast.makeText(AccountCreation.this, "Fields Empty!", Toast.LENGTH_SHORT).show();
        } else if (!(userEmail.isEmpty() && userPaswd.isEmpty())) {
            firebaseAuth.createUserWithEmailAndPassword(userEmail, userPaswd).addOnCompleteListener(AccountCreation.this, new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                    if (!task.isSuccessful()) {
                        Toast.makeText(AccountCreation.this.getApplicationContext(),
                                "SignUp unsuccessful: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        startActivity(new Intent(AccountCreation.this, Connexion.class));
                    }
                }
            });
        } else {
            Toast.makeText(AccountCreation.this, "Error", Toast.LENGTH_SHORT).show();
        }
    }
}
