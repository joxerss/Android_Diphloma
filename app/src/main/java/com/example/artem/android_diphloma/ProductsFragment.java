package com.example.artem.android_diphloma;

import android.app.Fragment;
import android.content.Intent;
import android.content.pm.ComponentInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by artem on 23.03.2018.
 */

public class ProductsFragment extends Fragment {

    View view;

    Preferences_Singleton preferences_singleton = Preferences_Singleton.getInstance();
    FirebaseDatabase database;
    DatabaseReference databaseReference;

    Fragment allProducts = this;


    GridView gridViewProductsComponents;

    //components
    Adapter_Components adapterAllComponents;
    ArrayList<Component_Firebase> linesAllComponents;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_products, container, false);
        setHasOptionsMenu(true);//Make sure you have this line of code.

        gridViewProductsComponents = view.findViewById(R.id.grid_view_products);

        database = preferences_singleton.database;
        databaseReference = preferences_singleton.databaseReference;

        this.ReadComponents();


        return view;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    // Подключение меню к активности (декларативное или программное)
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_producsts, menu);
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem item = menu.findItem(R.id.menu_action_search_products);
        SearchView searchView = (SearchView)item.getActionView();
        searchView.setQueryHint("Search by title");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //adapter.getFilter().filter(query);
                Toast.makeText(getContext(), "onQueryTextSubmit", Toast.LENGTH_SHORT).show();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                Toast.makeText(getContext(), "onQueryTextChange", Toast.LENGTH_SHORT).show();
                adapterAllComponents.getFilter().filter(newText);
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener(){

            @Override
            public boolean onClose()
            {
                adapterAllComponents.notifyDataSetChanged();
                gridViewProductsComponents.invalidate();
                return false;
            }
        });
        //return  true;
    }

    void ReadComponents(){
        databaseReference.child("components").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                linesAllComponents = new ArrayList<>();
                //User user = dataSnapshot.getValue(User.class);
                HashMap<String, Object> snapshot = (HashMap<String,Object>) dataSnapshot.getValue();
                //String uid = snapshot.keySet().toArray()[0].toString();
                for ( String key : snapshot.keySet() ) {
                    HashMap<String, Object> line = (HashMap<String, Object>)snapshot.get(key);
                    double price = Double.valueOf(line.get("price").toString());
                    Component_Firebase component_firebase = new Component_Firebase(key.toString(), (ArrayList<String>)line.get("colors"),
                            (ArrayList<String>)line.get("density"), price);

                    linesAllComponents.add(component_firebase);
                }

                //UserRecord userRecord = FirebaseAuth.getInstance().getUserAsync(uid).get();
                RenderAllComponents();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }//ReadProducts

    public  void RenderAllComponents(){
        if(linesAllComponents.isEmpty()) return;

        adapterAllComponents = new Adapter_Components(this.getContext(), linesAllComponents, allProducts);
        gridViewProductsComponents.setAdapter(adapterAllComponents);

        adapterAllComponents.notifyDataSetChanged();
        gridViewProductsComponents.invalidate();
        //((BaseAdapter) mMyListView.getAdapter()).notifyDataSetChanged();

        //Log.d("RENDER", "RENDER USERS");
        //gridViewAllUsers.setBackgroundColor(Color.parseColor("#ff0000"));
    }


    public void ShowSelectedComponent(Component_Firebase component_firebase){
        if(component_firebase == null) return;

        //Toast.makeText(getContext(), "ShowSelectedComponent", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getContext(), AdminCompInfo.class);
        intent.putExtra("admin_component_firebase", component_firebase);
        startActivity(intent);
    }
}
