package com.example.artem.android_diphloma;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.File;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class NavigationActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Preferences_Singleton preferences_singleton = Preferences_Singleton.getInstance();
    NavigationView navigationView;
    View hView;

    FirebaseDatabase database;
    DatabaseReference databaseReference;

    TextView userEmail;
    TextView userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getInstance().getReference();
        //database.setPersistenceEnabled(true);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        preferences_singleton.navigationActivity = this;

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // При старте активности получить параметры из намерения
        Intent intent = getIntent();
        int actionInt = intent.getIntExtra("type_of_action_with_user",-1);

        if(actionInt == preferences_singleton.WRITE_USER_INFO)
            NewUser();
        else if(actionInt == preferences_singleton.UPDATE_USER_INFO)
            UpdateUser();
            //Toast.makeText(this, "UPDATE USER INFO", Toast.LENGTH_SHORT).show();


        //show information about user in navigation controller menu
        hView =  navigationView.getHeaderView(0);
        //hView.setBackgroundResource(R.drawable.agency);
        hView.setBackgroundResource(R.drawable.background_five_colors);

        userEmail = hView.findViewById(R.id.userEmail);
        userName = hView.findViewById(R.id.userName);


        //move to orders
        //onNavigationItemSelected(navigationView.getMenu().getItem(4));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager = getFragmentManager();

        if (id == R.id.nav_profile) {
            /*Bundle bundle = new Bundle();
            bundle.putString("string1", "string1");
            bundle.putInt("string2", 111);

            AccauntInfoFragment accauntInfoFragment = new AccauntInfoFragment();
            accauntInfoFragment.setArguments(bundle);*/
            this.setTitle("My account");
            fragmentManager.beginTransaction().replace(R.id.navigation_container, new AccauntInfoFragment()).commit();
        } else if (id == R.id.nav_manage) {
            this.setTitle("All users");
            fragmentManager.beginTransaction().replace(R.id.navigation_container, new AllUsersFragment()).commit();
            //Toast.makeText(this, "nav_manage", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_products) {
            this.setTitle("Products settings");
            fragmentManager.beginTransaction().replace(R.id.navigation_container, new ProductsFragment()).commit();
            //Toast.makeText(this, "nav_products", Toast.LENGTH_SHORT).show();
        }else if (id == R.id.nav_statistics) {
            this.setTitle("Statistics");
            Toast.makeText(this, "nav_statistics", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_order) {
            this.setTitle("New order");
            fragmentManager.beginTransaction().replace(R.id.navigation_container, new NewOrdersFragment()).commit();
            //Toast.makeText(this, "nav_order", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_orders_history) {
            this.setTitle("Orders history");
            Toast.makeText(this, "nav_orders_history", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_info){
            //Intent intent = new Intent(this, AboutCompanyActivity.class);
            // Старт активности без возврата результата
            //startActivity(intent);
            // Create new fragment and transaction
            this.setTitle("About company");
            fragmentManager.beginTransaction().replace(R.id.navigation_container, new AboutFragment()).commit();
        } else if (id == R.id.nav_logout){
            showProgressDialog();
            // Configure Google Sign In
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

            // [START initialize_auth]
            FirebaseAuth mAuth; mAuth = FirebaseAuth.getInstance();

            // Firebase sign out
            mAuth.signOut();

            // Google revoke access
            mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                    new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            preferences_singleton.user = null;
                        }
                    });

            // Создать намерение, которое показывает, какую активность вызвать
            // и содержит необходимые параметры
            Intent intent = new Intent(this, MainActivity.class);

            // Добавление параметров в намерение
            //intent.putExtra("test", edit1.getText().toString());

            // Старт активности без возврата результата
            startActivity(intent);

            // Старт активности с возвратом результата
            //startActivityForResult(intent, RC_GOOGLE_SIGN_LOGOUT);
            hideProgressDialog();
            finish();
        }
        else if (id == R.id.nav_faq)
        {
            this.setTitle("FAQ");
            fragmentManager.beginTransaction().replace(R.id.navigation_container, new FAQFragment()).commit();
            Toast.makeText(this, "FAQ", Toast.LENGTH_SHORT).show();
        }
        // navigation item selected

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void NewUser(){
        // Write a message to the database
        //databaseReference = database.getReference("message");
        //databaseReference.setValue("Hello, World!");
        //databaseReference.child("users").child(preferences_singleton.user.getUid()).setValue(preferences_singleton.user.getEmail());
        databaseReference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //User user = dataSnapshot.getValue(User.class);
                HashMap<String, Object> snapshot = (HashMap<String,Object>) dataSnapshot.getValue();
                //String uid = snapshot.keySet().toArray()[0].toString();
                boolean flag = false;
                if(snapshot != null)
                    for ( String key : snapshot.keySet() ) {
                        HashMap<String, String> line = (HashMap<String, String>)snapshot.get(key);
                        String email = line.get("email");
                        if(line.get("email").equals(preferences_singleton.user.getEmail())) {
                            flag = true;
                            preferences_singleton.user_firebaseInfo = new User_Firebase(key, line.get("displayName"), line.get("email"),
                                    line.get("phone"), line.get("photoLink"), line.get("company"), line.get("companyAddress"), false);
                            preferences_singleton.CheckAdmin();

                            if(preferences_singleton.user.getPhotoUrl() != null) {
                                SetName_Email();
                                LoadUserPhoto();
                            } else
                                UpdateNavigationInfo(preferences_singleton.user_firebaseInfo.email, preferences_singleton.user_firebaseInfo.username,
                                        preferences_singleton.user_firebaseInfo.photoBMP);
                            CheckAdmin();
                            break;
                        }//if
                    }//for

                if(!flag) {
                    HashMap<String, String> userInfo = new HashMap<>();
                    userInfo.put("email", preferences_singleton.user.getEmail());
                    userInfo.put("displayName", preferences_singleton.user.getDisplayName());
                    userInfo.put("phone", preferences_singleton.user.getPhoneNumber());
                    userInfo.put("company", null);
                    userInfo.put("companyAddress", null);
                    if (preferences_singleton.user.getPhotoUrl() != null)
                        userInfo.put("photoLink", preferences_singleton.user.getPhotoUrl().toString());
                    else
                        userInfo.put("photoLink", null); // if null will ERROR!!

                    String urlToImg = "";
                    if(preferences_singleton.user.getPhotoUrl() != null)
                        urlToImg = preferences_singleton.user.getPhotoUrl().toString();

                    preferences_singleton.user_firebaseInfo = new User_Firebase(preferences_singleton.user.getUid(), preferences_singleton.user.getDisplayName(),
                            preferences_singleton.user.getEmail(), preferences_singleton.user.getPhoneNumber(),
                            urlToImg, null, null, false);

                    databaseReference.child("users").child(preferences_singleton.user.getUid()).setValue(userInfo);

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    public void UpdateUser(){
        //Toast.makeText(this, "UPDATE USER INFO", Toast.LENGTH_LONG).show();
    }

    public void LoadUserPhoto(){
        try {
            AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
            asyncHttpClient.get(preferences_singleton.user.getPhotoUrl().toString(), new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(responseBody,0,responseBody.length);
                    //preferences_singleton.setUserImage(bmp);

                    //((ImageView)findViewById(R.id.imageUser)).setImageBitmap(bmp);
                    Resources mResources = getResources();
                    RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(
                            mResources,
                            bmp
                    );

                    // Set the RoundedBitmapDrawable corners radius
                    roundedBitmapDrawable.setCornerRadius(50.0f);
                    roundedBitmapDrawable.setAntiAlias(true);

                    // Set the ImageView image as drawable object
                    ((ImageView)findViewById(R.id.imageUser)).setImageDrawable(roundedBitmapDrawable);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    //Toast.makeText(null, "onFailure", Toast.LENGTH_SHORT).show();
                }
            });
        }catch(Exception ex){
            Log.d("LoadUserPhoto : ", ex.toString());
        }//catch
    }

    public void SetName_Email(){
        if(preferences_singleton.user_firebaseInfo.username != null)
            userName.setText(preferences_singleton.user_firebaseInfo.username);
        else {

            /*UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName("FirebaseUserName").build();
            preferences_singleton.user.updateProfile(profileUpdates);
            preferences_singleton.user = FirebaseAuth.getInstance().getCurrentUser();

            preferences_singleton.user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    ((TextView) navigationView.getHeaderView(0).findViewById(R.id.userName)).setText(preferences_singleton.user.getDisplayName());
                }
            });*/
        }

        if(preferences_singleton.user.getEmail() != null)
            userEmail.setText(preferences_singleton.user.getEmail().toString());
        //Toast.makeText(this, userEmail.getText(), Toast.LENGTH_SHORT).show();
        hView.invalidate();
    }

    public void CheckAdmin(){

        databaseReference.child("admins").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //User user = dataSnapshot.getValue(User.class);
                HashMap<String, Object> snapshot = (HashMap<String,Object>) dataSnapshot.getValue();
                if(snapshot != null && snapshot.get(preferences_singleton.user.getUid()) != null) {
                    //if string isn't null check user status
                    boolean admin = Boolean.valueOf(snapshot.get(preferences_singleton.user.getUid()).toString());
                    if (admin)
                        AdminOptionsStatus(true);
                    else
                        AdminOptionsStatus(false);

                } else{
                    //write new info about user to admin db
                    AdminOptionsStatus(false);
                    //HashMap<String, String> userAdminInfo = new HashMap<>();
                    //userAdminInfo.put("email", preferences_singleton.user.getEmail());
                    databaseReference.child("admins").child(preferences_singleton.user.getUid()).setValue("false");

                }

                //UserRecord userRecord = FirebaseAuth.getInstance().getUserAsync(uid).get();
                Log.d("READ DATABASE USERS", dataSnapshot.toString());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                AdminOptionsStatus(false);
            }
        });
    }

    public  void AdminOptionsStatus(boolean flag){
        navigationView.getMenu().findItem(R.id.nav_manage).setVisible(flag);
        navigationView.getMenu().findItem(R.id.nav_products).setVisible(flag);
        navigationView.getMenu().findItem(R.id.nav_statistics).setVisible(flag);

    }

    public void UpdateNavigationInfo(String email, String name, Bitmap img){
        ((TextView)hView.findViewById(R.id.userEmail)).setText(email);
        ((TextView)hView.findViewById(R.id.userName)).setText(name);
        ((ImageView)findViewById(R.id.imageUser)).setImageBitmap(img);
        hView.invalidate();
    }

    public void UpdateNavigationInfo(String email, String name, Drawable img){
        ((TextView)hView.findViewById(R.id.userEmail)).setText(email);
        ((TextView)hView.findViewById(R.id.userName)).setText(name);
        ((ImageView)findViewById(R.id.imageUser)).setImageDrawable(img);
        hView.invalidate();
    }

}


