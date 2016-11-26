package com.gjorgjimarkov.caffeineperday;

import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by gjorgjim on 6/6/16.
 */
public class ParentDrink {
    private String name;
    private ArrayList<Drinks> array;

    public ParentDrink(String name, ArrayList<Drinks> array) {
        this.name = name;
        this.array = array;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Drinks> getArray() {
        return array;
    }

    public void setArray(ArrayList<Drinks> array) {
        this.array = array;
    }
}
