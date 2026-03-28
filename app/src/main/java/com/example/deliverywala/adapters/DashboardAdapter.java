package com.example.deliverywala.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.deliverywala.R;
import com.example.deliverywala.activities.CartActivity;
import com.example.deliverywala.database.FoodDatabase;
import com.example.deliverywala.database.FoodEntity;
import com.example.deliverywala.model.FoodDetails;
import java.util.List;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.RestaurantMenuViewHolder> {
    private Context context;
    private String restaurantName;
    private RelativeLayout proceedPassed;
    private Button btnProceed;
    private List<FoodDetails> itemList;
    private FoodDatabase db;
    private int itemSelectedCount = 0;
    private RelativeLayout proceedToCart;
    private int totalCost = 0;

    public DashboardAdapter(Context context, String restaurantName, 
                          RelativeLayout proceedPassed, Button btnProceed,
                          List<FoodDetails> itemList) {
        this.context = context;
        this.restaurantName = restaurantName;
        this.proceedPassed = proceedPassed;
        this.btnProceed = btnProceed;
        this.itemList = itemList;
        this.db = FoodDatabase.getDbInstant(context);
    }

    @NonNull
    @Override
    public RestaurantMenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dashboard_row, parent, false);
        return new RestaurantMenuViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull RestaurantMenuViewHolder holder, @SuppressLint("RecyclerView") int position) {
        FoodDetails menu = itemList.get(position);
        proceedToCart = proceedPassed;
        holder.txtFoodName.setText(menu.getFoodName());

        Glide.with(context).load(menu.getFoodImage())
                .apply(new RequestOptions().centerCrop().placeholder(R.drawable.no_image))
                .into(holder.imgFood);

        holder.txtPrice.setText("Rs." + menu.getFoodAmount());
        holder.txtqty.setText(String.valueOf(menu.getQty()));

        if (holder.spQty != null) {
            String[] qty = context.getResources().getStringArray(R.array.qty);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    context, R.layout.sp_text, qty
            );
            holder.spQty.setAdapter(adapter);
            holder.spQty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int positions, long id) {
                    if (positions > 0) {
                        Toast.makeText(context, "" + qty[positions], Toast.LENGTH_SHORT).show();
                        FoodDetails menu = itemList.get(position);
                        menu.setQty(Integer.parseInt(qty[positions]));
                        itemList.set(position, menu);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // write code to perform some action
                }
            });
        }

        // Check if food already exists in the database
        FoodEntity foodEntity = new FoodEntity(
                Integer.parseInt(menu.getFoodId()),
                menu.getFoodName(),
                menu.getFoodAmount(),
                menu.getFoodName(),
                menu.getFoodId(),
                String.valueOf(menu.getQty())
        );

        FoodEntity existingFood = db.FoodDao().getFoodById(foodEntity.food_id);
        if (existingFood == null) {
            // Insert into the database only if the food does not already exist
            db.FoodDao().insertFood(foodEntity);
        }

        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CartActivity.class);
                intent.putExtra("restaurantId", "1");
                intent.putExtra("restaurantName", restaurantName);
                intent.putExtra("total_cost", totalCost);
                context.startActivity(intent);
                totalCost = 0;
            }
        });

        holder.btnAdd.setOnClickListener(v -> {
            int qty = menu.getQty();
            qty++;
            updateData(menu, holder, qty, position);
        });

        // માઈનસ (-) બટન (btnMinus)
        holder.btnMinus.setOnClickListener(v -> {
            int qty = menu.getQty();
            if (qty > 0) {
                qty--;
                updateData(menu, holder, qty, position);
            }
        });
    }

    private void updateTotalBar() {
        totalCost = 0;
        itemSelectedCount = 0;

        for (FoodDetails food : itemList) {
            if (food.getQty() > 0) {
                itemSelectedCount++;
                totalCost += (food.getQty() * Integer.parseInt(food.getFoodAmount()));
            }
        }

        if (itemSelectedCount > 0) {
            btnProceed.setVisibility(View.VISIBLE);
            btnProceed.setText("Proceed to Cart (Rs." + totalCost + ")");
        } else {
            btnProceed.setVisibility(View.GONE);
        }
    }
    private void updateData(FoodDetails menu, RestaurantMenuViewHolder holder, int qty, int position) {
        menu.setQty(qty);
        holder.txtqty.setText(String.valueOf(qty));

        FoodEntity entity = new FoodEntity(
                Integer.parseInt(menu.getFoodId()),
                menu.getFoodName(),
                menu.getFoodAmount(),
                menu.getFoodName(),
                menu.getFoodId(),
                String.valueOf(qty)
        );

        if (qty == 1 && qty > menu.getQty() - 1) { // પહેલી વાર એડ થાય ત્યારે
            new DBAsyncTaskCart(context, entity, 2).execute(); // Insert
        } else if (qty == 0) {
            new DBAsyncTaskCart(context, entity, 4).execute(); // Delete
        } else {
            new DBAsyncTaskCart(context, entity, 3).execute(); // Update
        }

        updateTotalBar(); // નીચેની પટ્ટીમાં ટોટલ અપડેટ કરો
    }

    private void checkCart(RestaurantMenuViewHolder holder, boolean isUpdate) {
        itemSelectedCount = 0;
        totalCost = 0;
        for (int i = 0; i < itemList.size(); i++) {
            if (itemList.get(i).getQty() > 0) {
                FoodEntity foodEntity = new FoodEntity(
                    Integer.parseInt(itemList.get(i).getFoodId()),
                        itemList.get(i).getFoodName(),
                        itemList.get(i).getFoodAmount(),
                        itemList.get(i).getFoodName(),
                        itemList.get(i).getFoodId(),
                    String.valueOf(itemList.get(i).getQty())
                );
                if (isUpdate) {
                    db.FoodDao().updateFood(foodEntity);
                } else {
                    db.FoodDao().deleteFood(foodEntity);
                }
                itemSelectedCount += itemList.get(i).getQty();
                int qtyAmt = itemList.get(i).getQty() * Integer.parseInt(itemList.get(i).getFoodAmount());
                totalCost += qtyAmt;
                Log.e("totalCost", String.valueOf(totalCost));
            }
        }
        if (itemSelectedCount > 0) {
            btnProceed.setVisibility(View.VISIBLE);
            btnProceed.setText("Proceed to Cart(" + totalCost + ")");
        } else {
            btnProceed.setVisibility(View.GONE);
            btnProceed.setText("Proceed to Cart");
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


    public static class RestaurantMenuViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFood;
        TextView txtFoodName;
        TextView txtPrice;
        TextView btnMinus;
        TextView btnAdd;
        TextView txtqty;
        Spinner spQty;

        public RestaurantMenuViewHolder(View view) {
            super(view);
            imgFood = view.findViewById(R.id.imgFood);
            txtFoodName = view.findViewById(R.id.txtFoodname);
            txtPrice = view.findViewById(R.id.txtPrice);
            btnMinus = view.findViewById(R.id.btnMinus);
            btnAdd = view.findViewById(R.id.btnAdd);
            txtqty = view.findViewById(R.id.qtyText);
            spQty = view.findViewById(R.id.spQty);
        }
    }

    public static class DBAsyncTaskCart extends AsyncTask<Void, Void, Boolean> {
        private Context context;
        private FoodEntity foodEntity;
        private int mode;
        private FoodDatabase db;

        public DBAsyncTaskCart(Context context, FoodEntity foodEntity, int mode) {
            this.context = context;
            this.foodEntity = foodEntity;
            this.mode = mode;
            this.db = FoodDatabase.getDbInstant(context);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            switch (mode) {
                case 1: // Check if exists
                    return db.FoodDao().getFoodById(foodEntity.food_id) != null;
                case 2: // Insert
                    db.FoodDao().insertFood(foodEntity);
                    return true;
                case 3: // Update
                    db.FoodDao().updateFood(foodEntity);
                    return true;
                case 4: // Delete
                    db.FoodDao().deleteFood(foodEntity);
                    return true;
            }
            return false;
        }
    }
}