package com.miage.gruppetto;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class IWasHere extends AppCompatActivity {
    public static final String EXTRA_KEY="IWASHERE_INTENT_EXTRA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iwas_here);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
    }

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


}
