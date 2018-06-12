package com.example.artem.android_diphloma;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.artem.android_diphloma.R;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FAQFragment extends android.app.Fragment implements View.OnClickListener {

    View view;
    ExpandableRelativeLayout expandableLayout1, expandableLayout2, expandableLayout3, expandableLayout4,
            expandableLayout5, expandableLayout6, expandableLayout7, expandableLayout8, expandableLayout9;
    Button size_button, preparation_method_button, type_paper_button,color_button, amount_button,number_sides_button,
            paper_density_button, rounded_corners_button, disign_button;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_faq, container, false);
        setHasOptionsMenu(true);
        //Make sure you have this line of code.

        size_button = (Button) view.findViewById(R.id.expandableButton1);
        preparation_method_button = (Button) view.findViewById(R.id.expandableButton2);
        type_paper_button = (Button) view.findViewById(R.id.expandableButton3);
        color_button = (Button) view.findViewById(R.id.expandableButton4);
        amount_button = (Button) view.findViewById(R.id.expandableButton5);
        number_sides_button = (Button) view.findViewById(R.id.expandableButton6);
        paper_density_button = (Button) view.findViewById(R.id.expandableButton7);
        rounded_corners_button = (Button) view.findViewById(R.id.expandableButton8);
        disign_button = (Button) view.findViewById(R.id.expandableButton9);

        expandableLayout1 = (ExpandableRelativeLayout) view.findViewById(R.id.expandableLayout1);
        expandableLayout2 = (ExpandableRelativeLayout) view.findViewById(R.id.expandableLayout2);
        expandableLayout3 = (ExpandableRelativeLayout) view.findViewById(R.id.expandableLayout3);
        expandableLayout4 = (ExpandableRelativeLayout) view.findViewById(R.id.expandableLayout4);
        expandableLayout5 = (ExpandableRelativeLayout) view.findViewById(R.id.expandableLayout5);
        expandableLayout6 = (ExpandableRelativeLayout) view.findViewById(R.id.expandableLayout6);
        expandableLayout7 = (ExpandableRelativeLayout) view.findViewById(R.id.expandableLayout7);
        expandableLayout8 = (ExpandableRelativeLayout) view.findViewById(R.id.expandableLayout8);
        expandableLayout9 = (ExpandableRelativeLayout) view.findViewById(R.id.expandableLayout9);

        size_button.setOnClickListener(this);
        preparation_method_button.setOnClickListener(this);
        type_paper_button.setOnClickListener(this);
        color_button.setOnClickListener(this);
        amount_button.setOnClickListener(this);
        number_sides_button.setOnClickListener(this);
        paper_density_button.setOnClickListener(this);
        rounded_corners_button.setOnClickListener(this);
        disign_button.setOnClickListener(this);

        return view;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void onClick(View v) {
        //expandableLayout1.toggle();
        switch (v.getId()) {
            case R.id.expandableButton1:
                //Toast.makeText(view.getContext(), "expandableckick", Toast.LENGTH_SHORT).show();
                //Log.d("expandableLayout1",((TextView)expandableLayout1.findViewById(R.id.textexpand)).getText().toString());
                expandableLayout1.toggle();
                break;
            case R.id.expandableButton2:
                //Toast.makeText(view.getContext(), "expandableckick", Toast.LENGTH_SHORT).show();
                expandableLayout2.toggle();
                break;
            case R.id.expandableButton3:
                //Toast.makeText(view.getContext(), "expandableckick", Toast.LENGTH_SHORT).show();
                expandableLayout3.toggle();
                break;
            case R.id.expandableButton4:
                //Toast.makeText(view.getContext(), "expandableckick", Toast.LENGTH_SHORT).show();
                expandableLayout4.toggle();
                break;
            case R.id.expandableButton5:
                //Toast.makeText(view.getContext(), "expandableckick", Toast.LENGTH_SHORT).show();
                expandableLayout5.toggle();
                break;
            case R.id.expandableButton6:
                //Toast.makeText(view.getContext(), "expandableckick", Toast.LENGTH_SHORT).show();
                expandableLayout6.toggle();
                break;
            case R.id.expandableButton7:
                //Toast.makeText(view.getContext(), "expandableckick", Toast.LENGTH_SHORT).show();
                expandableLayout7.toggle();
                break;
            case R.id.expandableButton8:
                //Toast.makeText(view.getContext(), "expandableckick", Toast.LENGTH_SHORT).show();
                expandableLayout8.toggle();
                break;
            case R.id.expandableButton9:
                //Toast.makeText(view.getContext(), "expandableckick", Toast.LENGTH_SHORT).show();
                expandableLayout9.toggle();
                break;
        }
    }
}