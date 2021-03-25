package com.deepak.neelimaskitchen.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deepak.neelimaskitchen.Interface.ItemClickListener;
import com.deepak.neelimaskitchen.R;

public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView tvMenuName;
    public ImageView imageMenu;
    private ItemClickListener itemClickListener;


    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public MenuViewHolder(@NonNull View itemView) {
        super(itemView);

        tvMenuName = itemView.findViewById(R.id.menu_name);
        imageMenu = itemView.findViewById(R.id.menu_image);

        itemView.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        itemClickListener.onClick(v, getAdapterPosition(), false);

    }
}
