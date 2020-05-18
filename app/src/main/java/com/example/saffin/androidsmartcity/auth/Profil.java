package com.example.saffin.androidsmartcity.auth;

import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.saffin.androidsmartcity.Home;
import com.example.saffin.androidsmartcity.MainActivity;
import com.example.saffin.androidsmartcity.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;

public class Profil extends BaseActivity {
    private static final int SIGN_OUT_TASK = 10;
    private static final int DELETE_USER_TASK = 20;

    private androidx.appcompat.widget.Toolbar toolbar;
    private Profil instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.instance = this;
        if(this.isCurrentUserLogged()) {
            setContentView(getFragmentLayout());
            this.updateUIWhenCreating();
        }
        else{
            goMainActivity();
        }

        toolbar = findViewById(R.id.toolbar3);
        this.configureToolBarProfil();


        toolbar.setNavigationIcon(R.drawable.ic_action_ad_black_mdpi);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(instance, Home.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!this.isCurrentUserLogged()){
            goMainActivity();
        }
    }

   /* private void showProfileFragment(){
        if (this.fragmentMenu == null) this.fragmentMenu = MenuFragment.newInstance();
        this.startTransactionFragment(this.fragmentMenu);
    }*/


    public int getFragmentLayout() { return R.layout.activity_profil; }

    private void updateUIWhenCreating(){

        if (this.getCurrentUser() != null){

            //Get picture URL from Firebase
            ImageView imageViewProfile = findViewById(R.id.imageView2);
            if (this.getCurrentUser().getPhotoUrl() != null) {
                Glide.with(this)
                        .load(this.getCurrentUser().getPhotoUrl())
                        .into(imageViewProfile);
            }

            //Get email & username from Firebase
            String email = TextUtils.isEmpty(this.getCurrentUser().getEmail()) ? getString(R.string.no_email) : this.getCurrentUser().getEmail();
            String username = TextUtils.isEmpty(this.getCurrentUser().getDisplayName()) ? getString(R.string.no_username) : this.getCurrentUser().getDisplayName();

            //Update views with data
            TextView textInputEditTextUsername = (TextView)findViewById(R.id.editText2);
            TextView textViewEmail = (TextView)findViewById(R.id.editText);
            textInputEditTextUsername.setText(username);
            textViewEmail.setText(email);
        }
    }
    // --------------------
    // ACTIONS
    // --------------------

    public void onClickUpdateButton(View v) { }

    public void onClickSignOutButton(View v) {
        this.signOutUserFromFirebase();
        goMainActivity();
    }
    public void goMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    public void onClickDeleteButton(View v) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.delete_message_account)
                .setPositiveButton(R.string.choice_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteUserFromFirebase();

                    }
                })
                .setNegativeButton(R.string.choice_no, null)
                .show();
    }
    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin){
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                switch (origin){
                    case SIGN_OUT_TASK:
                        finish();
                        break;
                    case DELETE_USER_TASK:
                        finish();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private void signOutUserFromFirebase(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK));
    }


    private void deleteUserFromFirebase(){
        if (this.getCurrentUser() != null) {
            AuthUI.getInstance()
                    .delete(this)
                    .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(DELETE_USER_TASK));
            goMainActivity();
        }
    }

    // 1 - Configure Toolbar
    private void configureToolBarProfil(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }
}
