package com.example.artem.android_diphloma;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.admin.android_colorpicker.HSVColorPickerDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class AdminCompInfo extends AppCompatActivity implements HSVColorPickerDialog.OnColorSelectedListener, ColorPickerDialogListener {

    Preferences_Singleton preferences_singleton = Preferences_Singleton.getInstance();
    FirebaseDatabase database;
    DatabaseReference databaseReference;

    Component_Firebase component_firebase;
    boolean flagIsShow = true; // true it's density

    GridView gridView;

    ArrayList<Double> linesDensities;
    Adapter_Component_Densities adapterComponentDensities;

    //String selectedColor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_comp_info);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getInstance().getReference();

        gridView = findViewById(R.id.grid_view_comp_params);

        // При старте активности получить параметры из намерения
        Intent intent = getIntent();
        component_firebase = (Component_Firebase) intent.getExtras().getSerializable("admin_component_firebase");
        if(component_firebase == null) {
            Toast.makeText(this, "Component contains incorrect information.", Toast.LENGTH_SHORT).show();
            finish();
        }

        this.setTitle(component_firebase.title  + " : Density");
        this.ReadDensities();

    }

    // Подключение меню к активности (декларативное или программное)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_admin_comp_info, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.menu_comp_info_swap:
                if(flagIsShow) {
                    this.setTitle(component_firebase.title + " : Color");
                    flagIsShow = false;
                    RenderGrid();
                }else {
                    this.setTitle(component_firebase.title + " : Density");
                    flagIsShow = true;
                    RenderGrid();
                }
                RenderGrid();
                break;
            case R.id.menu_comp_info_add:
                addInfoByFlag();
                break;
        }
        return true;
    }

    void addInfoByFlag(){
        if(flagIsShow)
            AddDensity();
        else
            AddColor();
    }//if addInfoByFlag

    void AddDensity(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final EditText inputDencity = new EditText(this);
        inputDencity.setHint("Write dencity");

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(8,8, 8,8);
        inputDencity.setInputType(InputType.TYPE_CLASS_NUMBER);
        inputDencity.setLayoutParams(lp);


        // Add the buttons
        builder.setPositiveButton("Okey", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                //preferences_singleton.databaseReference.child("components").child(component_firebase.title)
                // .child("dencity").setValue(inputDencity.getText());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        // Set other dialog properties
        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.setTitle("Create new dencity to component");
        dialog.setView(inputDencity);
        dialog.show();
    }// AddDensity

    void AddColor(){
        //HSVColorPickerDialog cpd = new HSVColorPickerDialog(this, 0xFF0000FF, this);
        //cpd.setTitle( "Pick a color" );
        //cpd.show();
        ColorPickerDialog.newBuilder()
                .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                .setAllowPresets(true)
                .setDialogId(0)
                .setColor(Color.BLACK)
                .setShowAlphaSlider(true)
                .show(this);
    }// AddColor

    @Override
    public void onColorSelected(int dialogId, int color) {
        switch (dialogId) {
            case 0:
                // We got result from the dialog that is shown when clicking on the icon in the action bar.
                Toast.makeText(this, "Selected Color: #" + Integer.toHexString(color), Toast.LENGTH_SHORT).show();
                //selectedColor = Integer.toHexString(color);

                HashMap<String, String> colors = new HashMap<>();
                //userInfo.put("", "#" + Integer.toHexString(color));
                //preferences_singleton.databaseReference.child("components").child(component_firebase.title)
                // .child("colors").setValue(colors);
                break;
        }// switch
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }


    @Override
    public void colorSelected(Integer color) {
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(color);
    }

    public  void ReadDensities(){
        databaseReference.child("components").child(component_firebase.title).child("density").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                linesDensities = new ArrayList<>();
                //User user = dataSnapshot.getValue(User.class);
                ArrayList<String> snapshot = (ArrayList<String>) dataSnapshot.getValue();
                //String uid = snapshot.keySet().toArray()[0].toString();
                for ( Object value : snapshot)
                    linesDensities.add(Double.parseDouble(value.toString()) );

                RenderGrid();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }//read users

    public  void RenderGrid(){
        if(flagIsShow) {
            if (linesDensities.isEmpty()) return;

            adapterComponentDensities = new Adapter_Component_Densities(this, linesDensities, this);
            gridView.setAdapter(adapterComponentDensities);

            adapterComponentDensities.notifyDataSetChanged();
            gridView.invalidate();
            //((BaseAdapter) mMyListView.getAdapter()).notifyDataSetChanged();

            //Log.d("RENDER", "RENDER USERS");
            //gridViewAllUsers.setBackgroundColor(Color.parseColor("#ff0000"));
        }else {
            gridView.setAdapter(null);
            gridView.invalidate();
        }
    }
}
