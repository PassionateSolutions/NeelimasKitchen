package com.deepak.neelimaskitchen.ViewHolder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deepak.neelimaskitchen.Model.Order;
import com.deepak.neelimaskitchen.R;

import java.util.List;

class MyViewHolder extends RecyclerView.ViewHolder {

    //UI elements
    public TextView tvProductName, tvProductQuantity;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        tvProductName = itemView.findViewById(R.id.tvProductName);
        tvProductQuantity = itemView.findViewById(R.id.tvProductQuantity);
    }
}

public class OrderDetailAdapter extends RecyclerView.Adapter<MyViewHolder> {

    List<Order> myOrders;


    public OrderDetailAdapter(List<Order> myOrders) {
        this.myOrders = myOrders;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //Code to display the order details from clicked order
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_detail_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        //Code that routes what info to be displayed from the Order.java Model and where to put it
        Order order = myOrders.get(position);
        holder.tvProductName.setText(String.format("Name is: %s", order.getProductName()));
        holder.tvProductQuantity.setText(String.format("%s", order.getQuantity()));


    }

    @Override
    public int getItemCount() {

        //Gets the total list of items being called from onBindViewHolder
        return myOrders.size();
    }
}
