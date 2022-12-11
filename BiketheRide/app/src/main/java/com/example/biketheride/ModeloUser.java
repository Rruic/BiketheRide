package com.example.biketheride;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ModeloUser {

    private String uid;
    private String name;
    private String email;
    private String onlineStatus;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    private static ModeloUser mInstance;

    //nobody can instantiate
    public ModeloUser() {

    }

    public ModeloUser(String name, String email, String uid,String onlineStatus) {
        this.name = name;
        this.email = email;
        this.uid = uid;
        this.onlineStatus=onlineStatus;
    }

    public void addToDatabase(String uid){
        DatabaseReference database= FirebaseDatabase.getInstance("https://biketheride-d83a4-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        database.child("user/"+uid).setValue(this);
    }

    public static ModeloUser getInstance() {
        if (mInstance == null)
            mInstance = new ModeloUser();
        return mInstance;
    }
}
