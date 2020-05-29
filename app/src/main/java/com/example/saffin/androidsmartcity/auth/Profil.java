package com.example.saffin.androidsmartcity.auth;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.saffin.androidsmartcity.MainActivity;
import com.example.saffin.androidsmartcity.ProfilEdit;
import com.example.saffin.androidsmartcity.R;
import com.example.saffin.androidsmartcity.models.User;
import com.example.saffin.androidsmartcity.models.UserHelper;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import androidx.appcompat.app.AlertDialog;

public class Profil extends BaseActivity {
    private static final int SIGN_OUT_TASK = 10;
    private static final int DELETE_USER_TASK = 20;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //text.setVisibility(View.GONE);
        //editText.setVisibility(View.VISIBLE);
        //this.configureToolbar();
        if(this.isCurrentUserLogged()) {
            setContentView(getFragmentLayout());
            this.updateUIWhenCreating();
        }
        else{
            goMainActivity();
        }
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
            // 7 - Get additional data from Firestore (isMentor & Username)
            UserHelper.getUser(this.getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User currentUser = documentSnapshot.toObject(User.class);
                    String firstName = TextUtils.isEmpty(currentUser.getFirstName()) ? getString(R.string.no_username) : currentUser.getFirstName();
                    String age = TextUtils.isEmpty(currentUser.getAge()) ? getString(R.string.no_username) : currentUser.getAge();
                    String city = TextUtils.isEmpty(currentUser.getAge()) ? getString(R.string.no_username) : currentUser.getCity();
                    String secondName = TextUtils.isEmpty(currentUser.getSecondName()) ? getString(R.string.no_username) : currentUser.getSecondName();
                    TextView textInputFirstName = (TextView)findViewById(R.id.editSecondName);
                    TextView textInputSecondName = (TextView)findViewById(R.id.editFirstName);
                    TextView textInputAge = (TextView)findViewById(R.id.textAge);
                    TextView textInputCity = (TextView)findViewById(R.id.editCity);
                    textInputCity.setText(city);
                    textInputAge.setText(age);
                    textInputFirstName.setText(firstName);
                    textInputSecondName.setText(secondName);
                }
            });
            //Update views with data
            //TextView textInputEditTextUsername = (TextView)findViewById(R.id.editText2);
            TextView textViewEmail = (TextView)findViewById(R.id.editMail);
            // textInputEditTextUsername.setText(username);
            textViewEmail.setText(email);
        }
    }
    // --------------------
    // ACTIONS
    // --------------------

    public void onClickUpdateButton(View v) {
        TextView age = (TextView)findViewById(R.id.textAge);
        TextView mail = (TextView)findViewById(R.id.editMail);
        TextView nom = (TextView)findViewById(R.id.editSecondName);
        TextView prenom = (TextView)findViewById(R.id.editFirstName);
        TextView ville = (TextView)findViewById(R.id.editCity);

        Intent intent = new Intent(Profil.this, ProfilEdit.class);
        //startActivity(intent);
        intent.putExtra("Age",age.getText().toString());
        intent.putExtra("Mail",mail.getText().toString());
        intent.putExtra("Nom",nom.getText().toString());
        intent.putExtra("Prenom",prenom.getText().toString());
        intent.putExtra("City",ville.getText().toString());
        finish();
        startActivityForResult(intent, 1);
    }

    public void onClickSignOutButton(View v) {
        //UserHelper.deleteUser(this.getCurrentUser().getUid()).addOnFailureListener(this.onFailureListener());
        this.signOutUserFromFirebase();
    }
    public void goMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        finish();
        startActivity(intent);
    }
    public void onClickDeleteButton(View v) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.delete_message_account)
                .setPositiveButton(R.string.choice_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteUserFromFirebase();
                     //   goMainActivity();

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
                        goMainActivity();
                        finish();
                        break;
                    case DELETE_USER_TASK:
                        goMainActivity();
                        finish();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private void signOutUserFromFirebase(){
        FirebaseAuth.getInstance()
                .signOut();
        goMainActivity();
    }


    private void deleteUserFromFirebase(){
        if (this.getCurrentUser() != null) {
            FirebaseAuth.getInstance().getCurrentUser()
                    .delete();
            FirebaseAuth.getInstance()
                    .signOut();
            goMainActivity();
        }
    }
}
