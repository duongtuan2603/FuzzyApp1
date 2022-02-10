package com.example.fuzzyapp1;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.fuzzyapp1.databinding.ActivityMapsBinding;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import io.socket.emitter.Emitter;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, CarParkAdapter.ICarPark {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private LocationManager lm;
    private Location location;
    private String[] permissions;
    private DistanceService distanceService;
    private DistanceService locationService;

    private List<CarPark> carParks;
    private CarParkAdapter carParkAdapter;
    String queryString = "";
    private Socket mSocket;

    {
        try {
            mSocket = IO.socket("http://192.168.2.103:4000");
        } catch (URISyntaxException e) {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSocket.on("new message", onNewMessage);
        mSocket.connect();

        carParkAdapter = new CarParkAdapter(this);

        distanceService = new DistanceService("https://trueway-matrix.p.rapidapi.com/");
        locationService = new DistanceService("http://192.168.2.103:4000/");

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        setContentView(binding.getRoot());


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.rcv.setAdapter(carParkAdapter);
        binding.rcv.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSocket.disconnect();
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
        //Get current location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, 1000);
        } else {
            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            getLocation();
        }

    }


    @Override
    public void onClickItem(double lat, double lon) {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?saddr="
                        + location.getLatitude() + "," + location.getLongitude()
                        + "&daddr="
                        + lat + "," + lon));
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            getLocation();
        }

    }

    private void getLocation() {

        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        // Add a marker in Sydney and move the camera
        LatLng currentLocation = new LatLng(latitude, longitude);
        //mMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,
                17));
        mMap.setMyLocationEnabled(true);
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        callAPILocation();
    }

    private void callAPILocation() {
        locationService.loicationAPI.getLocations().enqueue(new Callback<LocationResponse>() {
            @Override
            public void onResponse(Call<LocationResponse> call, Response<LocationResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        carParks = response.body().getData();
                        for (int i = 0; i < carParks.size(); i++) {
                            CarPark carPark = carParks.get(i);
                            if (i != carParks.size() - 1) {
                                queryString = queryString.concat(carPark.getLat() + "," + carPark.getLon() + ";");
                            } else {
                                queryString = queryString.concat(carPark.getLat() + "," + carPark.getLon());
                            }
                            callAPIDistance();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<LocationResponse> call, Throwable t) {
                Log.d("onResponse", "response: " + t.getMessage());

            }
        });
    }

    private void callAPIDistance() {
        distanceService.distanceAPI.getDistances("trueway-matrix.p.rapidapi.com", "d9394aba86msh5d9752ebcfcb740p10782ajsnad6dfcd9b9c6", queryString, location.getLatitude() + "," + location.getLongitude()).enqueue(new Callback<DistanceResponse>() {
            @Override
            public void onResponse(Call<DistanceResponse> call, Response<DistanceResponse> response) {
                if (response.isSuccessful()) {
                    for (int i = 0; i < carParks.size(); i++) {
                        CarPark carPark = carParks.get(i);
                        Log.d("onResponse", "response: " + response.body().getDistances().toString());

                        List<Double> dumpDistances = (List<Double>) response.body().getDistances().get(i);
                        List<Double> dumpDurations = (List<Double>) response.body().getDurations().get(i);
                        carPark.setDistance(dumpDistances.get(0));
                        carPark.setDuration(

                                dumpDurations.get(0));
                        carParks.set(i, carPark);
                    }
                    Log.d("onResponse", "carparks: " + carParks.toString());
                    carParkAdapter.setCarParks(carParks);
                }
            }

            @Override
            public void onFailure(Call<DistanceResponse> call, Throwable t) {
                Log.d("onCallFailure", "onResponse: " + t.getMessage());
            }
        });
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("socketMessage", "run: " + args[0]);
                }
            });
        }
    };


}