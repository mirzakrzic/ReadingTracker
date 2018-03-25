package com.readingtrackerapp.model;

/**
 * Created by Anes on 3/23/2018.
 */

public class User {

    private int id;
    private String name;
    private String surname;
    private String dateOfRegistration;
    private int numberOfReadPages;


    public User(){}
    public User(String name, String surname, String dateofRegistration, int numberOfReadPages){
        this.name=name;
        this.surname=surname;
        this.dateOfRegistration =dateofRegistration;
        this.numberOfReadPages=numberOfReadPages;
    }


    // id
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    // name
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    // surname
    public String getSurname() {
        return surname;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }

    // date of registration
    public String getDateOfRegistration() {
        return dateOfRegistration;
    }
    public void setDateOfRegistration(String dateOfRegistration) {
        this.dateOfRegistration = dateOfRegistration;
    }

    // read pages number
    public int getNumberOfReadPages() {
        return numberOfReadPages;
    }
    public void setNumberOfReadPages(int numberOfReadPages) {
        this.numberOfReadPages = numberOfReadPages;
    }
}
