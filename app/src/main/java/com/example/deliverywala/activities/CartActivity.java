package com.example.deliverywala.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.deliverywala.R;
import com.example.deliverywala.adapters.CartRecyclerAdapter;
import com.example.deliverywala.database.FoodDatabase;
import com.example.deliverywala.database.FoodEntity;
import com.example.deliverywala.model.Cart;
import com.example.deliverywala.util.ConnectionManager;
import com.example.deliverywala.util.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class CartActivity extends AppCompatActivity implements PaymentResultListener {
    private Toolbar toolbar;
    private TextView txtRestaurantName;
    private TextView txtTotal;
    private RecyclerView recyclerCart;
    private RecyclerView.LayoutManager layoutManager;
    private CartRecyclerAdapter recyclerAdapter;
    private Button btnOrder;
    private Button btnClear;
    private DatabaseReference dbRef;
    private FirebaseAuth auth;
    private final String TAG = "CartActivity";
    private final String midString = "ZOnwfd50349150252925";
    private String txnAmountString;
    private String orderIdString;
    private String txnTokenString;
    private final int ActivityRequestCode = 2;
    private ArrayList<Cart> dbCartList = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        toolbar = findViewById(R.id.toolbar);
        btnOrder = findViewById(R.id.btnOrder);
        txtTotal = findViewById(R.id.txtTotal);
        btnClear = findViewById(R.id.btnClear);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My Cart");
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        auth = FirebaseAuth.getInstance();
        recyclerCart = findViewById(R.id.recyclerCart);
        layoutManager = new LinearLayoutManager(CartActivity.this);



        int totalCost = getIntent().getIntExtra("total_cost", 0);
        String cost = String.valueOf(totalCost);
        txtTotal.setText("Total amount: " + getResources().getString(R.string.Rs) + " " + cost);

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                     new DeleteCart(getApplicationContext(),101,"").execute().get();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                dbCartList.clear();
                recyclerAdapter = new CartRecyclerAdapter(getApplicationContext(), dbCartList);
                recyclerCart.setAdapter(recyclerAdapter);
                recyclerCart.setLayoutManager(layoutManager);
                if (dbCartList.isEmpty()) {
                    btnOrder.setEnabled(false);
                    btnOrder.setAlpha(0.5f);
                } else {
                    btnOrder.setEnabled(true);
                    btnOrder.setAlpha(1.0f);
                }


                txtTotal.setText("Total amount: ₹ 0");

                btnOrder.setEnabled(false);
                btnOrder.setAlpha(0.5f); // Optional visual feedback
            }
        });

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new ConnectionManager().checkConnectivity(CartActivity.this)) {
                    orderIdString = getrandomOrderID();
                    txnAmountString = String.valueOf(calculateTotalAmount());
                    txnTokenString = Constants.TOKENIS;
                    startPaymentRazorPay(calculateTotalAmount());
                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(CartActivity.this);
                    dialog.setTitle("Error");
                    dialog.setMessage("Internet Connection Not Found");
                    dialog.setPositiveButton("Open Settings", (dialog1, which) -> {
                        Intent settingsIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                        startActivity(settingsIntent);
                        finish();
                    });
                    dialog.setNegativeButton("Exit", (dialog12, which) -> {
                        ActivityCompat.finishAffinity(CartActivity.this);
                    });
                    dialog.create().show();
                }
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadCartItems();  // custom method to refresh cart list
    }

    private void loadCartItems() {
        dbCartList.clear();
        try {
            List<FoodEntity> foodList = new RetrieveCart(getApplicationContext()).execute().get();
            if (foodList != null && !foodList.isEmpty()) {
                for (FoodEntity i : foodList) {
                    FirebaseUser currentUser = auth.getCurrentUser();
                    dbCartList.add(new Cart(
                            String.valueOf(i.food_id),
                            i.foodName,
                            i.foodPrice,
                            i.restaurantName,
                            i.restaurantId,
                            i.quantity,
                            "orderid",
                            "pending",
                            currentUser != null ? currentUser.getUid() : ""
                    ));
                }
                recyclerAdapter = new CartRecyclerAdapter(getApplicationContext(), dbCartList);
                recyclerCart.setAdapter(recyclerAdapter);
                recyclerCart.setLayoutManager(layoutManager);

                // Calculate and update total cost
                int totalAmount = calculateTotalAmount();
                txtTotal.setText("Total amount: " + getResources().getString(R.string.Rs) + " " + totalAmount);
                btnOrder.setEnabled(true);
                btnOrder.setAlpha(1.0f);
            } else {
                recyclerAdapter = new CartRecyclerAdapter(getApplicationContext(), new ArrayList<>());
                recyclerCart.setAdapter(recyclerAdapter);
                txtTotal.setText("Total amount: " + getResources().getString(R.string.Rs) + " 0");
                btnOrder.setEnabled(false);
                btnOrder.setAlpha(0.5f);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private int calculateTotalAmount() {
        int total = 0;
        for (Cart item : dbCartList) {
            try {
                int price = Integer.parseInt(item.getPrice());
                int quantity = Integer.parseInt(item.getQuantity());
                total += (price * quantity);
            } catch (NumberFormatException e) {
                Log.e(TAG, "Error calculating total: " + e.getMessage());
            }
        }
        return total;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull android.view.MenuItem item) {
        // આ ચેક કરે છે કે જે બટન દબાયું છે એનો ID 'home' (Back Arrow) છે કે નહીં
        if (item.getItemId() == android.R.id.home) {
            // આ લાઈનથી એપ પાછલી એક્ટિવિટી પર જતી રહેશે
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private String getrandomOrderID() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy");
        String date = df.format(c.getTime());
        Random rand = new Random();
        int min = 1000;
        int max = 9999;
        int randomNum = rand.nextInt(max - min + 1) + min;
        String ss = date + randomNum;
        Log.e("orderID", ss);
        return ss;
    }

    private void startPaymentRazorPay(int totalCost) {
        Checkout co = new Checkout();
        try {
            JSONObject options = new JSONObject();
            options.put("name", getString(R.string.app_name));
            options.put("description", "Order details");
            options.put("image", R.drawable.ic_logo_b_round);
            options.put("theme.color", R.color.app_color);
            options.put("currency", "INR");
            String cost = totalCost + "00";
            options.put("amount", cost);


            JSONObject prefill = new JSONObject();
            prefill.put("email", Constants.Emp_email);
            prefill.put("contact", Constants.Emp_contact);

            JSONObject retryObj = new JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            options.put("prefill", prefill);
            co.open(CartActivity.this, options);
        } catch (Exception e) {
            Toast.makeText(this, "Error in payment: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onPaymentSuccess(String s) {
        ArrayList<Cart> dbCartListemp = new ArrayList<>();
        int totalCost = calculateTotalAmount();
        for (int i = 0; i < dbCartList.size(); i++) {
            FirebaseUser currentUser = auth.getCurrentUser();
            dbCartListemp.add(new Cart(
                    dbCartList.get(i).getFoodId(),
                    dbCartList.get(i).getFoodName(),
                    dbCartList.get(i).getPrice(),
                    dbCartList.get(i).getRestaurantName(),
                    dbCartList.get(i).getRestaurantId(),
                    dbCartList.get(i).getQuantity(),
                    orderIdString,
                    s,
                    currentUser != null ? currentUser.getUid() : ""
            ));
        }

        try {
            new ClearAllCart(getApplicationContext()).execute().get();  } catch (Exception e) {
            e.printStackTrace();
        }

        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = dateFormat.format(currentDate);
        dbRef = FirebaseDatabase.getInstance().getReference();
        try {
            String orderPath = Constants.DBItemOrderName + "/" + formattedDate;
            dbRef.child(orderPath).setValue(dbCartListemp);
            dbRef.child(orderPath).child("total_cost").setValue(totalCost);
            dbRef.child(orderPath).child("date_time").setValue(formattedDate);
            dbRef.child(orderPath).child("payment_status").setValue("success");
            dbRef.child(orderPath).child("orderStatus").setValue(Constants.orderStatus[0]);
            FirebaseUser currentUser = auth.getCurrentUser();

            dbCartList.clear();
            if (recyclerAdapter != null) {
                recyclerAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

        Intent intent = new Intent(CartActivity.this, OrderActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(CartActivity.this, "Payment Failed", Toast.LENGTH_SHORT).show();
    }
    private static class ClearAllCart extends AsyncTask<Void, Void, Boolean> {
        private FoodDatabase db;

        public ClearAllCart(Context context) {
            this.db = FoodDatabase.getDbInstant(context);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            // DAO માં જે ક્વેરી છે એને અહીં કોલ કરો
            db.FoodDao().clearCart();
            return true;
        }
    }
    private static class RetrieveCart extends AsyncTask<Void, Void, List<FoodEntity>> {
        private Context context;

        RetrieveCart(Context context) {
            this.context = context;
        }

        @Override
        protected List<FoodEntity> doInBackground(Void... voids) {
            FoodDatabase db = FoodDatabase.getDbInstant(context);
            return db.FoodDao().getAllFoodsCart();
        }
    }

    private static class DeleteCart extends AsyncTask<Void, Void, Boolean> {
        private FoodDatabase db;


        private int foodId;
        private String foodName;


        public DeleteCart(Context applicationContext, int foodId , String foodName) {

            this.db = FoodDatabase.getDbInstant(applicationContext);
                this.foodId = foodId;
                this.foodName=foodName;
            }




        @Override
        protected Boolean doInBackground(Void... voids) {
            db.FoodDao().deleteFoodByID(String.valueOf(foodId), foodName);
            return true;
        }
    }
}