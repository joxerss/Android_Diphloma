package com.example.artem.android_diphloma;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.design.widget.Snackbar;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG_Email = "EmailPassword";
    private static final String TAG_Google = "GoogleLogIn";
    private static final int RC_GOOGLE_SIGN_IN = 9001;
    private static final int RC_GOOGLE_SIGN_LOGOUT = 8001;

    Activity activity_login;
    Preferences_Singleton preferences_singleton = Preferences_Singleton.getInstance();

    private TextView mStatusTextView;
    private TextView mDetailTextView;
    private EditText mEmailField;
    private EditText mPasswordField;

    //google sign In authenticator
    private GoogleSignInClient mGoogleSignInClient;

    // [START declare_auth]
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.setTitle("Welcome");
        activity_login = this;

        // Views
        mStatusTextView = findViewById(R.id.status);
        mDetailTextView = findViewById(R.id.detail);
        mEmailField = findViewById(R.id.field_email);
        mPasswordField = findViewById(R.id.field_password);

        // Buttons
        findViewById(R.id.email_sign_in_button).setOnClickListener(this);
        findViewById(R.id.email_create_account_button).setOnClickListener(this);
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.verify_email_button).setOnClickListener(this);
        findViewById(R.id.reset_password_button).setOnClickListener(this);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        if(preferences_singleton.isFirstStart()) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            preferences_singleton.setFirstStart(false);
            updateUI(currentUser);

        }

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.email_create_account_button) {
            createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.email_sign_in_button) {
            signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } if (i == R.id.sign_in_button) { //google button
            signIn();
        }else if (i == R.id.sign_out_button) {
            signOut();
        }else if (i == R.id.verify_email_button) {
            sendEmailVerification();
        }else if (i == R.id.reset_password_button){
            resetPassword();
        }
    }

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
            mStatusTextView.setText(getString(R.string.emailpassword_status_fmt,
                    user.getEmail(), user.isEmailVerified()));
            mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));

            findViewById(R.id.sign_in_button).setEnabled(false);
            findViewById(R.id.email_sign_in_button).setEnabled(false);
            findViewById(R.id.email_create_account_button).setEnabled(false);
            findViewById(R.id.sign_out_button).setEnabled(true);

            if(user.isEmailVerified())
                findViewById(R.id.verify_email_button).setEnabled(false);
            else
                findViewById(R.id.verify_email_button).setEnabled(true);

            preferences_singleton.user = user;
            unLockButtons();
            MoveToNextActivity(preferences_singleton.WRITE_USER_INFO);
        } else {
            mStatusTextView.setText(R.string.signed_out);
            mDetailTextView.setText(null);

            findViewById(R.id.sign_in_button).setEnabled(true);
            findViewById(R.id.email_sign_in_button).setEnabled(true);
            findViewById(R.id.email_create_account_button).setEnabled(true);
            findViewById(R.id.sign_out_button).setEnabled(false);
            findViewById(R.id.verify_email_button).setEnabled(false);

            preferences_singleton.user = null;
            unLockButtons();
        }
    }


    // login by email and password
    private boolean validateForm(boolean passFlag) {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        if(passFlag) {
            String password = mPasswordField.getText().toString();
            if (TextUtils.isEmpty(password)) {
                mPasswordField.setError("Required.");
                valid = false;
            } else {
                mPasswordField.setError(null);
            }
        }

        return valid;
    }

    private void createAccount(String email, String password) {
        Log.d(TAG_Email, "createAccount:" + email);
        if (!validateForm(true)) {
            return;
        }

        showProgressDialog();
        LockButtons();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG_Email, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG_Email, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(activity_login, "Create user with email failed.\n" + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }

    private void signIn(String email, String password) {
        Log.d(TAG_Email, "signIn:" + email);
        if (!validateForm(true)) {
            return;
        }

        showProgressDialog();
        LockButtons();

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG_Email, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG_Email, "signInWithEmail:failure", task.getException());
                            Toast.makeText(activity_login, "Authentication failed.\n" + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
                            mStatusTextView.setText(R.string.auth_failed);
                        }
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });

        //updateUI(null);
    }

    private void sendEmailVerification() {
        // Disable button
        findViewById(R.id.verify_email_button).setEnabled(false);

        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        // Re-enable button
                        findViewById(R.id.verify_email_button).setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(activity_login,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG_Email, "sendEmailVerification", task.getException());
                            Toast.makeText(activity_login,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END send_email_verification]
    }

    private void resetPassword(){

        if(!validateForm(false) || mEmailField.getText().toString().length() < 6){
            Toast.makeText(this, "Check email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.sendPasswordResetEmail(mEmailField.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG_Email, "Email sent.");
                            Toast.makeText(activity_login, "Email sent.", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(activity_login, task.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Firebase Authentication using a Google ID Token.
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
    }

    //get account from default chooser of accounts
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG_Google, "Google sign in failed", e);
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        } else if(requestCode == RC_GOOGLE_SIGN_LOGOUT){
            signOut();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG_Google, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        showProgressDialog();
        // [END_EXCLUDE]


        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG_Google, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG_Google, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }

    void MoveToNextActivity(int key){
        // Создать намерение, которое показывает, какую активность вызвать
        // и содержит необходимые параметры
        Intent intent = new Intent(this, NavigationActivity.class);
        intent.putExtra("type_of_action_with_user", key);
        // Старт активности без возврата результата
        startActivity(intent);
        finish();
    }

    void LockButtons(){
        findViewById(R.id.email_sign_in_button).setEnabled(false);
        findViewById(R.id.email_create_account_button).setEnabled(false);
        findViewById(R.id.sign_in_button).setEnabled(false);
        findViewById(R.id.sign_out_button).setEnabled(false);
        findViewById(R.id.verify_email_button).setEnabled(false);
        findViewById(R.id.reset_password_button).setEnabled(false);
    }

    void unLockButtons(){
        findViewById(R.id.email_sign_in_button).setEnabled(true);
        findViewById(R.id.email_create_account_button).setEnabled(true);
        findViewById(R.id.sign_in_button).setEnabled(true);
        findViewById(R.id.sign_out_button).setEnabled(true);
        findViewById(R.id.verify_email_button).setEnabled(true);
        findViewById(R.id.reset_password_button).setEnabled(true);
    }
}
