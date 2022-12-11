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


}


