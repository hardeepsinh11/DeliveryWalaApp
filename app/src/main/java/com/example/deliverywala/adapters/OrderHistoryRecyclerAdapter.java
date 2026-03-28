package com.example.deliverywala.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.deliverywala.R;
import com.example.deliverywala.model.OrderHistory;
import android.graphics.Color;
import android.graphics.Typeface;
import java.util.ArrayList;
import java.util.List;

public class OrderHistoryRecyclerAdapter extends RecyclerView.Adapter<OrderHistoryRecyclerAdapter.OrderViewHolder> {
    private final Context context;
    private final ArrayList<OrderHistory> itemList;

    public OrderHistoryRecyclerAdapter(Context context, List<OrderHistory> itemList) {
        this.context = context;
        this.itemList = (ArrayList<OrderHistory>) itemList;
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView txtRestaurantName;
        TextView txtOrderDate;
        TextView txtTotalCost;
        TextView txtOrderTime;
        TextView txtOrderStatus;
        LinearLayout llFood;

        public OrderViewHolder(View view) {
            super(view);
            txtRestaurantName = view.findViewById(R.id.txtRestaurantName);
            txtOrderDate = view.findViewById(R.id.txtOrderDate);
            txtTotalCost = view.findViewById(R.id.txtTotalCost);
            txtOrderTime = view.findViewById(R.id.txtOrderTime);
            txtOrderStatus = view.findViewById(R.id.txtOrderStatus);
            llFood = view.findViewById(R.id.llFood);
        }
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_order_history_restaurant, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderHistory food = itemList.get(position);
        holder.txtRestaurantName.setText("Order details: " + food.getOrderId().get(0));
        holder.txtOrderDate.setText(food.getOrderDate().replace('-', '/'));
        holder.txtTotalCost.setText(context.getResources().getString(R.string.Rs) + ". " + food.getTotalCost());

        switch (food.getOrderStatus()) {
            case "0":
                holder.txtOrderStatus.setText("Not dispatch yes");
                break;
            case "1":
                holder.txtOrderStatus.setText("Out for Delivery");
                holder.txtOrderStatus.setTextColor(Color.GREEN);
                break;
            case "2":
                holder.txtOrderStatus.setText("Delivered");
                holder.txtOrderStatus.setTypeface(holder.txtOrderStatus.getTypeface(), Typeface.ITALIC);
                holder.txtOrderStatus.setTextColor(Color.WHITE);
                break;
        }

        for (int i = 0; i < food.getFoodName().size(); i++) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout llFoodItem = (LinearLayout) inflater.inflate(R.layout.recycler_cart_single_row, null);

            TextView txtFoodName = llFoodItem.findViewById(R.id.txtFoodname);
            TextView txtQty = llFoodItem.findViewById(R.id.txtFoodQty);
            TextView txtFoodPrice = llFoodItem.findViewById(R.id.txtPrice);

            String itemName = food.getFoodName().get(i);
            String itemCost = context.getResources().getString(R.string.Rs) + ". " + food.getFoodPrice().get(i);
            txtFoodName.setText(itemName);
            txtFoodPrice.setText(itemCost);
            txtQty.setText(food.getQty().get(i));
            holder.llFood.addView(llFoodItem);
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}