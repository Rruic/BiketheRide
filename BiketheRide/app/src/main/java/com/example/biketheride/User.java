package com.example.biketheride;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class User {

    private String uid;
    private String name;
    private String email;

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

    private static User mInstance;

    //nobody can instantiate
    public User() {

    }

    public User(String name, String email, String uid) {
        this.name = name;
        this.email = email;
        this.uid = uid;
    }

    public void addToDatabase(String uid){
        DatabaseReference database= FirebaseDatabase.getInstance("https://biketheride-d83a4-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        String key= database.child("user").push().getKey();
        String mail=getEmail().replace(".","");
        Map<String, Object> childUpdates = new HashMap<>();
        database.child("user/"+uid).setValue(this);
    }

    public static User getInstance() {
        if (mInstance == null)
            mInstance = new User();
        return mInstance;
    }
}
