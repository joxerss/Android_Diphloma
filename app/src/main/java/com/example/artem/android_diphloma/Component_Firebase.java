package com.example.artem.android_diphloma;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by artem on 23.03.2018.
 */

public class Component_Firebase implements Serializable {

    //papers components
    public String title;
    public ArrayList<String> colors;
    public ArrayList<String> densities;
    public double price;

    public Component_Firebase(String title, ArrayList<String> colors, ArrayList<String> densities, double price){ // for papers
        this.title = title;
        this.colors = colors;
        this.densities = densities;
        this.price = price;
    }
}
