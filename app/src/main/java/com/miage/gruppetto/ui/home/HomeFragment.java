package com.miage.gruppetto.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.miage.gruppetto.IWasHere;
import com.miage.gruppetto.R;

import static androidx.core.content.ContextCompat.getSystemService;
import static androidx.core.content.PermissionChecker.checkSelfPermission;

public class HomeFragment extends Fragment  {

    private HomeViewModel homeViewModel;
    public MapView mMapView;
    private GoogleMap googleMap;

    private static final String[] INITIAL_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int INITIAL_REQUEST = 1337;

    protected LocationManager locationManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Google Map
        requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);

        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        mMapView = root.findViewById(R.id.homeMapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }


        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                // For showing a move to my location button
                googleMap.setMyLocationEnabled(true);

                double[] longLat = getLongLat();

                double lat = longLat[0]; //37.3721;
                double lng = longLat[1]; // -121.9824;
                Log.d("[INFO]", "HomeFragment:lat=" + lat + ", lng=" + lng);

                // For dropping a marker at a point on the Map
                LatLng sydney = new LatLng(lat, lng);
                googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description"));

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });

        return root;
    }

    /**
     *
     * @return String[long,lat]
     */
    private double[] getLongLat() {
        double[] longLat = new double[2];
        locationManager = (LocationManager) getSystemService(getContext(), LocationManager.class);
        String provider = LocationManager.GPS_PROVIDER;

        if ( checkSelfPermission(getContext(),Manifest.permission.ACCESS_FINE_LOCATION) <= 1) { // Il fallait le vérifier, ça marche avec -1
            Log.d("Location",">=1");
            Location location = locationManager.getLastKnownLocation(provider);

            longLat[0] = location.getLongitude();
            longLat[1] = location.getLatitude();

            Log.d("Location",">=1 worked");
        } else {
            Log.d("Location","<=1");
        }

        return longLat;
    }

}