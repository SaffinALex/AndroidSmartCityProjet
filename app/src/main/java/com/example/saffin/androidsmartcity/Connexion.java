package com.example.saffin.androidsmartcity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.saffin.androidsmartcity.auth.BaseActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;

import androidx.annotation.NonNull;

public class Connexion extends BaseActivity {
    FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(this.isCurrentUserLogged()) {
            startActivity(new Intent(Connexion.this, Home.class));
        }
        else{
            setContentView(R.layout.activity_connexion);
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

    public void goHome(View v) throws IOException {
        firebaseAuth = FirebaseAuth.getInstance();
        EditText mail = (EditText)findViewById(R.id.email);
        EditText psw = (EditText)findViewById(R.id.password);
        String userEmail = mail.getText().toString();
        String userPaswd = psw.getText().toString();
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
                        startActivity(new Intent(Connexion.this, Home.class));
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
                        }
                    });
        }
    }
}
