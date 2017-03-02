package com.example.hugh.map;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.KeyEvent.Callback;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.vision.text.Line;
import com.google.firebase.database.ThrowOnExtraProperties;

import android.location.Geocoder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Hugh on 2/27/17.
 */

public class ListActivity extends FragmentActivity {

    ArrayList<String> objectIDs = new ArrayList<String>();
    int picked_up_size = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_locations);

        final JSONArray finalList = new JSONArray();
        final JSONArray finalDropoffList = new JSONArray();

        final Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        CheckBox box = (CheckBox) findViewById(R.id.checkBox);

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url ="https://thesnapapp.herokuapp.com/ride_requests?picked_up=true";


        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response1) {

                        try {
                            final JSONObject obj = new JSONObject(response1);

                            JSONArray rides = obj.getJSONArray("rides");

                            for(int i=0; i < rides.length(); i++) {

                                picked_up_size++;

                                JSONObject ride = rides.getJSONObject(i);

                                JSONObject address_ride = new JSONObject();

                                address_ride.put("timestamp", ride.getString("timestamp"));
                                address_ride.put("num_passengers", ride.getString("num_passengers"));

                                String pickup_location = ride.getString("pickup_location");
                                String dropoff_location = ride.getString("dropoff_location");
                                List<String> items = Arrays.asList(pickup_location.split("\\s*,\\s*"));

                                float pickup_lat = Float.parseFloat(items.get(0));
                                float pickup_lon = Float.parseFloat(items.get(1));

                                items = Arrays.asList(dropoff_location.split("\\s*,\\s*"));

                                float dropoff_lat = Float.parseFloat(items.get(0));
                                float dropoff_lon = Float.parseFloat(items.get(1));

                                LatLng pickup = new LatLng(pickup_lat, pickup_lon);
                                LatLng dropoff = new LatLng(dropoff_lat, dropoff_lon);

                                List<Address> addresses = null;

                                addresses = geocoder.getFromLocation(pickup_lat, pickup_lon, 1);



                                if (addresses == null || addresses.size() == 0) {
                                    System.out.println("\n\nCOULD NOT FIND ADDRESS FOR COORDINATES");
                                } else {
                                    Address address = addresses.get(0);
                                    ArrayList<String> addressFragments = new ArrayList<String>();

                                    // Fetch the address lines using getAddressLine,
                                    // join them, and send them to the thread.
                                    for (int j = 0; j < address.getMaxAddressLineIndex(); j++) {
                                        addressFragments.add(address.getAddressLine(j));
                                    }



                                    address_ride.put("pickup_location", addressFragments.get(0));
                                }

                                addresses = geocoder.getFromLocation(dropoff_lat, dropoff_lon, 1);

                                if (addresses == null || addresses.size() == 0) {
                                    System.out.println("\n\nCOULD NOT FIND ADDRESS FOR COORDINATES");
                                } else {
                                    Address address = addresses.get(0);
                                    ArrayList<String> addressFragments = new ArrayList<String>();

                                    // Fetch the address lines using getAddressLine,
                                    // join them, and send them to the thread.
                                    for (int j = 0; j < address.getMaxAddressLineIndex(); j++) {
                                        addressFragments.add(address.getAddressLine(j));
                                    }

                                    address_ride.put("dropoff_location", addressFragments.get(0));

                                }

                                objectIDs.add(i, ride.getString("_id"));

                                address_ride.put("_id", ride.getString("_id"));
                                address_ride.put("dropoff_coords", ride.getString("dropoff_location"));
                                address_ride.put("pickup_coords", ride.getString("pickup_location"));

                                finalDropoffList.put(address_ride);

                            }

                            for(int i = 0; i < finalDropoffList.length(); i++){

                                Context context = getApplicationContext();
                                TextView address_1 = new TextView(context);
                                TextView address_2 = new TextView(context);
                                TextView num_passengers = new TextView(context);
                                ImageView imageView = new ImageView(context);
                                LinearLayout list = (LinearLayout) findViewById(R.id.list);

                                LinearLayout ride_list_item = new LinearLayout(context);

                                RideStatus status = new RideStatus(finalDropoffList.getJSONObject(i).getString("_id"), true, finalDropoffList.getJSONObject(i).getString("pickup_coords"), finalDropoffList.getJSONObject(i).getString("dropoff_coords"));

                                ride_list_item.setTag(status);

                                LinearLayout address = new LinearLayout(context);
                                Button completeButton = new Button(context);

                                //address 1, num_passengers
                                try {
                                    address_1.setText(finalDropoffList.getJSONObject(i).getString("pickup_location"));
                                    address_1.setTextColor(Color.BLACK);
                                    num_passengers.setText(finalDropoffList.getJSONObject(i).getString("num_passengers"));
                                }catch(Throwable l){
                                }

                                //System.out.println(finalList.getJSONObject(i).getString("pickup_address"));
                                //imageView
                                imageView.setImageDrawable(getDrawable(R.drawable.downarrow));
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(100, 100);
                                imageView.setLayoutParams(layoutParams);
                                imageView.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View view) {
                                        Intent myIntent = new Intent(view.getContext(), MapsActivity.class);
                                        startActivityForResult(myIntent, 0);
                                    }

                                });

                                //address
                                address.setOrientation(LinearLayout.VERTICAL);
                                LinearLayout.LayoutParams myLayoutParams = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                myLayoutParams.width = 500;
                                address.setLayoutParams(myLayoutParams);
                                address.addView(address_1);


                                //ride_list_item
                                ride_list_item.setOrientation(LinearLayout.HORIZONTAL);
                                LinearLayout.LayoutParams itemLayoutParams = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                myLayoutParams.height = 100;
                                ride_list_item.setLayoutParams(itemLayoutParams);
                                ride_list_item.addView(imageView);
                                ride_list_item.addView(address);
                                ride_list_item.addView(num_passengers);

                                completeButton.setMaxHeight(75);
                                completeButton.setText("Mark Complete");
                                completeButton.setId(i);
                                completeButton.setOnClickListener(new OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        LinearLayout linearParent =  (LinearLayout) v.getParent().getParent();
                                        LinearLayout linearChild = (LinearLayout) v.getParent();
                                        RideStatus tag = (RideStatus)linearChild.getTag();
                                        if(tag.picked_up == false) {
                                            updateServer(tag._id , true);
                                            TextView addressTwo = (TextView) linearChild.findViewById(R.id.address_layout).findViewById(R.id.address_to);

                                            linearParent.removeView(linearChild);
                                            linearChild.setTag(new RideStatus(tag._id, true, tag.pickup_location, tag.dropoff_location));
                                            TextView address = (TextView) linearChild.findViewById(R.id.address_layout).findViewById(R.id.address_slot);
                                            address.setText(addressTwo.getText());

                                            ImageView icon = (ImageView) linearChild.findViewById(R.id.pickup_icon);
                                            icon.setImageDrawable(getDrawable(R.drawable.downarrow));
                                            linearParent.addView(linearChild, 0);
                                        }else{
                                            linearParent.removeView(linearChild);
                                            updateServer(tag._id , false);
                                        }
                                    }
                                });

                                imageView.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        LinearLayout linearParent =  (LinearLayout) v.getParent().getParent();
                                        LinearLayout linearChild = (LinearLayout) v.getParent();
                                        RideStatus tag = (RideStatus)linearChild.getTag();

                                        Intent intent = new Intent(ListActivity.this, MapsActivity.class);
                                        intent.putExtra("LOCATION", tag.dropoff_location);
                                        startActivity(intent);
                                    }
                                });

                                ride_list_item.addView(completeButton);

                                list.addView(ride_list_item, 0);

                            }
                        }catch (Throwable t) {
                            System.out.println("Could not parse malformed JSON: \"" + response1 + "\"");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //mTextView.setText("That didn't work!");
            }
        });
        // Add the request to the RequestQueue.


         url = "https://thesnapapp.herokuapp.com/ride_requests?picked_up=false";


        // Request a string response from the provided URL.
        StringRequest stringRequest2 = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("2: " + response);
                        try {
                            JSONObject obj = new JSONObject(response);

                            JSONArray rides = obj.getJSONArray("rides");

                            for(int i=0; i < rides.length(); i++) {
                                JSONObject ride = rides.getJSONObject(i);

                                JSONObject address_ride = new JSONObject();

                                address_ride.put("timestamp", ride.getString("timestamp"));
                                address_ride.put("num_passengers", ride.getString("num_passengers"));

                                String pickup_location = ride.getString("pickup_location");
                                String dropoff_location = ride.getString("dropoff_location");
                                List<String> items = Arrays.asList(pickup_location.split("\\s*,\\s*"));

                                float pickup_lat = Float.parseFloat(items.get(0));
                                float pickup_lon = Float.parseFloat(items.get(1));

                                items = Arrays.asList(dropoff_location.split("\\s*,\\s*"));

                                float dropoff_lat = Float.parseFloat(items.get(0));
                                float dropoff_lon = Float.parseFloat(items.get(1));

                                LatLng pickup = new LatLng(pickup_lat, pickup_lon);
                                LatLng dropoff = new LatLng(dropoff_lat, dropoff_lon);

                                List<Address> addresses = null;

                                addresses = geocoder.getFromLocation(pickup_lat, pickup_lon, 1);

                                if (addresses == null || addresses.size() == 0) {
                                    System.out.println("\n\nCOULD NOT FIND ADDRESS FOR COORDINATES");
                                } else {
                                    Address address = addresses.get(0);
                                    ArrayList<String> addressFragments = new ArrayList<String>();

                                    // Fetch the address lines using getAddressLine,
                                    // join them, and send them to the thread.
                                    for (int j = 0; j < address.getMaxAddressLineIndex(); j++) {
                                        addressFragments.add(address.getAddressLine(j));
                                    }



                                    address_ride.put("pickup_location", addressFragments.get(0));
                                }

                                addresses = geocoder.getFromLocation(dropoff_lat, dropoff_lon, 1);

                                if (addresses == null || addresses.size() == 0) {
                                    System.out.println("\n\nCOULD NOT FIND ADDRESS FOR COORDINATES");
                                } else {
                                    Address address = addresses.get(0);
                                    ArrayList<String> addressFragments = new ArrayList<String>();

                                    // Fetch the address lines using getAddressLine,
                                    // join them, and send them to the thread.
                                    for (int j = 0; j < address.getMaxAddressLineIndex(); j++) {
                                        addressFragments.add(address.getAddressLine(j));
                                    }

                                    address_ride.put("dropoff_location", addressFragments.get(0));
                                }
                                address_ride.put("_id", ride.getString("_id"));

                                address_ride.put("dropoff_coords", ride.getString("dropoff_location"));
                                address_ride.put("pickup_coords", ride.getString("pickup_location"));
                                System.out.println(ride.getString("pickup_location"));
                                System.out.println(ride.getString("dropoff_location"));


                                objectIDs.add(objectIDs.size(), ride.getString("_id"));
                                finalList.put(address_ride);
                            }

                            for(int i = 0; i < finalList.length(); i++){
                                Context context = getApplicationContext();
                                TextView address_1 = new TextView(context);
                                address_1.setId(R.id.address_slot);
                                TextView address_2 = new TextView(context);
                                address_2.setId(R.id.address_to);
                                TextView num_passengers = new TextView(context);
                                ImageView imageView = new ImageView(context);
                                imageView.setId(R.id.pickup_icon);
                                LinearLayout list = (LinearLayout) findViewById(R.id.list);
                                LinearLayout ride_list_item = new LinearLayout(context);

                                RideStatus status = new RideStatus(finalList.getJSONObject(i).getString("_id"), false, finalList.getJSONObject(i).getString("pickup_coords"), finalList.getJSONObject(i).getString("dropoff_coords"));

                                ride_list_item.setTag(status);
                                LinearLayout address = new LinearLayout(context);
                                address.setId(R.id.address_layout);
                                Button completeButton = new Button(context);


                                //address 1, num_passengers
                                try {
                                    address_1.setText(finalList.getJSONObject(i).getString("pickup_location"));
                                    address_1.setTextColor(Color.BLACK);
                                    address_2.setText(finalList.getJSONObject(i).getString("dropoff_location"));
                                    address_2.setTextColor(Color.argb( 0,  0,  0,  0));
                                    num_passengers.setText(finalList.getJSONObject(i).getString("num_passengers"));
                                }catch(Throwable l){
                                }
                                System.out.println("TEST");

                                //System.out.println(finalList.getJSONObject(i).getString("pickup_address"));
                                //imageView
                                imageView.setImageDrawable(getDrawable(R.drawable.uparrow));
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(100, 100);
                                imageView.setLayoutParams(layoutParams);
                                System.out.println("TEST");

                                //address
                                address.setOrientation(LinearLayout.VERTICAL);
                                LinearLayout.LayoutParams myLayoutParams = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                myLayoutParams.width = 500;
                                address.setLayoutParams(myLayoutParams);
                                address.addView(address_1);
                                address.addView(address_2);


                                //ride_list_item
                                ride_list_item.setOrientation(LinearLayout.HORIZONTAL);
                                LinearLayout.LayoutParams itemLayoutParams = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                myLayoutParams.height = 100;
                                ride_list_item.setLayoutParams(itemLayoutParams);
                                ride_list_item.addView(imageView);
                                ride_list_item.addView(address);
                                ride_list_item.addView(num_passengers);

                                completeButton.setMaxHeight(75);
                                completeButton.setText("Mark Complete");
                                completeButton.setId(i+picked_up_size);
                                completeButton.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        LinearLayout linearParent =  (LinearLayout) v.getParent().getParent();
                                        LinearLayout linearChild = (LinearLayout) v.getParent();
                                        RideStatus tag = (RideStatus)linearChild.getTag();
                                        if(tag.picked_up == false) {
                                            updateServer(tag._id , true);

                                            linearParent.removeView(linearChild);
                                            linearChild.setTag(new RideStatus(tag._id, true, tag.pickup_location, tag.dropoff_location));
                                            TextView address = (TextView) linearChild.findViewById(R.id.address_layout).findViewById(R.id.address_slot);
                                            TextView addressTwo = (TextView) linearChild.findViewById(R.id.address_layout).findViewById(R.id.address_to);
                                            address.setText(addressTwo.getText());

                                            ImageView icon = (ImageView) linearChild.findViewById(R.id.pickup_icon);
                                            icon.setImageDrawable(getDrawable(R.drawable.downarrow));
                                            linearParent.addView(linearChild, 0);
                                        }else{
                                            linearParent.removeView(linearChild);
                                            updateServer(tag._id , false);
                                        }
                                    }
                                });

                                imageView.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        LinearLayout linearParent =  (LinearLayout) v.getParent().getParent();
                                        LinearLayout linearChild = (LinearLayout) v.getParent();
                                        RideStatus tag = (RideStatus)linearChild.getTag();

                                        Intent intent = new Intent(ListActivity.this, MapsActivity.class);
                                        //intent.putExtras(new Bundle());

                                        if(!tag.picked_up){
                                            System.out.println("HHHHEEERRRE111 "  + tag.pickup_location);

                                            intent.putExtra("LOCATION", tag.pickup_location.toString());
                                        }else{
                                            intent.putExtra("LOCATION", tag.dropoff_location.toString());
                                        }

                                        startActivity(intent);
                                    }
                                });

                                ride_list_item.addView(completeButton);


                                list.addView(ride_list_item);

                            }
                        }catch (Throwable t) {
                            System.out.println("Could not parse malformed JSON: \"" + response + "\"");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("That didn't work!");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        queue.add(stringRequest2);

    }

    public void updateServer(String id, boolean picking_up){

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        if(picking_up){
            System.out.println("updating");
            String url = "https://thesnapapp.herokuapp.com/pickup?id=" + id;
            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("That didn't work!");
                }
            });
            queue.add(stringRequest);
        }else{
            System.out.println("deleting");
            String url = "https://thesnapapp.herokuapp.com/ride_requests?id=" + id;
            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("That didn't work!");
                }
            });
            queue.add(stringRequest);
        }
    }
}

class RideStatus{
    public String _id;
    public boolean picked_up;
    public String dropoff_location;
    public String pickup_location;


    public RideStatus(String id, boolean picked_up, String pickup_location, String dropoff_location){
        this._id = id;
        this.picked_up = picked_up;
        this.dropoff_location = dropoff_location;
        this.pickup_location = pickup_location;
    }
}
