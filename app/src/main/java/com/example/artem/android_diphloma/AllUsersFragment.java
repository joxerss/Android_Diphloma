package com.example.artem.android_diphloma;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.security.Key;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * Created by artem on 07.03.2018.
 */

public class AllUsersFragment extends Fragment {

    View view;
    AllUsersFragment allUsersFragment = this;
    Preferences_Singleton preferences_singleton = Preferences_Singleton.getInstance();
    FirebaseDatabase database;
    DatabaseReference databaseReference;

    GridView gridViewAllUsers;
    Adapter_AllUsers adapterAllUsers;
    ArrayList<User_Firebase> linesUsers;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_allusers, container, false);
        setHasOptionsMenu(true);//Make sure you have this line of code.

        gridViewAllUsers = view.findViewById(R.id.grid_view_users);

        database = preferences_singleton.database;
        databaseReference = preferences_singleton.databaseReference;
        this.ReadUsers();


        return view;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    // Подключение меню к активности (декларативное или программное)
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_all_users, menu);
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem item = menu.findItem(R.id.menu_action_search);
        SearchView searchView = (SearchView)item.getActionView();
        searchView.setQueryHint("Search by email or company");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //adapter.getFilter().filter(query);

                Toast.makeText(view.getContext(), "onQueryTextSubmit", Toast.LENGTH_SHORT).show();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                Toast.makeText(view.getContext(), "onQueryTextChange", Toast.LENGTH_SHORT).show();
                adapterAllUsers.getFilter().filter(newText);
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener(){

            @Override
            public boolean onClose()
            {
                adapterAllUsers.notifyDataSetChanged();
                gridViewAllUsers.invalidate();
                return false;
            }
        });
        //return  true;
    }

    public  void ReadUsers(){
        databaseReference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                linesUsers = new ArrayList<>();
                //User user = dataSnapshot.getValue(User.class);
                HashMap<String, Object> snapshot = (HashMap<String,Object>) dataSnapshot.getValue();
                //String uid = snapshot.keySet().toArray()[0].toString();
                for ( String key : snapshot.keySet() ) {
                    HashMap<String, String> line = (HashMap<String, String>)snapshot.get(key);
                    String s = line.get("photoLink");
                    User_Firebase userFirebase = new User_Firebase(key, line.get("displayName"), line.get("email"),
                            line.get("phone"), line.get("photoLink"), line.get("company"), line.get("companyAddress"), false);

                    if(!linesUsers.contains(userFirebase)) {
                        userFirebase.allUsersFragment = allUsersFragment;
                        linesUsers.add(userFirebase); //all users
                    }

                }
                CheckAdmins();
                //UserRecord userRecord = FirebaseAuth.getInstance().getUserAsync(uid).get();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }//read users

    public  void CheckAdmins(){
        if(linesUsers.isEmpty()) return;

        databaseReference.child("admins").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //User user = dataSnapshot.getValue(User.class);
                HashMap<String, Object> snapshot = (HashMap<String,Object>) dataSnapshot.getValue();
                if(snapshot != null && snapshot.get(preferences_singleton.user.getUid()) != null) {
                    //if string isn't null check user status
                    for (User_Firebase user : linesUsers)
                        if(snapshot.get(user.uid)!=null)
                            user.isAdmin = Boolean.valueOf(snapshot.get(user.uid).toString());
                        else
                            user.isAdmin = false;

                    //RenderAllUsers();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
    }

    public  void RenderAllUsers(){
        if(linesUsers.isEmpty()) return;


        adapterAllUsers = new Adapter_AllUsers(this.getContext(), linesUsers, allUsersFragment);

        gridViewAllUsers.setAdapter(adapterAllUsers);

        adapterAllUsers.notifyDataSetChanged();
        gridViewAllUsers.invalidate();
        //((BaseAdapter) mMyListView.getAdapter()).notifyDataSetChanged();

        //Log.d("RENDER", "RENDER USERS");
        //gridViewAllUsers.setBackgroundColor(Color.parseColor("#ff0000"));

    }

    public void invalidateCustom(){
        if(gridViewAllUsers == null &&  linesUsers.size() > 0) return;


        if(adapterAllUsers == null) {
            User_Firebase[] mUsersArray = new User_Firebase[linesUsers.size()];
            mUsersArray = linesUsers.toArray(mUsersArray);
            adapterAllUsers = new Adapter_AllUsers(this.getContext(), linesUsers, allUsersFragment);
            gridViewAllUsers.setAdapter(adapterAllUsers);
        }

        adapterAllUsers.notifyDataSetChanged();
        gridViewAllUsers.invalidate();

    }

    public void ShowSelectedUserInfo(User_Firebase user_firebase){
        User_Firebase templUser = new User_Firebase();
        templUser.email = user_firebase.email;
        //templUser.photoBMP = user_firebase.photoBMP;
        templUser.phone = user_firebase.phone;
        templUser.company = user_firebase.company;
        templUser.companyAddress = user_firebase.companyAddress;
        templUser.isAdmin = user_firebase.isAdmin;
        templUser.uid = user_firebase.uid;
        templUser.username = user_firebase.username;

        Intent intent = new Intent(getContext(), AdminUserInfo.class);
        intent.putExtra("admin_user_info", templUser);
        intent.putExtra("admin_user_info_bmp", user_firebase.photoBMP);
        startActivity(intent);
    }
}


