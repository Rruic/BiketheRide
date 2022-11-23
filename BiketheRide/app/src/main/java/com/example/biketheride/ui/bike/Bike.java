package com.example.biketheride.ui.bike;

import android.graphics.Bitmap;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Bike {

    private Bitmap imageBitmap;
    private String image;
    private String owner;
    private String description;
    private String city;
    private Double longitude;
    private Double latitude;
    private String location;
    private String email;
    private String country;
    private String bId;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) { this.owner = owner; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public String getCity() { return city; }

    public void setCity(String city) { this.city = city; }

    public String getbId() { return bId; }

    public void setbId(String bId) { this.bId = bId; }

    public Bike(String image, String owner, String description,
                String city, Double longitude, Double latitude,
                String location, String email, Bitmap imageBitmap, String country) {
        this.image = image;
        this.owner = owner;
        this.description = description;
        this.city = city;
        this.longitude = longitude;
        this.latitude = latitude;
        this.location = location;
        this.email = email;
        this.imageBitmap = imageBitmap;
        this.country = country;
    }

    public Bike(String image, String owner, String description, String city, Double longitude, Double latitude, String location, String email, String country) {
        this.image = image;
        this.owner = owner;
        this.description = description;
        this.city = city;
        this.longitude = longitude;
        this.latitude = latitude;
        this.location = location;
        this.email = email;
        this.country = country;
    }

    public Bike() {
    }

    //añade bici a la base de datos
    public void addToDatabase() {
        DatabaseReference database = FirebaseDatabase.getInstance("https://biketheride-d83a4-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        String key = database.child("bikes_list").push().getKey();
        Map<String, Object> childUpdates = new HashMap<>();
        database.child("bikes_list/" + key).setValue(this);
    }


}
