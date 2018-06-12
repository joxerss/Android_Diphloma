package com.example.artem.android_diphloma;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.Image;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

import java.io.Serializable;

public class AdminUserInfo extends AppCompatActivity {

    Preferences_Singleton preferences_singleton = Preferences_Singleton.getInstance();

    EditText name;
    EditText phone;
    EditText email;
    EditText company;
    EditText deliveryAddress;
    ImageView photo;
    String pathToNewImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_info);

        name = findViewById(R.id.nameEditText);
        phone = findViewById(R.id.phoneEditText);
        email = findViewById(R.id.emailEditText);
        company = findViewById(R.id.companyEditText);
        deliveryAddress = findViewById(R.id.del_addressEditText);
        photo = findViewById(R.id.userImageProfile);

        // При старте активности получить параметры из намерения
        Intent intent = getIntent();
        User_Firebase user_firebase = (User_Firebase) intent.getExtras().getSerializable("admin_user_info");

        if(user_firebase == null) return;

        name.setText(user_firebase.username);
        phone.setText(user_firebase.phone);
        email.setText(user_firebase.email);
        company.setText(user_firebase.company);
        deliveryAddress.setText(user_firebase.companyAddress);

        Bitmap bitmap = (Bitmap) intent.getParcelableExtra("admin_user_info_bmp");
        if(bitmap != null) {
            Resources mResources = getResources();
            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(
                    mResources,
                    bitmap
            );

            // Set the RoundedBitmapDrawable corners radius
            roundedBitmapDrawable.setCornerRadius(50.0f);
            roundedBitmapDrawable.setAntiAlias(true);
            ((ImageView) findViewById(R.id.userImageProfile)).setImageDrawable(roundedBitmapDrawable);
        }//if

        Switch switchAdmin = findViewById(R.id.switchAdminUserInfo);
        switchAdmin.setChecked(user_firebase.isAdmin);
        switchAdmin.setTag(user_firebase.uid);
        switchAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //change station on server
                //databaseReference.child("admins").child(preferences_singleton.user.getUid()).setValue("false");
                if(((Switch)v).isChecked())
                    preferences_singleton.databaseReference.child("admins").child(((Switch)v).getTag().toString()).setValue("true");
                else
                    preferences_singleton.databaseReference.child("admins").child(((Switch)v).getTag().toString()).setValue("false");
            }
        });
    }
}
