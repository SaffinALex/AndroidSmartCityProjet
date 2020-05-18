package com.example.saffin.androidsmartcity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.saffin.androidsmartcity.auth.BaseActivity;
import com.example.saffin.androidsmartcity.auth.Profil;
import com.example.saffin.androidsmartcity.models.UserHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ProfilEdit extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_edit);
        Intent i = getIntent();

        EditText age = findViewById(R.id.editAge);
        if (i.hasExtra("Age")) {
            age.setText(i.getStringExtra("Age"));
        }
        EditText nom = findViewById(R.id.editFirstName);
        if (i.hasExtra("Nom")) {
            nom.setText(i.getStringExtra("Nom"));
        }
        EditText prenom = findViewById(R.id.editSecondName);
        if (i.hasExtra("Prenom")) {
            prenom.setText(i.getStringExtra("Prenom"));
        }
        EditText mail = findViewById(R.id.editMail);
        if (i.hasExtra("Mail")) {
            mail.setText(i.getStringExtra("Mail"));
        }
    }

    public void onClickValidateChange(View v){
        EditText age = findViewById(R.id.editAge);
        String a = age.getText().toString();
        EditText nom = findViewById(R.id.editFirstName);
        String firstName = nom.getText().toString();
        EditText prenom = findViewById(R.id.editSecondName);
        String secondName = prenom.getText().toString();
        EditText mail = findViewById(R.id.editMail);
        String m = mail.getText().toString();
        UserHelper.updateAge(a,this.getCurrentUser().getUid());
        UserHelper.updateFirstName(firstName,this.getCurrentUser().getUid());
        UserHelper.updateSecondName(secondName,this.getCurrentUser().getUid());
        this.getCurrentUser().updateEmail(m);
        Intent intent = new Intent(this, Profil.class);
        startActivity(intent);
    }
}
