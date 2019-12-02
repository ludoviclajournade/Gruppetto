package com.miage.gruppetto.REST;

import com.miage.gruppetto.data.Location;

import java.util.ArrayList;

public interface AsyncResponse {

    public void setMonString(String s) ;

    public String getMonString();

    public void setLocations(ArrayList<Location> locations);

    public void setFinished(boolean f);

}