package com.example.saffin.androidsmartcity;

        import android.content.Intent;
        import androidx.appcompat.app.AppCompatActivity;
        import android.os.Bundle;
        import androidx.appcompat.app.ActionBarDrawerToggle;

        import android.view.View;

        import com.example.saffin.androidsmartcity.auth.BaseActivity;
        import com.firebase.ui.auth.AuthUI;

        import java.io.IOException;
        import java.util.Arrays;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reception_screen);
    }
    protected void onResume() {
        super.onResume();
        // 5 - Update UI when activity is resuming
        this.updateUIWhenResuming();
    }

    //FOR DATA
    // 1 - Identifier for Sign-In Activity
    private static final int RC_SIGN_IN = 123;

    // --------------------
    // NAVIGATION
    // --------------------

    // 2 - Launch Sign-In Activity
    private void startSignInActivity(){
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(
                                Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                        new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))

                        .setIsSmartLockEnabled(false, true)
                        .build(),
                RC_SIGN_IN);
    }

    public void goIdentification(View v) throws IOException {
        if (this.isCurrentUserLogged()){
            this.startHomeActivity();
        } else {
            //this.startSignInActivity();
            Intent intent = new Intent(this, Connexion.class);
            startActivity(intent);
        }

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
    public void goCreateAccount(View v) throws IOException {
        Intent intent = new Intent(this, AccountCreation.class);
        startActivity(intent);
    }

}
