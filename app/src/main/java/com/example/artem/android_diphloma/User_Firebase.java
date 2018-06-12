package com.example.artem.android_diphloma;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.Image;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.Serializable;

import cz.msebera.android.httpclient.Header;

/**
 * Created by artem on 07.03.2018.
 */

public class User_Firebase  implements Serializable {

    public String uid;
    public String username;
    public String email;
    public String phone;
    public String company;
    public String companyAddress;
    public Bitmap photoBMP;
    public boolean isAdmin;

    public AllUsersFragment allUsersFragment;

    public User_Firebase() {
    }

    public User_Firebase(String uid, String username, String email, String phone, String photoUrl, String company, String companyAddress, boolean isAdmin) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.company = company;
        this.companyAddress = companyAddress;
        this.isAdmin = isAdmin;

        if(photoUrl != null &&  photoUrl.length() > 0)
            LoadUserPhoto(photoUrl);
        else{

            photoBMP = Bitmap.createBitmap( 50, 50, Bitmap.Config.RGB_565);


            Paint paint = new Paint();
            paint.setARGB(255, 0,87,156);
            paint.setStyle(Paint.Style.FILL);
            Canvas canvas = new Canvas(photoBMP);
            canvas.drawPaint(paint);

            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setStrokeWidth(0.4f);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setTextSize(30);

            if(username != null && username.length() > 0)
                canvas.drawText(Character.toString(username.charAt(0)).toUpperCase(), 15, 35, paint);


        }

    }

    public void LoadUserPhoto(String photoUrl){
        try {
            AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
            asyncHttpClient.get(photoUrl, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    photoBMP = BitmapFactory.decodeByteArray(responseBody,0,responseBody.length);

                    if(allUsersFragment != null)
                        allUsersFragment.RenderAllUsers();

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    //Toast.makeText(null, "onFailure", Toast.LENGTH_SHORT).show();
                }
            });
        }catch(Exception ex){
            Log.d("LoadUserPhoto : ", ex.toString());
        }
    }

}
