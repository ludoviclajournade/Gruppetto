package com.miage.gruppetto.ui.users;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.miage.gruppetto.HomeActivity;
import com.miage.gruppetto.R;
import com.miage.gruppetto.data.Location;
import com.miage.gruppetto.ui.home.HomeFragment;

import java.util.ArrayList;
import java.util.List;

public class ListUserLocations extends AppCompatActivity {
    private double lat;
    private double lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_location_users);

        // Retrive intent extrat
        if (savedInstanceState == null) {
            Bundle extras = this.getIntent().getExtras();
            if(extras == null) {
                lat=0;
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

        // Fill list
        arrayAdapterListView();

        // Add onclick return button
        Button returnButton = (Button) findViewById(R.id.button_listLocationUsersBack);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    // This method use an ArrayAdapter to add data in ListView.
    private void arrayAdapterListView()
    {
        Log.d("ListUserLocations","arrayAdapterListView()");
        // Retrive messages
        //setListMessages();

        List<String> dataList = new ArrayList<String>();
        boolean alreadyInDataList = false;
        for (int i = 1; i<= HomeFragment.locations.size(); i++) {
            Location location = HomeFragment.locations.get(HomeFragment.locations.size()-i);

            if (location.getLat() == lat && location.getLng() == lng) {
                for (String data : dataList) {
                    if (data.equals(location.getUser())) {
                        alreadyInDataList = true;
                    }
                }
                if (!alreadyInDataList) {
                    dataList.add(location.getUser());
                    alreadyInDataList=false;
                }

            }
        }

        ListView listView = (ListView) findViewById(R.id.list_locationUsers);
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
}
