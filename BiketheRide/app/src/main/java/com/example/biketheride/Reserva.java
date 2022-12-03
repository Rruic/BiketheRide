package com.example.biketheride;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Reserva {

    private String id;
    private String userId;
    private String bikeId;
    private String fecha;

    public Reserva() {
    }

    public Reserva(String id, String userId, String bikeId, String fecha) {
        this.id = id;
        this.userId = userId;
        this.bikeId = bikeId;
        this.fecha = fecha;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBikeId() {
        return bikeId;
    }

    public void setBikeId(String bikeId) {
        this.bikeId = bikeId;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public void addToDatabase(String id){
        DatabaseReference database= FirebaseDatabase.getInstance("https://biketheride-d83a4-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        String key= database.child("reservas").push().getKey();
        Map<String, Object> childUpdates = new HashMap<>();
        database.child("reservas/"+id).setValue(this);
    }
    private static Reserva mInstance;


    public static Reserva getInstance() {
        if (mInstance == null)
            mInstance = new Reserva();
        return mInstance;
    }

}


