package com.example.fuzzyapp1;

import androidx.annotation.NonNull;

import java.io.Serializable;


public class CarPark implements Serializable {
    private String id;
    private Double lat;
    private Double lon;
    private String name;
    private int emptySpace;
    private double duration;
    private double distance;

    public enum DistanceClassified {
        NEAR,MIDDLE,FAR
    }



    public CarPark(String id, Double lat, double lon, String name, int emptySpace) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.name = name;
        this.emptySpace = emptySpace;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getEmptySpace() {
        return emptySpace;
    }

    public void setEmptySpace(int emptySpace) {
        this.emptySpace = emptySpace;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @NonNull
    @Override
    public String toString() {
        return name + "-" + lat + "-" + lon + "\n";
    }
}
