package com.deepak.neelimaskitchen;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.deepak.neelimaskitchen.Common.Common;
import com.deepak.neelimaskitchen.Model.Order;
import com.deepak.neelimaskitchen.Model.Request;
import com.deepak.neelimaskitchen.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jaredrummler.materialspinner.MaterialSpinner;

public class OrderStatus extends AppCompatActivity {

    public RecyclerView recyclerView;
    public  RecyclerView.LayoutManager layoutManager;
    MaterialSpinner materialSpinner;

    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference requests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        //Firebase Init
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        recyclerView = findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Allow user to click on notification when order status changes
        if (getIntent() == null)
        loadOrder(Common.currentUser.getPhone());
        else loadOrder(getIntent().getStringExtra("userPhone"));
    }

    private void loadOrder(String phone) {



        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request.class,
                R.layout.order_layout,
                OrderViewHolder.class,
                requests.orderByChild("phone")
                        .equalTo(Common.currentUser.getPhone())
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder orderViewHolder, Request request, int i) {
                orderViewHolder.tvOrderId.setText(adapter.getRef(i).getKey());
                orderViewHolder.tvOrderStatus.setText(convertCodeToStatus(request.getStatus()));
                orderViewHolder.tvOrderAddress.setText(request.getAddress());
                orderViewHolder.tvOrderPhone.setText(request.getPhone());

                //TODO: OrderStatus.java line 71 from Admin App
                //Admin Action buttons from OrderViewHolder to update, remove, and see detail
                //Update
                orderViewHolder.bUpdateOrder.setOnClickListener(v ->
                        showUpDateDialog(adapter.getRef(i).getKey(), adapter.getItem(i)));


                //Send User  to Order Detail
                orderViewHolder.bOrderDetail.setOnClickListener(v -> {
                    Intent orderDetail = new Intent(OrderStatus.this, OrderDetail.class);
                    Common.currentRequest = request;
                    orderDetail.putExtra("OrderId", adapter.getRef(i).getKey());
                    startActivity(orderDetail);
                });
            }
        };

        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);


    }

    // Ability to let user select "Cancel order..."
    //Dialog method for showing Dialog Window with selection for order update status
    private void showUpDateDialog(String key, final Request item) {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderStatus.this);
        alertDialog.setTitle("Update Order");
        alertDialog.setMessage("Please choose status");

        LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.update_order_layout, null);

        materialSpinner = view.findViewById(R.id.statusSpinner);
        materialSpinner.setPadding(4, 16, 0, 16);
        materialSpinner.setItems("Placed", "Cancel Order");

        alertDialog.setView(view);

        // YES Selection
        final String localKey = key;
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                item.setStatus(String.valueOf(materialSpinner.getSelectedIndex()));

                requests.child(localKey).setValue(item);

                adapter.notifyDataSetChanged();
            }
        });

        // NO selection
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();

    }

    private String convertCodeToStatus(String status) {

        //old code when user can select to cancel order...

//        if (status.equals("0"))
//            return "Placed";
//
//        else if (status.equals("1"))
//            return "Order being made...";
//
//        else
//
//            return "On my way...";

        if (status.equals("0"))
            return "Placed";

        switch (status) {
            case "1":
                return "Cancel Order...";
            case "2":
                return "Order being made...";
            case "3":
                return "On my way...";
        }

        return status;
    }

}
