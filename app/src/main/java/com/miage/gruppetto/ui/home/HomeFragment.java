package com.miage.gruppetto.ui.home;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.miage.gruppetto.IWasHere;
import com.miage.gruppetto.Join;
import com.miage.gruppetto.R;
import com.miage.gruppetto.REST.AsyncResponse;
import com.miage.gruppetto.REST.MyRestAPI;
import com.miage.gruppetto.REST.LocationsRunnable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static androidx.core.content.ContextCompat.getSystemService;
import static androidx.core.content.PermissionChecker.checkSelfPermission;

public class HomeFragment extends Fragment implements AsyncResponse {
    private HomeViewModel homeViewModel;
    public MapView mMapView;
    private GoogleMap googleMap;
    private FirebaseAuth mAuth;
    private MyRestAPI myRestAPI;
    private String monString;
    private ArrayList<com.miage.gruppetto.data.Location> locations;
    private boolean threadFinished = false;

    private static final String[] INITIAL_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int INITIAL_REQUEST = 1337;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Add on click  listener

        Button buttonJySuis = (Button) getActivity().findViewById(R.id.button_iwashere);
        buttonJySuis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), IWasHere.class);
                startActivityForResult(myIntent, 0);
            }
        });

        Button buttonParticipation = (Button) getActivity().findViewById(R.id.buttonParticipation);
        buttonParticipation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), Join.class);
                startActivityForResult(myIntent, 0);
            }
        });

        Button buttonMarkers = (Button) getActivity().findViewById(R.id.button_showMarkers);
        buttonMarkers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get user locations
                onClickButtonMarkers();


            }
        });
    }

    private void onClickButtonMarkers() {
        myRestAPI.execute(new LocationsRunnable(getContext(),this));
        Log.d("HomeFragment","monString:"+getMonString());
        Log.d("HomeFragment:finished","status:"+myRestAPI.getStatus());
        int i=0;
        while (!threadFinished) {
            // Wait
            i++;
            Log.d("HomeFragment","threadFinished(i):"+threadFinished+"("+i+")");
        }
        Log.d("HomeFragment","threadFinished(i):"+threadFinished+"("+i+")");
        setFinished(false);
        if (locations != null) {
            Log.d("HomeFragment", "Locations(size):" + locations.size());
            for(com.miage.gruppetto.data.Location location : locations) {
                Log.d("HomeFragment","location:"+location.toString());
                LatLng position = new LatLng(location.getLat(),location.getLng());
                googleMap.addMarker(new MarkerOptions().position(position).title("my title").snippet("my description"));
            }
        } else {
            Log.d("HomeFragment","Locations:null");
        }
        // Get current position
        double[] longLat = getLongLat();
        double lat = longLat[0];
        double lng = longLat[1];
        LatLng myPosition = new LatLng(lng, lat);
        Log.d("[INFO]", "HomeFragment:markerAtCurrentPosition:lat=" + lat + ", lng=" + lng);
        googleMap.addMarker(new MarkerOptions().position(myPosition).title("my title").snippet("my description"));
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, final Bundle savedInstanceState) {
        // ask permissions
        requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);

        // set view
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // Init myRestAPI
        myRestAPI = new MyRestAPI();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // set map view
        mMapView = root.findViewById(R.id.homeMapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Sync map
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                // For showing a move to my location button
                googleMap.setMyLocationEnabled(true);

                // Get current position
                double[] longLat = getLongLat();
                double lat = longLat[0];
                double lng = longLat[1];
                LatLng myPosition = new LatLng(lng, lat);
                Log.d("[INFO]", "HomeFragment:markerAtCurrentPosition:lat=" + lat + ", lng=" + lng);

                // Get extra
                String iWasHereMessage;
                if (savedInstanceState == null) {
                    Bundle extras = getActivity().getIntent().getExtras();
                    if(extras == null) {
                        iWasHereMessage= null;
                    } else {
                        iWasHereMessage= extras.getString(IWasHere.EXTRA_KEY);
                        extras.remove(IWasHere.EXTRA_KEY);
                    }
                } else {
                    iWasHereMessage= (String) savedInstanceState.getSerializable(IWasHere.EXTRA_KEY);
                    savedInstanceState.remove(IWasHere.EXTRA_KEY);
                }
                // If message, put marker at current position
                if (iWasHereMessage != null) {
                    // For dropping a marker at a point on the Map
                    googleMap.addMarker(new MarkerOptions().position(myPosition).title("my title").snippet("my description"));

                    // Send location to the server to be registered
                    // Init myRestAPI
                    JSONObject jsonParam = new JSONObject();
                    try {
                        jsonParam.put("id", "1");
                        jsonParam.put("user", mAuth.getCurrentUser().getEmail());
                        jsonParam.put("horodatage", (System.currentTimeMillis()/1000));
                        jsonParam.put("lat",lat);
                        jsonParam.put("lng",lng);
                        jsonParam.put("message",iWasHereMessage);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    myRestAPI.execute(new LocationsRunnable(getContext(),jsonParam));
                }

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(myPosition).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                Log.d("INFO", "iWasHereMessage:" + iWasHereMessage);
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
        LocationManager locationManager = (LocationManager) getSystemService(getContext(), LocationManager.class);
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

    @Override
    public void setMonString(String s) {
        this.monString = s;
    }

    @Override
    public String getMonString() {
        return this.monString;
    }

    @Override
    public void setLocations(ArrayList<com.miage.gruppetto.data.Location> loc) {
        this.locations = loc;
    }

    @Override
    public void setFinished(boolean f) {
        this.threadFinished = f;
    }
}