package com.miage.gruppetto.REST;

import android.content.Context;
import android.net.http.HttpResponseCache;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.miage.gruppetto.data.Location;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

public class LocationsRunnable implements Runnable {
    private Context context;
    private JSONObject message;
    private AsyncResponse asyncResponse;

    public LocationsRunnable(Context c) {
        super();
        context=c;
    }

    public LocationsRunnable(Context c, JSONObject json) {
        this(c);
        message = json;
    }

    public LocationsRunnable(Context c, AsyncResponse asyncResponse) {
        this(c);
        this.asyncResponse=asyncResponse;
    }

    @Override
    public void run() {

        if(message == null) {
            consumeGetREST();
        } else {
            sendPOST(message);
        }
    }

    private void consumeGetREST() {
        try {
            asyncResponse.setFinished(false);
            asyncResponse.setMonString("mon string est awesome");

            // init variables
            ArrayList<Location> locations = new ArrayList<>();
            Location location = new Location();

            // Create URL
            URL myUrl = new URL("http://10.0.2.2:5000/locations");

            // Create connection
            HttpURLConnection myConnection = (HttpURLConnection) myUrl.openConnection();

            // Add multiple header to request
            myConnection.setRequestProperty("User-Agent", "my-rest-app-v0.1");
            myConnection.setRequestProperty("Accept", "application/vnd.github.v3+json");

            // Check response
            if (myConnection.getResponseCode() == 200) {
                // Success
                Log.d("LocationsRunnable","Reponse:success(200)");

                // Get reference to the input stream
                InputStream responseBody = myConnection.getInputStream();

                // Input stream reader to secure the reading with UTF-8 format
                InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");

                // Json parser
                JsonReader jsonReader = new JsonReader(responseBodyReader);

                // Consume Json
                jsonReader.beginArray(); // Start processing the JSON object
                jsonReader.beginObject();
                while (jsonReader.hasNext()) { // Loop through all keys
                    // Fetch the next key
                    String key = jsonReader.nextName();
                    Log.d("LocationsRunnable","key:"+key);

                    switch (key) {
                        case "lng":
                            location.setLng(jsonReader.nextDouble());
                            try {
                                locations.add(location);
                                location = new Location();
                                jsonReader.endObject();
                                jsonReader.beginObject();
                            } catch(IllegalStateException e) {
                                Log.d("LocationsRunnable","jsonReader:end");
                            }
                            break;
                        case "lat":
                            location.setLat(jsonReader.nextDouble());
                            break;
                        case "id":
                            location.setId(jsonReader.nextInt());
                            break;
                        case "user":
                            //jsonReader.skipValue(); // Skip values of other keys
                            location.setUser(jsonReader.nextString());
                            break;
                        case "horodatage":
                            location.setHorodatage(jsonReader.nextString());
                            break;
                        case "message":
                            location.setMessage(jsonReader.nextString());
                            break;
                        default:
                            jsonReader.skipValue(); // Skip values of other keys
                            break;

                    }
                }

                // Close Json reader
                jsonReader.close();

                // Disconnect
                myConnection.disconnect();

                // Finished
                asyncResponse.setFinished(true);

                // Set positions
                asyncResponse.setLocations(locations);

            } else {
                // Finished
                asyncResponse.setFinished(true);
                // Error
                Log.d("LocationsRunnable","Reponse:error(expected: 200)");
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            // Finished
            asyncResponse.setFinished(true);
        }
    }

    private void sendPOST(JSONObject jsonParam) {

        try {
            // Create URL
            URL myUrl =  new URL("http://10.0.2.2:5000/locations");// new URL("http://10.0.2.2:5000/postTest");

            // Open connection
            HttpURLConnection myConnection = (HttpURLConnection) myUrl.openConnection();

            // Set mode POST
            myConnection.setRequestMethod("POST");

            // Set properties
            myConnection.setRequestProperty("Accept", "application/json");
            myConnection.setRequestProperty("Content-Type", "application/json");

            // Enable writing
            myConnection.setDoOutput(true);

            // Write the data
            myConnection.getOutputStream().write(jsonParam.toString().getBytes());

            // Init for catching response
            HttpResponseCache myCache = HttpResponseCache.install(context.getCacheDir(), 100000L);

            // Check response
            if (myConnection.getResponseCode() == 200) {
                // Success
                Log.d("LocationsRunnable","Reponse:success(200)");

                // Get reference to the input stream
                InputStream responseBody = myConnection.getInputStream();

                // Input stream reader to secure the reading with UTF-8 format
                InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");

                // Json parser
                JsonReader jsonReader = new JsonReader(responseBodyReader);

                // Consume Json
                jsonReader.beginObject(); // Start processing the JSON object
                while (jsonReader.hasNext()) { // Loop through all keys

                    String key = jsonReader.nextName(); // Fetch the next key
                    if (key.equals("organization_url")) { // Check if desired key
                        // Fetch the value as a String
                        String value = jsonReader.nextString();

                        break; // Break out of the loop
                    } else {
                        jsonReader.skipValue(); // Skip values of other keys
                    }
                }

                // Close Json reader
                jsonReader.close();

                // Disconnect
                myConnection.disconnect();

            } else {
                // Error
                Log.e("LocationsRunnable","Reponse:error(expected: 200)");
            }

            // Catch response
            if (myCache.getHitCount() > 0) {
                // The cache is working
                Log.d("INFO","The cache is working");
            } else {
                Log.e("INFO","The cache isn't working");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
