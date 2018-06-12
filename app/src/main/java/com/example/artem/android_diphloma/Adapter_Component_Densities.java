package com.example.artem.android_diphloma;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class Adapter_Component_Densities extends BaseAdapter {

    Context context;
    ArrayList<Double> densities;
    Activity activity;

    LayoutInflater layoutInflater;
    Preferences_Singleton preferences_singleton = Preferences_Singleton.getInstance();

    public Adapter_Component_Densities(Context context, ArrayList<Double> densities,  Activity activity){
        this.densities = densities;
        this.context = context;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return densities.size();
    }

    @Override
    public Object getItem(int position) {
        return densities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return densities.indexOf(densities.get(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null)
            convertView = layoutInflater.inflate(R.layout.row_component_densities, null);
        TextView density =  convertView.findViewById(R.id.density);
        density.setText(Double.toString( densities.get(position)));

        return convertView;
    }
}
