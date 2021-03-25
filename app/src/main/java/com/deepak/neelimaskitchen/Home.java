package com.deepak.neelimaskitchen;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.deepak.neelimaskitchen.Common.Common;
import com.deepak.neelimaskitchen.Model.Category;
import com.deepak.neelimaskitchen.Service.ListenOrder;
import com.deepak.neelimaskitchen.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.squareup.picasso.Picasso;

import io.paperdb.Paper;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseDatabase database;
    DatabaseReference category;
    ImageView curryOn;
    TextView navFullName;
    RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Neelima's Kitchen Menu");
        setSupportActionBar(toolbar);

        //Paper Init
        Paper.init(this);

        //Init Firebase
        database = FirebaseDatabase.getInstance();
        category = database.getReference("Category");


        FloatingActionButton fab = findViewById(R.id.fab);
        //Button action to send user to Cart Activity
        fab.setOnClickListener(view -> {
            Intent cartIntent = new Intent(Home.this, Cart.class);
            startActivity(cartIntent);
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        //Set Name for user
        View headView = navigationView.getHeaderView(0);
        navFullName = headView.findViewById(R.id.navFullName);
        navFullName.setText(Common.currentUser.getName());

        //Load Menu
        recycler_menu = findViewById(R.id.recycler_menu);
        recycler_menu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_menu.setLayoutManager(layoutManager);
        //UI choice to make menu have Grid layout or not.
//        recycler_menu.setLayoutManager(new GridLayoutManager(this, 2));
        loadMenu();

        //Register Service for Notifications of order status being updated
        Intent service = new Intent(Home.this, ListenOrder.class);
        startService(service);

        //Display curryOn Gif inside Nav Drawer
        curryOn = headView.findViewById(R.id.curryOn);
        Glide.with(Home.this)
                .load(R.raw.curry_on)
                .into(curryOn);



    }


    //Method to load Main Menu from Firebase
    @SuppressLint("LongLogTag")
    private void loadMenu() {

        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(Category.class, R.layout.menu_item, MenuViewHolder.class, category) {
            @Override
            protected void populateViewHolder(MenuViewHolder menuViewHolder, Category category, int i) {

                //Loads Text and Image
                menuViewHolder.tvMenuName.setText(category.getName());
                Picasso.get().load(category.getImage())
                        .into(menuViewHolder.imageMenu);
//                final Category clickItem = category;
                menuViewHolder.setItemClickListener((view, position, isLongClick) -> {
                    //Get CategoryId and send it to new Activity
                    Intent foodList = new Intent(Home.this, FoodList.class);
                    foodList.putExtra("CategoryId", adapter.getRef(position).getKey());
                    Log.d("POSITION KEY", String.valueOf(getRef(position).getKey()));
                    startActivity(foodList);
                });

            }
        };

        //Refreshes data when data has been updated
        adapter.notifyDataSetChanged();
        recycler_menu.setAdapter(adapter);

    }


    @Override
    public void onBackPressed() {

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            FancyToast.makeText(Home.this, "You're already logged in :)", FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show();
            //Enable this to exit app when user pressed back in Home Activity
//            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {
            //Send user to Menu
        } else if (id == R.id.nav_cart) {
            //Send user to Cart
            Intent cartIntent = new Intent(this, Cart.class);
            startActivity(cartIntent);

        } else if (id == R.id.nav_orders) {
            //Send user to their Orders
            Intent orderIntent = new Intent(this, OrderStatus.class);
            startActivity(orderIntent);

        } else if (id == R.id.nav_logout){
            //Delete RememberMe Info
            Paper.book().destroy();
            //Logout
            Intent mainscreenIntent = new Intent(this, MainActivity.class);
            startActivity(mainscreenIntent);
            finish();


        } else if (id == R.id.nav_contact) {
            //Send user to dial phone number or to Google Maps Store
            String phone = "2245510504";
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
            startActivity(intent);

        } else if (id == R.id.nav_directions) {
            //Send user to dial phone number or to Google Maps Store
            String url = "https://neelimakitchenapp.wixsite.com/nkapp";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);

        } else if (id == R.id.nav_share) {
            //send user share Google Play Store Link
            String url = "https://play.google.com/store/apps/details?id=com.deepak.neelimaskitchen";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
