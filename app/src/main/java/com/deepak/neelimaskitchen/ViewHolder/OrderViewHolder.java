package com.deepak.neelimaskitchen.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deepak.neelimaskitchen.Interface.ItemClickListener;
import com.deepak.neelimaskitchen.R;

public class OrderViewHolder extends RecyclerView.ViewHolder{

    public TextView tvOrderId, tvOrderStatus, tvOrderPhone, tvOrderAddress;

    public Button bUpdateOrder, bOrderDetail;

    private ItemClickListener itemClickListener;


    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);

        tvOrderAddress = itemView.findViewById(R.id.tvOrderAddress);
        tvOrderPhone = itemView.findViewById(R.id.tvOrderPhone);
        tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
        tvOrderId = itemView.findViewById(R.id.tvOrderId);

        bUpdateOrder = itemView.findViewById(R.id.bUpdateOrder);
        bOrderDetail = itemView.findViewById(R.id.bOrderDetail);

//        itemView.setOnClickListener(this);

    }

//    public void setItemClickListener(ItemClickListener itemClickListener) {
//        this.itemClickListener = itemClickListener;
//    }
//
//    @Override
//    public void onClick(View v) {
//
////        itemClickListener.onClick(v, getAdapterPosition(), false );
//
//
//    }

}
