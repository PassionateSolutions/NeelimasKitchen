package com.deepak.neelimaskitchen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.deepak.neelimaskitchen.Database.Database;
import com.deepak.neelimaskitchen.Model.Food;
import com.deepak.neelimaskitchen.Model.Order;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.squareup.picasso.Picasso;

public class FoodDetail extends AppCompatActivity {

    TextView food_name, food_price, food_description;
    ImageView food_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton fabCart;
    ElegantNumberButton numberButton;

    String foodId = "";

    FirebaseDatabase database;
    DatabaseReference food;
    Food currentFood;
    FloatingActionButton fabDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        fabDetail = findViewById(R.id.fabDetail);
        //Button action to send user to Cart Activity
        fabDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cartIntent = new Intent(FoodDetail.this, Cart.class);
                startActivity(cartIntent);
            }
        });


        //Firebase Init
        database = FirebaseDatabase.getInstance();
        food = database.getReference("Food");

        //init view
        numberButton = findViewById(R.id.number_button);
        fabCart = findViewById(R.id.fabCart);

        //Method for Cart Button "Add To Cart"
        fabCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Database(getBaseContext()).addToCart(new Order(
                        foodId,
                        currentFood.getName(),
                        numberButton.getNumber(),
                        currentFood.getPrice(),
                        currentFood.getDiscount()
                ));

                //Toast when successfully added to cart
                FancyToast.makeText(FoodDetail.this, "Added to cart", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();

            }
        });

        food_description = findViewById(R.id.tv_food_detail_description);
        food_name = findViewById(R.id.tv_food_detail_name);
        food_price = findViewById(R.id.tv_food_detail_price);
        food_image = findViewById(R.id.imageCollapToolbar);
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbar);

        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);

        //Get Food Id from Intent from FoodList.java method loadListFood
        if (getIntent() != null) {
            foodId = getIntent().getStringExtra("FoodId");
            if (!foodId.isEmpty()){
                getDetailFood(foodId);
            }
        }



    }

    //Get Food detail for selected Food Id
    private void getDetailFood(final String foodId) {
        food.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //When food item is deleted and user in inside food detail screen
                if (currentFood != null) {
                    //Toast when Food item has been removed
                    FancyToast.makeText(FoodDetail.this, ":( Food is no longer available...call for special request", FancyToast.LENGTH_LONG, FancyToast.INFO, false).show();
                    finish();
                }

                currentFood = dataSnapshot.getValue(Food.class);

                //Set Image
                if (currentFood != null) {
                    Picasso.get().load(currentFood.getImage())
                            .into(food_image);

                    collapsingToolbarLayout.setTitle(currentFood.getName());
                    food_price.setText(currentFood.getPrice());
                    food_name.setText(currentFood.getName());
                    food_description.setText(currentFood.getDescription());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
