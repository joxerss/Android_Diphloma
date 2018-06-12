package com.example.artem.android_diphloma;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentActivity;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by artem on 09.03.2018.
 */

public class Adapter_AllUsers extends BaseAdapter implements Filterable {

    Context context;
    Fragment fragment;
    ArrayList<User_Firebase> users;
    ArrayList<User_Firebase> filterList;
    CustomFilterUsers customFilterUsers;

    LayoutInflater layoutInflater;
    Preferences_Singleton preferences_singleton = Preferences_Singleton.getInstance();

    public Adapter_AllUsers(Context context, ArrayList<User_Firebase> users, Fragment fragment) {
        this.context = context;
        this.users = users;
        this.filterList = this.users;
        this.fragment = fragment;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return users.indexOf(users.get(position));
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null)
            convertView = layoutInflater.inflate(R.layout.row_user, null);

        ImageView imageView = (ImageView) convertView.findViewById(R.id.usersInfoImg);
        TextView name =  (TextView) convertView.findViewById(R.id.usersInfoName);
        TextView email =  (TextView) convertView.findViewById(R.id.usersInfoEmail);
        Switch switchAdmin = (Switch) convertView.findViewById(R.id.switch_usersInfo);

        if(users.get(position).photoBMP != null) {
            Resources mResources = convertView.getResources();
            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(
                    mResources,
                    users.get(position).photoBMP
            );

            // Set the RoundedBitmapDrawable corners radius
            roundedBitmapDrawable.setCornerRadius(50.0f);
            roundedBitmapDrawable.setAntiAlias(true);

            // Set the ImageView image as drawable object
            imageView.setImageDrawable(roundedBitmapDrawable);
        }else
            imageView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.ic_account_circle_black_24dp));

        name.setText(users.get(position).username);
        email.setText(users.get(position).email);
        switchAdmin.setChecked(users.get(position).isAdmin);
        switchAdmin.setTag(users.get(position).uid);
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

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fragment != null)
                    //fragment.getFragmentManager().beginTransaction().replace(R.id.navigation_container, new AboutFragment()).commit();
                    ((AllUsersFragment)fragment).ShowSelectedUserInfo( users.get(position));
            }
        });

        return convertView;
    }

    @Override
    public Filter getFilter() {

        if(customFilterUsers == null)
            customFilterUsers = new CustomFilterUsers();

        return customFilterUsers;
    }

    class CustomFilterUsers extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults filterResults = new FilterResults();

            if(constraint != null && constraint.length() > 0){
                constraint  = constraint.toString().toUpperCase();

                ArrayList<User_Firebase> user_firebaseListFiltered = new ArrayList<>();

                for (int i = 0; i< filterList.size(); i++)
                    if(filterList.get(i).company != null && filterList.get(i).company.toUpperCase().contains(constraint) ||
                            filterList.get(i).email.toUpperCase().contains(constraint))
                        user_firebaseListFiltered.add(filterList.get(i));

                filterResults.count = user_firebaseListFiltered.size();
                filterResults.values = user_firebaseListFiltered;
            }else {
                filterResults.count = filterList.size();
                filterResults.values = filterList;
            }

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            users = (ArrayList<User_Firebase>) results.values;
            notifyDataSetChanged();
        }
    }
}
