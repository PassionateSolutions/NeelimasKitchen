package com.deepak.neelimaskitchen.ViewHolder;

import android.content.Context;
import android.graphics.Color;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.deepak.neelimaskitchen.Interface.ItemClickListener;
import com.deepak.neelimaskitchen.Model.Order;
import com.deepak.neelimaskitchen.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

//class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
//
//    public TextView cart_item_name, cart_item_price;
//    public ImageView cart_item_count;
//
//    private ItemClickListener itemClickListener;
//
//    public void setCart_item_name(TextView cart_item_name) {
//        this.cart_item_name = cart_item_name;
//    }
//
//    public CartViewHolder(@NonNull View itemView) {
//        super(itemView);
//        cart_item_name = itemView.findViewById(R.id.cart_item_name);
//        cart_item_price = itemView.findViewById(R.id.cart_item_price);
//        cart_item_count = itemView.findViewById(R.id.cart_item_count);
//    }
//
//    @Override
//    public void onClick(View v) {
//
//    }
//}

public class CartAdapter extends RecyclerView.Adapter<CartViewHolder> {

    private List<Order> listData = new ArrayList<>();
    private Context context;

    public CartAdapter(List<Order> listData, Context context) {
        this.listData = listData;
        this.context = context;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.cart_layout, parent, false);
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {

        //Make sure TextDrawable API is implemented in Module:app
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(""+listData.get(position).getQuantity(), Color.BLUE);
        holder.cart_item_count.setImageDrawable(drawable);

        //Gets the US Dollar price for the menu item from Firebase data and multiplies it by the quantity user picked
        Locale locale = new Locale ("en", "US");
        NumberFormat format = NumberFormat.getCurrencyInstance(locale);
        int price = (Integer.parseInt(listData.get(position).getPrice())) * (Integer.parseInt(listData.get(position).getQuantity()));
        holder.cart_item_price.setText(format.format(price));
        holder.cart_item_name.setText(listData.get(position).getProductName());


    }

    @Override
    public int getItemCount() {
        return listData.size();
    }


    //Methods for Removing Cart item
    public void removeItem(int position)
    {
        listData.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Order item, int position)
    {
        listData.add(position, item);
        notifyItemInserted(position);
    }


}
