package com.deepak.neelimaskitchen;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.deepak.neelimaskitchen.Common.Common;
import com.deepak.neelimaskitchen.ViewHolder.OrderDetailAdapter;

public class OrderDetail extends AppCompatActivity {

    TextView tvOrderInfoId, tvOrderInfoPhone, tvOrderInfoTotal, tvOrderInfoAddress;
    String order_id_value = "";
    RecyclerView listFoods;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        //Recycler view UI
        listFoods = findViewById(R.id.orderInfoListFoods);
        listFoods.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        listFoods.setLayoutManager(layoutManager);

        tvOrderInfoId = findViewById(R.id.tvOrderInfoId);
        tvOrderInfoPhone = findViewById(R.id.tvOrderInfoPhone);
        tvOrderInfoTotal = findViewById(R.id.tvOrderInfoTotal);
        tvOrderInfoAddress = findViewById(R.id.tvOrderInfoAddress);

        //gets OrderId intent from admin clicking on order to show order details
        if (getIntent() != null){
            order_id_value = getIntent().getStringExtra("OrderId");

            //Set Values of order detail
            tvOrderInfoId.setText(order_id_value);
            tvOrderInfoPhone.setText(Common.currentRequest.getPhone());
            tvOrderInfoTotal.setText(Common.currentRequest.getTotal());
            tvOrderInfoAddress.setText(Common.currentRequest.getAddress());

            OrderDetailAdapter orderDetailAdapter = new OrderDetailAdapter(Common.currentRequest.getFoods());
            orderDetailAdapter.notifyDataSetChanged();
            listFoods.setAdapter(orderDetailAdapter);

        }



    }
}
