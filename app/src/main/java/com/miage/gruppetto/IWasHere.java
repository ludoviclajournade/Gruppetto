package com.miage.gruppetto;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.FirebaseApp;
import com.miage.gruppetto.REST.AsyncResponse;
import com.miage.gruppetto.REST.LocationsRunnable;
import com.miage.gruppetto.REST.MyRestAPI;
import com.miage.gruppetto.data.Location;
import com.miage.gruppetto.ui.home.HomeFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class IWasHere extends AppCompatActivity implements AsyncResponse {
    public static final String EXTRA_KEY="IWASHERE_INTENT_EXTRA";
    private FirebaseAuth mAuth;
    private boolean threadFinished = false;
    double lat;
    double lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iwas_here);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Retrive intent extrat
        // Get extra
        if (savedInstanceState == null) {
            Bundle extras = this.getIntent().getExtras();
            if(extras == null) {
                lat= 0;
                lng=0;
            } else {
                lat= extras.getDouble("lat");
                lng= extras.getDouble("lng");
                extras.remove("lat");
                extras.remove("lng");
            }
        } else {
            lat = (Double) savedInstanceState.getSerializable("lat");
            lng = (Double) savedInstanceState.getSerializable("lng");
            savedInstanceState.remove("lat");
            savedInstanceState.remove("lng");
        }

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Add action on validation
        Button buttonValid = (Button) findViewById(R.id.boutton_iwashere_valid);
        buttonValid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickButtonValid();
            }
        });

        // Add action on cancel
        Button buttonCancel = (Button) findViewById(R.id.button_iwashere_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickButtonCancel();
            }
        });

        // Set list messages of this location
        arrayAdapterListView();
    }
/*
    private void setListMessages() {

        // Init myRestAPI
        JSONObject jsonParam = new JSONObject();
        try {
            jsonParam.put("lat", lat);
            jsonParam.put("lng", lng);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MyRestAPI myRestAPI = new MyRestAPI();
        myRestAPI.execute(new LocationsRunnable(getApplicationContext(),this,jsonParam,"/locationMessages/43.5620/1.4701"));

        setFinished(false);
        if (HomeFragment.locations != null) {
            Log.d("IWasHere", "Locations(size):" + HomeFragment.locations.size());
            for(com.miage.gruppetto.data.Location location : HomeFragment.locations) {
                Log.d("IWasHere","location.message:"+location.getMessage());
            }
        } else {
            Log.d("IWasHere","Locations:null");
        }

    }
*/

    private void onClickButtonCancel() {

        // Go to home activity
        Intent myIntent = new Intent(this, HomeActivity.class);
        startActivity(myIntent);
    }

    /**
     * Start home activity giving user message
     */
    private void onClickButtonValid() {

        Log.d("IWasHere","buttonValid:c licked");

        // Get user message for the location
        EditText editTextMessage = (EditText) findViewById(R.id.editText_iwashere_message);
        String message = editTextMessage.getText().toString();

        // Get intent for HomeActivity
        Intent myIntent = new Intent(this, HomeActivity.class);

        // Put message into the intent
        myIntent.putExtra(EXTRA_KEY, message);

        // Go to home activity
        startActivity(myIntent);
    }


    // This method use an ArrayAdapter to add data in ListView.
    private void arrayAdapterListView()
    {
        // Retrive messages
        //setListMessages();

        List<String> dataList = new ArrayList<String>();
        for (int i=1; i<=HomeFragment.locations.size();i++) {
            Location location = HomeFragment.locations.get(HomeFragment.locations.size()-i);
            double latDiff = lat - location.getLat();
            double lngDiff = lng - location.getLng();
            boolean latDiffIsOk = false;
            boolean lngDiffIsOk = false;
            double tolerence = 0.0001;
            if ((latDiff <= tolerence && latDiff >= 0) || (latDiff >= -tolerence && latDiff <= 0)) {
                latDiffIsOk = true;
            }
            if ((lngDiff <= 5 && lngDiff >= 0) || (lngDiff >= -5 && lngDiff <= 0)) {
                lngDiffIsOk = true;
            }

            if (lngDiffIsOk && latDiffIsOk) {
                dataList.add(location.getUser() + " : " + location.getMessage());
            }
        }

        ListView listView = (ListView)findViewById(R.id.listView_locationMessages);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                //Object clickItemObj = adapterView.getAdapter().getItem(index);
                //Toast.makeText(getApplicationContext(), "You clicked " + clickItemObj.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void setLocations(ArrayList<Location> locations) {
        HomeFragment.locations=locations;
    }

    @Override
    public void setFinished(boolean f) {
        this.threadFinished=f;
    }
}
