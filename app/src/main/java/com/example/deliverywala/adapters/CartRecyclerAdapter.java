package com.example.deliverywala.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.deliverywala.R;
import com.example.deliverywala.model.Cart;
import java.util.ArrayList;

public class CartRecyclerAdapter extends RecyclerView.Adapter<CartRecyclerAdapter.CartViewHolder> {
    private Context context;
    private ArrayList<Cart> itemList;

    public CartRecyclerAdapter(Context context, ArrayList<Cart> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_cart_single_row, parent, false);
        return new CartViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Cart cart = itemList.get(position);
        holder.txtFoodName.setText(cart.getFoodName());
        holder.txtPrice.setText("Rs." + cart.getPrice());
        holder.txtFoodQty.setText(cart.getQuantity());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView txtFoodName;
        TextView txtPrice;
        TextView txtFoodQty;

        public CartViewHolder(View view) {
            super(view);
            txtFoodName = view.findViewById(R.id.txtFoodname);
            txtPrice = view.findViewById(R.id.txtPrice);
            txtFoodQty = view.findViewById(R.id.txtFoodQty);
        }
    }
}