package com.deepak.neelimaskitchen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.deepak.neelimaskitchen.Database.Database;
import com.deepak.neelimaskitchen.Interface.ItemClickListener;
import com.deepak.neelimaskitchen.Model.Food;
import com.deepak.neelimaskitchen.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FoodList extends AppCompatActivity {

    RecyclerView recyclerView;

    FirebaseDatabase database;
    DatabaseReference foodList;
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    //Search Functionality
    FirebaseRecyclerAdapter<Food, FoodViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;
    FloatingActionButton fabList;

    //Favorites
    Database localDB;


    String categoryId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        //Favorites
        localDB = new Database(this);

        //Firebase Init
        database = FirebaseDatabase.getInstance();
        foodList = database.getReference("Food");

        recyclerView = findViewById(R.id.foodListRV);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        fabList = findViewById(R.id.fabList);
        //Button action to send user to Cart Activity
        fabList.setOnClickListener(view -> {
            Intent cartIntent = new Intent(FoodList.this, Cart.class);
            startActivity(cartIntent);
        });

        //Get CategoryId Intent from Home.java
        if (getIntent() != null) {
            categoryId = getIntent().getStringExtra("CategoryId");
            if (!Objects.requireNonNull(categoryId).isEmpty()) {
                Log.d("FoodList POSITION KEY", categoryId);
                loadListFood(categoryId);
            }

            //Search Bar Functionality
//            materialSearchBar = findViewById(R.id.searchBar);
//            materialSearchBar.setHint("Search for food here");
//            //Load Suggest from Firebase
//            loadSuggest();
//            materialSearchBar.setLastSuggestions(suggestList);
//            materialSearchBar.setCardViewElevation(10);
//            materialSearchBar.addTextChangeListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                }
//
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                    //When user types in their text
//                    List<String> suggest = new ArrayList<>();
//                    for (String search : suggestList) {
//                        if (search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
//                            suggest.add(search);
//                    }
//
//                    materialSearchBar.setLastSuggestions(suggest);
//
//                }
//
//                @Override
//                public void afterTextChanged(Editable s) {
//
//                    if (materialSearchBar.getText().isEmpty())
//                        recyclerView.setAdapter(adapter);
//
//                }
//            });
//            materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
//                @Override
//                public void onSearchStateChanged(boolean enabled) {
//                    // When search bar is closed restore original adapter
//                    if (!enabled)
//                        recyclerView.setAdapter(adapter);
//                }
//
//                @Override
//                public void onSearchConfirmed(CharSequence text) {
//                    //when search finishes show result of adapter
//                    startSearch(text);
//                }
//
//                @Override
//                public void onButtonClicked(int buttonCode) {
//
//
//
//                }
//            });

        }

    } //End of on Create


    //Begins the search when user hits enter
//    private void startSearch(CharSequence text) {
//        searchAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(
//                Food.class,
//                R.layout.food_item,
//                FoodViewHolder.class,
//                foodList.orderByChild("Name").equalTo(text.toString())
//        ) {
//            @Override
//            protected void populateViewHolder(final FoodViewHolder foodViewHolder, final Food food, final int i) {
//
//                foodViewHolder.tvFoodName.setText(food.getName());
//                Picasso.with(getBaseContext()).load(food.getImage())
//                        .into(foodViewHolder.foodImage);
//
//
//                //Used for Toast Message to get name of item clicked on - turn on if you want
////                final Food local = food;
//                foodViewHolder.setItemClickListener((view, position, isLongClick) -> {
//
//                    //Creates a toast message of the clicked item
////                        FancyToast.makeText(FoodList.this, ""+local.getName(), FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
//                    //Send user to appropriate food detail
//                    Intent foodDetail = new Intent(FoodList.this, FoodDetail.class);
//                    foodDetail.putExtra("FoodId", searchAdapter.getRef(position).getKey());
//                    startActivity(foodDetail);
//                });
//
//
//            }
//        };
//
//        recyclerView.setAdapter(searchAdapter);
//    }

    //Loads suggested list from Firebase Database
//    private void loadSuggest() {
//
//        foodList.orderByChild("menuId").equalTo(categoryId)
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                            Food item = postSnapshot.getValue(Food.class);
//                            suggestList.add(item.getName());
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//
//    }

    @SuppressLint("LongLogTag")
    private void loadListFood(String categoryId) {

        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class, R.layout.food_item, FoodViewHolder.class, foodList.orderByChild("menuId").equalTo(categoryId)) {


            @Override
            protected void populateViewHolder(final FoodViewHolder foodViewHolder, final Food food, final int i) {

                foodViewHolder.tvFoodName.setText(food.getName());
                Picasso.get().load(food.getImage())
                        .into(foodViewHolder.foodImage);

//                //Add Favorites
//                if (localDB.isFavorite(adapter.getRef(i).getKey()))
//                    foodViewHolder.favImage.setImageResource(R.drawable.ic_favorite_white_24dp);
//
//                //Click to change status of Favorites when Heart if Clicked
//                foodViewHolder.favImage.setOnClickListener(v -> {
//                    //Gets name of food clicked on
//                    final Food local = food;
//                    if (!localDB.isFavorite(adapter.getRef(i).getKey())) {
//
//                        localDB.addToFavorites(adapter.getRef(i).getKey());
//                        foodViewHolder.favImage.setImageResource(R.drawable.ic_favorite_white_24dp);
//                        FancyToast.makeText(FoodList.this, "" + local.getName() + " was added to Favorites", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
//                    } else {
//
//                        localDB.removeFromFavorites(adapter.getRef(i).getKey());
//                        foodViewHolder.favImage.setImageResource(R.drawable.ic_favorite_border_white_24dp);
//                        FancyToast.makeText(FoodList.this, "" + local.getName() + " was removed from Favorites", FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show();
//
//                    }
//                });

//                final Food local = food;
                foodViewHolder.setItemClickListener((view, position, isLongClick) -> {

                    //Creates a toast message of the clicked item
//                        FancyToast.makeText(FoodList.this, ""+local.getName(), FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                    //Send user to appropriate food detail
                    Intent foodDetail = new Intent(FoodList.this, FoodDetail.class);
                    foodDetail.putExtra("FoodId", adapter.getRef(position).getKey());
                    Log.d("FoodDetail POSITION KEY", String.valueOf(getRef(position).getKey()));
                    startActivity(foodDetail);

                });

            }
        };

        //Set Adapter
        recyclerView.setAdapter(adapter);


    }
}
