package com.example.artem.android_diphloma;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by artem on 11.03.2018.
 */

public class NewOrdersFragment extends Fragment implements View.OnClickListener {

    View view;
    Preferences_Singleton preferences_singleton = Preferences_Singleton.getInstance();
    FirebaseDatabase database;
    DatabaseReference databaseReference;


    private CardView business_cards, flyers, envelope, pen, company_diaries;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_orders, container, false);
        setHasOptionsMenu(true);//Make sure you have this line of code.

        business_cards = (CardView)view.findViewById(R.id.business_cards);
        flyers = (CardView)view.findViewById(R.id.flyers);
        envelope = (CardView)view.findViewById(R.id. envelope);
        pen = (CardView)view.findViewById(R.id.pen);
        company_diaries = (CardView)view.findViewById(R.id.company_diaries);

        database = preferences_singleton.database;
        databaseReference = preferences_singleton.databaseReference;

        business_cards.setOnClickListener(this);
        flyers.setOnClickListener(this);
        envelope.setOnClickListener(this);
        pen.setOnClickListener(this);
        company_diaries.setOnClickListener(this);

        return view;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        FragmentManager fragmentManager = getFragmentManager();

        switch (v.getId()){
            case R.id.business_cards:
                Toast.makeText(view.getContext(), "OnClickListener", Toast.LENGTH_SHORT).show();
                getActivity().setTitle("Business card");
                //fragmentManager.beginTransaction().replace(R.id.navigation_container, new UniversalOrderFragment()).commit();
                break;
            case R.id.flyers:
                Toast.makeText(view.getContext(), "OnClickListener", Toast.LENGTH_SHORT).show();
                break;
            case R.id.envelope:
                Toast.makeText(view.getContext(), "OnClickListener", Toast.LENGTH_SHORT).show();
                break;
            case R.id.pen:
                Toast.makeText(view.getContext(), "OnClickListener", Toast.LENGTH_SHORT).show();
                break;
            case R.id.company_diaries:
                Toast.makeText(view.getContext(), "OnClickListener", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;

        }

    }
}
