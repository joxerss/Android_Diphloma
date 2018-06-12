package com.example.artem.android_diphloma;

import java.io.Serializable;
import java.util.ArrayList;

public class Product_Firebase implements Serializable {

    public String title;
    public String description;
    public ArrayList<String> size;


    public Product_Firebase(String title){

        this.title = title;
    }
}
