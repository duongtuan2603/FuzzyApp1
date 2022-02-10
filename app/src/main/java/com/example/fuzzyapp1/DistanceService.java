package com.example.fuzzyapp1;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class DistanceService {
    public final DistanceAPI distanceAPI;
    public final LocationAPI loicationAPI;

    public interface DistanceAPI {
        @GET("CalculateDrivingMatrix")
        Call<DistanceResponse> getDistances(@Header("X-RapidAPI-Host") String host, @Header("x-rapidapi-key") String key, @Query("origins") String origins, @Query("destinations") String destinations);
    }
    public interface LocationAPI {
        @GET("api/infolocation")
        Call<LocationResponse> getLocations();
    }

    public DistanceService(String host) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(host)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        distanceAPI = retrofit.create(DistanceAPI.class);
        loicationAPI = (LocationAPI) retrofit.create(LocationAPI.class);
    }
}
