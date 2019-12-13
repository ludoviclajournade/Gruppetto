package com.miage.gruppetto;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.miage.gruppetto.dummy.DummyContent;
import com.miage.gruppetto.ui.locations.ListLocationsFragment;
import com.miage.gruppetto.ui.users.ListUserLocations;

import java.util.ArrayList;

import static com.miage.gruppetto.ui.home.HomeFragment.locations;

public class HomeActivity extends AppCompatActivity implements usersListFragment.OnListFragmentInteractionListener, ListLocationsFragment.OnListFragmentInteractionListener {
    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Navigation
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_share, R.id.nav_send,
                R.id.nav_user,R.id.nav_listUsers,R.id.nav_listLocations)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

    }


    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Name, email address, and profile photo Url
            String name = currentUser.getDisplayName();
            String email = currentUser.getEmail();
            Uri photoUrl = currentUser.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = currentUser.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = currentUser.getUid();
            Log.d("[INFO]", "name=" + name + ", email=" + email);
        } else {
            Log.d("[INFO]", "User is null");
        }
    }

    /**
     *
     * @return String[long,lat]
     */
    @SuppressLint("WrongConstant")
    private double[] getLongLat() {
        double[] longLat = new double[2];
        LocationManager locationManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LocationManager.class);
        }
        String provider = LocationManager.GPS_PROVIDER;

        if ( getApplicationContext().checkCallingOrSelfPermission("") <= 1) {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {
        String user = item.content;
        Intent intent = new Intent(this, ListUserLocations.class);
        intent.putExtra("user",user);
        startActivity(intent);
    }

    @Override
    public void onListFragmentInteraction(com.miage.gruppetto.ui.locations.dummy.DummyContent.DummyItem item) {
        String myLocation = item.content;
        String[]  latLng = myLocation.replace(")","").replace("(","").split(",");
        Double lat = new Double(latLng[0]);
        Double lng = new Double(latLng[1]);
        Log.d("HomeActivity","lat:"+lat+", lng:"+lng);
        Intent intent = new Intent(this, ListUserLocations.class);
        intent.putExtra("lat",lat);
        intent.putExtra("lng",lng);
        startActivity(intent);
    }
}