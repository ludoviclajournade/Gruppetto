package com.miage.gruppetto.data;

import androidx.annotation.NonNull;

public class Location {
    private int id;
    private String user;
    private String message;
    private String horodatage;
    private double lat;
    private double lng;

    public Location() {
        super();
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getHorodatage() {
        return horodatage;
    }

    public void setHorodatage(String horodatage) {
        this.horodatage = horodatage;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    @NonNull
    @Override
    public String toString() {
        return "id:"+id+", user:"+user+", lat:"+lat+", lng:"+lng+", horodatage:"+horodatage+", message:"+message;
    }

    public Location(int id, String user, String message, String horodatage, double lat, double lng) {
        this.id = id;
        this.user = user;
        this.message = message;
        this.horodatage = horodatage;
        this.lat = lat;
        this.lng = lng;
    }
}
