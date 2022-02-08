package com.example.fuzzyapp1;

import java.util.ArrayList;
import java.util.List;

public class DistanceResponse {
    private List<Object> distances = new ArrayList<>();
    private List<Object> durations = new ArrayList<>();

    public List<Object> getDistances() {
        return distances;
    }

    public void setDistances(List<Object> distances) {
        this.distances = distances;
    }

    public List<Object> getDurations() {
        return durations;
    }

    public void setDurations(List<Object> durations) {
        this.durations = durations;
    }
}
