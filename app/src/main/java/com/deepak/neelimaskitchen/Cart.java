package com.deepak.neelimaskitchen;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.deepak.neelimaskitchen.Common.Common;
import com.deepak.neelimaskitchen.Database.Database;
import com.deepak.neelimaskitchen.Helper.RecyclerItemTouchHelper;
import com.deepak.neelimaskitchen.Interface.RecyclerItemTouchHelperListener;
import com.deepak.neelimaskitchen.Model.Order;
import com.deepak.neelimaskitchen.Model.Request;
import com.deepak.neelimaskitchen.ViewHolder.CartAdapter;
import com.deepak.neelimaskitchen.ViewHolder.CartViewHolder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Cart extends AppCompatActivity implements RecyclerItemTouchHelperListener {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    TextView tvTotal;
    Button bSubmitOrder;
    EditText etCustomDialogAddress;

    List<Order> cart = new ArrayList<>();
    CartAdapter adapter;

    ImageView ivThanksGif;
    CardView cartCardView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //Firebase Init
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");


        //Init
        recyclerView = findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Swipe to Delete
//        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
//        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        //UI
        tvTotal = findViewById(R.id.tvTotal);
        bSubmitOrder = findViewById(R.id.bSubmitOrder);
        ivThanksGif = findViewById(R.id.ivThanksGif);
        cartCardView = findViewById(R.id.cartCardView);
        etCustomDialogAddress = findViewById(R.id.etCustomDialogAddress);

        if (tvTotal.getText().equals("$0.00")) {
            bSubmitOrder.setVisibility(View.INVISIBLE);
        } else
            bSubmitOrder.setVisibility(View.VISIBLE);



        //Submit Button Method
        bSubmitOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SHOW ALERT DIALOG
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);

                alertDialog.setTitle("One more step!");
                alertDialog.setMessage("Order instructions: ");

                EditText etAddress = new EditText(Cart.this);
                etAddress.setHint("Tell us date, time, and" + "\n" +  "any special instructions");
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);

                etAddress.setLayoutParams(lp);
                alertDialog.setView(etAddress);




                alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

                //Action for YES button selection

                    alertDialog.setPositiveButton("Submit order", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //Create new Request
                            Request request = new Request(
                                    Common.currentUser.getPhone(),
                                    etAddress.getText().toString(),
                                    tvTotal.getText().toString(),
                                    Common.currentUser.getName(),
                                    cart
                            );


                                //Submit to Firebase
                                requests.child(String.valueOf(System.currentTimeMillis()))
                                        .setValue(request);

                                //Display Loading Image before sending user to Home
                                Glide.with(Cart.this)
                                        .load(R.raw.thanks)
                                        .into(ivThanksGif);
                                cartCardView.setVisibility(View.INVISIBLE);
                                recyclerView.setVisibility(View.INVISIBLE);
                                FancyToast.makeText(Cart.this, "Sending order...", FancyToast.LENGTH_LONG, FancyToast.INFO, false).show();


                                Handler handler = new Handler();
                                handler.postDelayed(() -> {
                                    // Actions to do after 3 seconds
                                    new Database(getBaseContext()).cleanCart();
                                    FancyToast.makeText(Cart.this, "Thank you, order placed.", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
                                    Intent homeIntent = new Intent(Cart.this, Home.class);
                                    startActivity(homeIntent);
                                    finish();

                                }, 3500);

                            }
                    });



                //Action for NO button selection
                alertDialog.setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss());

                if (cart.size() > 0)
                alertDialog.show();
                else FancyToast.makeText(Cart.this, "Empty :( Order delicious food!", FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show();

            }
        });

        loadListFood();



    }


    private void loadListFood() {

        cart = new Database(this).getCarts();
        adapter = new CartAdapter(cart, this);
        recyclerView.setAdapter(adapter);

        //Calculate Total Price
        int total = 0;

        for (Order order : cart)
            total += (Integer.parseInt(order.getPrice())) * (Integer.parseInt(order.getQuantity()));
        Locale locale = new Locale("en", "US");
        NumberFormat format = NumberFormat.getCurrencyInstance(locale);

        tvTotal.setText(format.format(total));


    }

    //Swipe to delete item
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CartViewHolder)
        {

        }
    }

    //Used to delete items from cart
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.DELETE))
            deleteCart(item.getOrder());

        return true;
    }

    //Used to delete item from cart
    private void deleteCart(int position) {
        //Remove item from List<Order>
        cart.remove(position);
        //delete old data from SQLite
        new Database(this).cleanCart();
        //Update new Data from List<Order>
        for (Order item:cart)
            new Database(this).addToCart(item);
        //Refresh
        loadListFood();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
