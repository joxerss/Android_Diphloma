package com.example.artem.android_diphloma;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

/**
 * Created by artem on 26.02.2018.
 */

class Preferences_Singleton {
    private static final Preferences_Singleton ourInstance = new Preferences_Singleton();

    final int RESULT_SET_USER_PHOTO = 1001;
    final int RESULT_STORAGE_PERMISSIONS = 1000;

    boolean firstStart;

    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference databaseReference;

    User_Firebase user_firebaseInfo; // information about current user in db
    public NavigationActivity navigationActivity;

    public static final int SINGLE_OUT = 0;
    public static final int WRITE_USER_INFO = 1;
    public static final int UPDATE_USER_INFO = 2;

    static Preferences_Singleton getInstance() {
        return ourInstance;
    }

    private Preferences_Singleton() {

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getInstance().getReference();
        firstStart = true;
    }

    public boolean isFirstStart(){
        return firstStart;
    }

    public void setFirstStart(boolean flag){
        firstStart = flag;
    }

    public  void CheckAdmin(){
        if(user == null || user_firebaseInfo == null) return;

        databaseReference.child("admins").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //User user = dataSnapshot.getValue(User.class);
                HashMap<String, Object> snapshot = (HashMap<String,Object>) dataSnapshot.getValue();
                if(snapshot != null && snapshot.get(user.getUid()) != null) {
                    //if string isn't null check user status
                    ourInstance.user_firebaseInfo.isAdmin = Boolean.valueOf(snapshot.get(user.getUid()).toString()); ;

                    //RenderAllUsers();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
    }
}


