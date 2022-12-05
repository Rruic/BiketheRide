package com.example.biketheride.reserva;

public class Reserva {

    private String id;
    private String idUser;
    private String idBike;
    private String fecha;
    private String city;
    private String location;


    public Reserva() {
    }

    public Reserva(String id, String idUser, String idBike, String fecha, String city, String location) {
        this.id = id;
        this.idUser = idUser;
        this.idBike = idBike;
        this.fecha = fecha;
        this.city = city;
        this.location = location;
    }

    public Reserva(String id, String idUser, String idBike, String fecha) {
        this.id = id;
        this.idUser = idUser;
        this.idBike = idBike;
        this.fecha = fecha;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdBike() {
        return idBike;
    }

    public void setIdBike(String idBike) {
        this.idBike = idBike;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    /*public void addToDatabase(String id){
        DatabaseReference database= FirebaseDatabase.getInstance("https://biketheride-d83a4-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        String key= database.child("reservas").push().getKey();
        Map<String, Object> childUpdates = new HashMap<>();
        database.child("reservas/"+id).setValue(this).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                System.out.println("En reserva OK");
            }
        });
    }*/
    private static Reserva mInstance;


    public static Reserva getInstance() {
        if (mInstance == null)
            mInstance = new Reserva();
        return mInstance;
    }

}


