package com.jarecamang.buslocate;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Alejandro on 21/02/2018.
 */

public class BusAdapter extends RecyclerView.Adapter<BusAdapter.MyViewHolder> {
    private List<Bus> busList;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, description;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            description = (TextView) view.findViewById(R.id.description);
        }

    }


    public BusAdapter(List<Bus> busList) {
        this.busList = busList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bus_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Bus bus = busList.get(position);
        holder.name.setText(bus.getName());
        holder.description.setText(bus.getDescription());
    }

    @Override
    public int getItemCount() {
        return busList.size();
    }
}
