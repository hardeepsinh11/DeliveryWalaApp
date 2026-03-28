package com.example.deliverywala.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.deliverywala.R;
import com.example.deliverywala.model.OrderHistory;
import com.example.deliverywala.util.Constants;
import android.graphics.Color;
import android.graphics.Typeface;
import java.util.ArrayList;

public class AdminOrderAdapter extends RecyclerView.Adapter<AdminOrderAdapter.OrderViewHolder> {
    private Context context;
    private ArrayList<OrderHistory> itemList;

    public AdminOrderAdapter(Context context, ArrayList<OrderHistory> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView txtRestaurantName;
        TextView txtOrderDate;
        TextView txtTotalCost;
        TextView txtOrderTime;
        TextView txtdeliveryAddress;
        TextView txtContact;
        TextView txtOrderStatus;
        LinearLayout llFood;

        public OrderViewHolder(View view) {
            super(view);
            txtRestaurantName = view.findViewById(R.id.txtRestaurantName);
            txtOrderDate = view.findViewById(R.id.txtOrderDate);
            txtTotalCost = view.findViewById(R.id.txtTotalCost);
            txtOrderTime = view.findViewById(R.id.txtOrderTime);
            txtdeliveryAddress = view.findViewById(R.id.txtdeliveryAddress);
            txtContact = view.findViewById(R.id.txtContact);
            txtOrderStatus = view.findViewById(R.id.txtOrderStatus);
            llFood = view.findViewById(R.id.llFood);
        }
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.admin_order_row, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderHistory food = itemList.get(position);
        if (food == null || food.getFoodName() == null) return;
        Log.e("adapterssss",food.getOrderDate().toString());
        holder.txtRestaurantName.setText("Order details: " + food.getOrderId().toString());
//        holder.txtOrderDate.setText(food.getOrderDate().replace('-', '/'));
        holder.txtOrderDate.setText(food.getOrderDate());
        holder.txtTotalCost.setText(context.getResources().getString(R.string.Rs) + ". " + food.getTotalCost());
        
        String strAdd = food.getDeliveryAddress();
        strAdd = strAdd.replace(",", ",\n");
        holder.txtdeliveryAddress.setText(strAdd);
        holder.txtContact.setText(food.getContact());
        holder.txtContact.setOnClickListener(v -> Constants.dialNo(food.getContact()));

        switch (food.getOrderStatus()) {
            case "0":
                holder.txtOrderStatus.setText("Not dispatch yes");
                break;
            case "1":
                holder.txtOrderStatus.setText("Out for Delivery");
                holder.txtOrderStatus.setTextColor(Color.RED);
                break;
            case "2":
                holder.txtOrderStatus.setText("Delivered");
                holder.txtOrderStatus.setTypeface(holder.txtOrderStatus.getTypeface(), Typeface.ITALIC);
                holder.txtOrderStatus.setTextColor(Color.BLACK);
                break;
        }
        holder.llFood.removeAllViews();

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

    // Adapter ની અંદર આ રીતે સેટ કરવું
    public void updateList(ArrayList<OrderHistory> newList) {
        this.itemList = newList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}