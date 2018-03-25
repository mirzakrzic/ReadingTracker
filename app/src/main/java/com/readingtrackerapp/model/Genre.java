package com.readingtrackerapp.model;

/**
 * Created by Anes on 3/23/2018.
 */

public class Genre {

    private int id;
    private String name;


    public Genre(int id,String name){
        this.name=name;
        this.id =id;
    }

    // name
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
