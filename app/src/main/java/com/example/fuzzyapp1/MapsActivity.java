package com.example.fuzzyapp1;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.fuzzyapp1.databinding.ActivityMapsBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Headers;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private LocationManager lm;
    private Location location;
    private String[] permissions;
    private DistanceService distanceService;
    private List<CarPark> carParks;
    String queryString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        distanceService = new DistanceService();
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        setContentView(binding.getRoot());

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
        //Get current location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, 1000);
        } else {
            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            getLocation();
        }

    }

    private void getLocation() {

        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        // Add a marker in Sydney and move the camera
        LatLng currentLocation = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        mMap.setMinZoomPreference(15f);
        carParks = new ArrayList<>();
        carParks.add(new CarPark("1", 21.022258, 105.825806, "DongDa", 100));
        carParks.add(new CarPark("2", 21.027259, 105.805831, "HoangCau", 100));
        carParks.add(new CarPark("3", 21.011036, 105.846921, "XaDan", 100));

        for (int i = 0; i < carParks.size(); i++) {
            CarPark carPark = carParks.get(i);
            if (i != carParks.size() - 1) {
                queryString = queryString.concat(carPark.getLat() + "," + carPark.getLon() + ";");
            } else {
                queryString = queryString.concat(carPark.getLat() + "," + carPark.getLon());
            }
        }
        //call api
        binding.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                distanceService.distanceAPI.getDistances("trueway-matrix.p.rapidapi.com", "d9394aba86msh5d9752ebcfcb740p10782ajsnad6dfcd9b9c6", queryString, latitude + "," + longitude).enqueue(new Callback<DistanceResponse>() {
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
                        }
                    }

                    @Override
                    public void onFailure(Call<DistanceResponse> call, Throwable t) {
                        Log.d("onCallFailure", "onResponse: " + t.getMessage());
                    }
                });
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            getLocation();
        }

    }

}