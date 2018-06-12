package com.example.artem.android_diphloma;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by artem on 23.03.2018.
 */

public class Adapter_Components extends BaseAdapter implements Filterable {

    Context context;
    Fragment fragment;
    ArrayList<Component_Firebase> components;
    ArrayList<Component_Firebase> filterList;
    CustomFilterComponents customFilterComponents;

    LayoutInflater layoutInflater;
    Preferences_Singleton preferences_singleton = Preferences_Singleton.getInstance();

    public Adapter_Components(Context context, ArrayList<Component_Firebase> components, Fragment fragment) {
        this.context = context;
        this.components = components;
        this.filterList = this.components;
        this.fragment = fragment;
    }

    @Override
    public int getCount() {
        return components.size();
    }

    @Override
    public Object getItem(int position) {
        return components.get(position);
    }

    @Override
    public long getItemId(int position) {
        return components.indexOf(components.get(position));
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null)
            convertView = layoutInflater.inflate(R.layout.row_component, null);

        TextView title =  (TextView) convertView.findViewById(R.id.titleCompTextView);
        TextView colorsCount =  (TextView) convertView.findViewById(R.id.colorsCountTextView);
        TextView densitiesCount =  (TextView) convertView.findViewById(R.id.densityTextView);
        TextView price = (TextView) convertView.findViewById(R.id.price);

        title.setText(components.get(position).title);
        colorsCount.setText("Colors = " + Integer.toString(components.get(position).colors.size()) );
        densitiesCount.setText("Densities = " + Integer.toString(components.get(position).densities.size()) );
        price.setText("Price = " + Double.toString(components.get(position).price));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fragment != null)
                    ((ProductsFragment)fragment).ShowSelectedComponent(components.get(position));
            }
        });

        return convertView;
    }

    @Override
    public Filter getFilter() {

        if(customFilterComponents == null)
            customFilterComponents = new CustomFilterComponents();

        return customFilterComponents;
    }

    class CustomFilterComponents extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults filterResults = new FilterResults();

            if(constraint != null && constraint.length() > 0){
                constraint  = constraint.toString().toUpperCase();

                ArrayList<Component_Firebase> user_firebaseListFiltered = new ArrayList<>();

                for (int i = 0; i< filterList.size(); i++)
                    if(filterList.get(i).title.toUpperCase().contains(constraint))
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
            components = (ArrayList<Component_Firebase>) results.values;
            notifyDataSetChanged();
        }
    }
}
