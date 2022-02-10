package com.example.fuzzyapp1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fuzzyapp1.databinding.ItemCarParkBinding;

import java.util.ArrayList;
import java.util.List;

public class CarParkAdapter extends RecyclerView.Adapter<CarParkAdapter.CarParkViewHolder> {
    private List<CarPark> carParks = new ArrayList<>();
    private ICarPark iCarPark;

    public CarParkAdapter(ICarPark iCarPark) {
        this.iCarPark = iCarPark;
    }

    public void setCarParks(List<CarPark> carParks) {
        this.carParks = carParks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CarParkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemCarParkBinding binding = ItemCarParkBinding.inflate(layoutInflater, parent, false);
        return new CarParkViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CarParkViewHolder holder, int position) {
        CarPark carPark = carParks.get(position);
        holder.binding.setCarPark(carPark);
        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iCarPark.onClickItem(carPark.getLat(), carPark.getLon());
            }
        });
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class CarParkViewHolder extends RecyclerView.ViewHolder {
        ItemCarParkBinding binding;

        public CarParkViewHolder(@NonNull ItemCarParkBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface ICarPark {
        void onClickItem(double lat, double lon);
    }
}
