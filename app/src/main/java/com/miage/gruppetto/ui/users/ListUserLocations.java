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
    private String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_location_users);

        // Retrive intent extrat
        if (savedInstanceState == null) {
            Bundle extras = this.getIntent().getExtras();
            if(extras == null) {
                Log.w("ListUserLocations","extras is null");
                user=null;
            } else {
                user = extras.getString("user");
                extras.remove("user");
                Log.d("ListUserLocations","extras(user:"+user+")");
            }
        } else {
            user = (String) savedInstanceState.getSerializable("yser");
            savedInstanceState.remove("user");
            Log.d("ListUserLocations","extras(user:"+user+")");
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
        boolean alreadyInDataList;
        Log.d("ListUserLocations","arrayAdapterListView:locations.size("+HomeFragment.locations.size()+")");
        for (int i = 1; i<= HomeFragment.locations.size(); i++) {
            alreadyInDataList = false;
            Location location = HomeFragment.locations.get(HomeFragment.locations.size()-i);

            if (location.getUser().equals(user)) {
                for (String data : dataList) {
                    Log.i("ListUserLocations","data:"+data+", ("+location.getLat()+","+location.getLng()+")");
                    if (data.equals("("+location.getLat()+","+location.getLng()+")")) {
                        alreadyInDataList = true;
                        Log.i("ListUserLocations","alreadyInDataList(true)");
                    }
                }
                if (!alreadyInDataList) {
                    dataList.add("("+location.getLat()+","+location.getLng()+")");
                    Log.i("ListUserLocations","dataLost.add()");
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
