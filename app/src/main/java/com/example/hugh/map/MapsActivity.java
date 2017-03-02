package com.example.hugh.map;

import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.KeyEvent.Callback;
import android.widget.Button;
import android.widget.TextView;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static android.R.attr.name;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    String location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        location = getIntent().getExtras().getString("LOCATION");


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        String[] locs = location.split("\\s*,\\s*");

        float lat = Float.parseFloat(locs[0]);
        float lon = Float.parseFloat(locs[1]);

        System.out.println("latlon: " + lat + ", " + lon);

        LatLng position = new LatLng(lat, lon);

        mMap.addMarker(new MarkerOptions()
                .position(position));

        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                position, 17);
        mMap.animateCamera(location);

        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position,5));
        // Zoom ;in, animating the camera.
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.

        // Add a marker in Sydney and move the camera

    }
}
