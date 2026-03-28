package com.example.deliverywala.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.deliverywala.R;
import com.example.deliverywala.adapters.AdminOrderAdapter;
import com.example.deliverywala.model.DataModel;
import com.example.deliverywala.model.NotificationModel;
import com.example.deliverywala.model.OrderHistory;
import com.example.deliverywala.model.RootModel;
import com.example.deliverywala.model.User;
import com.example.deliverywala.util.ApiClient;
import com.example.deliverywala.util.ApiInterface;
import com.example.deliverywala.util.ConnectionManager;
import com.example.deliverywala.util.Constants;
import com.example.deliverywala.util.RecyclerTouchListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminOrderFragment extends Fragment {
    private SharedPreferences sharedPreferences;
    private RecyclerView recyclerOrder;
    private RecyclerView.LayoutManager layoutManager;
    private AdminOrderAdapter recyclerAdapter;
    private ArrayList<OrderHistory> foodList = new ArrayList<>();
    private ArrayList<User> userList = new ArrayList<>();
    private int selectedFruitsIndex = 0;
    private final String[] fruits = {"Pending", "Out for delivery", "Delivered"};

    // Notification
    private final String FCM_API = "https://fcm.googleapis.com/fcm/send";
    private final String serverKey = "key=" + "Your Firebase server key";
    private final String contentType = "application/json";
    private final String TAG = "NOTIFICATION TAG";

    private String NOTIFICATION_TITLE = "";
    private String NOTIFICATION_MESSAGE = "";
    private String TOPIC = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_history, container, false);
        recyclerOrder = view.findViewById(R.id.recyclerOrder);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerOrder.setLayoutManager(layoutManager);
        sharedPreferences = requireContext().getSharedPreferences(
                getString(R.string.preference_file_name),
                Context.MODE_PRIVATE
        );
        recyclerAdapter = new AdminOrderAdapter(getActivity(), new ArrayList<>());
        recyclerOrder.setAdapter(recyclerAdapter);

        if (new ConnectionManager().checkConnectivity(getActivity())) {
            getUserList();
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle("Error");
            dialog.setMessage("Internet connection is not found");
            dialog.setPositiveButton("Open settings", (text, listener) -> {
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                startActivity(intent);
                getActivity().finish();
            });
            dialog.setNegativeButton("Exit app", (text, listener) -> {
                ActivityCompat.finishAffinity(getActivity());
            });
            dialog.create();
            dialog.show();
        }
        setonClickListener();
        return view;
    }

    private void getUserList() {
        FirebaseDatabase.getInstance().getReference("user")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userList.clear();
                        for (DataSnapshot postsnapshot : snapshot.getChildren()) {
                            try {
                                User user = postsnapshot.getValue(User.class);
                                userList.add(user);
                            } catch (Exception e) {
                                Log.e("name", e.toString());
                            }
                        }
                        if (userList.size() > 0) {
                            getOrderDetails();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void getOrderDetails() {
        FirebaseDatabase.getInstance().getReference(Constants.DBItemOrderName)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            foodList.clear();
                            for (DataSnapshot postsnapshot : snapshot.getChildren()) {
                                ArrayList<String> foodName = new ArrayList<>();
                                ArrayList<String> foodPrice = new ArrayList<>();
                                ArrayList<String> foodqty = new ArrayList<>();
                                ArrayList<String> orderId = new ArrayList<>();
                                ArrayList<String> userId = new ArrayList<>();
                                String dateTime = postsnapshot.child("date_time").getValue(String.class);
                                String totalCost = String.valueOf(postsnapshot.child("total_cost").getValue(Long.class));
                                String restaurantName = "";
                                String userName = "";
                                String struserId = "";
                                String contact = "";
                                String orderStatus = postsnapshot.child("orderStatus").getValue(String.class);
                                String deliveryAddress = "";

                                for (DataSnapshot i : postsnapshot.getChildren()) {
                                    if (i.getChildrenCount() > 0) {
                                        restaurantName = i.child("restaurantName").getValue(String.class);
                                        struserId = i.child("userId").getValue(String.class);
                                        foodName.add(i.child("foodName").getValue(String.class) != null ? 
                                            i.child("foodName").getValue(String.class) : "");
                                        foodPrice.add(i.child("price").getValue(String.class));
                                        foodqty.add(i.child("quantity").getValue(String.class));
                                        String uid = i.child("userId").getValue(String.class);
                                        userId.add(uid);
                                        
                                        for (User user : userList) {
                                            try {
                                                Log.e("usserlist","useruser: "+user.getAddress()+",mobile:"+user.getMobileNumber());

                                                if (user.getUid().equals(uid)) {
                                                    deliveryAddress = user.getAddress();
                                                    userName = user.getName();
                                                    contact = user.getMobileNumber();
                                                    break;
                                                }
                                            } catch (Exception e) {
                                                Log.e("usserlist",e.toString());
                                            }
                                        }

                                        if (deliveryAddress != null) {
                                            Log.e(uid + "_deliveryAddress", deliveryAddress);
                                        }
                                        orderId.add(i.child("orderId").getValue(String.class) +
                                                "\nPayment Id: " + i.child("onPaymentSuccess").getValue(String.class));
                                    }
                                }

                                OrderHistory orderHistory = new OrderHistory(
                                        restaurantName,
                                        dateTime,
                                        dateTime,
                                        totalCost,
                                        foodName,
                                        foodqty,
                                        foodPrice,
                                        orderId,
                                        userName + "," + deliveryAddress,
                                        contact,
                                        orderStatus,
                                        struserId
                                );
                                foodList.add(orderHistory);
                            }

                            if (sharedPreferences.getBoolean("newToOld", false)) {
                                ArrayList<OrderHistory> reversedList = new ArrayList<>();
                                for (int i = foodList.size() - 1; i >= 0; i--) {
                                    reversedList.add(foodList.get(i));
                                }
                                foodList = reversedList;
                            }

                            recyclerAdapter = new AdminOrderAdapter(getActivity(), foodList);
                            recyclerOrder.setAdapter(recyclerAdapter);
                            recyclerOrder.setLayoutManager(layoutManager);
                        } catch (Exception e) {
                            Log.e("errr'", e.toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Not implemented
                    }
                });
    }

    private void setonClickListener() {
        recyclerOrder.addOnItemTouchListener(new RecyclerTouchListener(
                requireActivity(),
                recyclerOrder,
                new RecyclerTouchListener.ClickListener() {
                    @Override
                    public void onClick(View view, int position) {

                        String token = "";
                        String userIdFromOrder = foodList.get(position).getStruserId();
                        for (User user : userList) {
                            if (user.getUid() != null && user.getUid().equals(userIdFromOrder)) {
                                token = user.getFirebaseToken();
                                break;
                            }
                        }
                        Log.e("Order_Debug", "Token for Order: " + token);
                        if (foodList.get(position).getOrderId() != null) {
                            Log.e("orderIdStatus", foodList.get(position).getOrderId().toString());
                        }
                        selectStatus(position);
                    }

                    @Override
                    public void onLongClick(View view, int position) {
                    }
                }));
    }

    private void selectStatus(int position) {
        final String[] selectedFruits = {fruits[selectedFruitsIndex]};
        new MaterialAlertDialogBuilder(requireActivity())
                .setTitle("select status for:" + foodList.get(position).getDeliveryAddress())
                .setSingleChoiceItems(fruits, selectedFruitsIndex, (dialog_, which) -> {
                    selectedFruitsIndex = which;
                    selectedFruits[0] = fruits[which];
                })
                .setPositiveButton("SUBMIT", (dialog, which) -> {
                    updateStatus(position, String.valueOf(selectedFruitsIndex));
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void updateStatus(int position, String selectedFruitsIndex) {
        FirebaseDatabase.getInstance().getReference()
                .child(Constants.DBItemOrderName)
                .child(foodList.get(position).getOrderDate())
                .child("orderStatus")
                .setValue(selectedFruitsIndex); //pending= 0 , dispatch=1, delivered=2
        
        OrderHistory food = foodList.get(position);
        food.setOrderStatus(selectedFruitsIndex);
        foodList.set(position, food);
        sendToUser(position, selectedFruitsIndex);
    }

    private void sendToUser(int position, String selectedFruitsIndex) {
        String token = "";
        String uname = "";
        String orderUserId = foodList.get(position).getStruserId();
        for (User user : userList) {
            if (user.getUid() != null && user.getUid().equals(orderUserId)) {
                token = user.getFirebaseToken();
                uname = user.getName();
                break;
            }
        }
        Log.e("Admin_Debug", "Sending Notification to: " + uname + " | Token: " + token);

        String appname = requireActivity().getResources().getString(R.string.app_name);
        NOTIFICATION_TITLE = appname + " Order Update";

        TOPIC = "/topics/userABC"; //topic must match with what the receiver subscribed to


        
        if ("0".equals(selectedFruitsIndex)) {
            NOTIFICATION_MESSAGE = "You order is accepted and dispatching soon";
        } else if ("1".equals(selectedFruitsIndex)) {
            NOTIFICATION_MESSAGE = "Hi " + uname + ", your " + appname + " order is out for delivery. ";
        } else if ("2".equals(selectedFruitsIndex)) {
            NOTIFICATION_MESSAGE = "Hi " + uname + "! Your package has been delivered. Let us know what you think by replying with a 1-5 rating on play store.";
        }
        if (token != null && !token.isEmpty()) {
            sendNotificationToUser(token, NOTIFICATION_TITLE, NOTIFICATION_MESSAGE);
        } else {
            Log.e("Notification_Error", "Token is null, cannot send notification");
        }
    }

    private void sendNotificationToUser(String token, String NOTIFICATION_TITLE, String NOTIFICATION_MESSAGE) {
        String imgUrl = "https://firebasestorage.googleapis.com/v0/b/fir-db-bafc7.appspot.com/o/banner_notis.jpg?alt=media&token=ea178495-ca5e-46eb-8cb0-60be3974f8fa";
        RootModel rootModel = new RootModel(
                token,
                new NotificationModel(NOTIFICATION_TITLE, NOTIFICATION_MESSAGE, imgUrl),
                new DataModel(NOTIFICATION_TITLE, NOTIFICATION_MESSAGE)
        );
        
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> responseBodyCall = apiService.sendNotification(rootModel);

        responseBodyCall.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.e(TAG, "Successfully notification send by using retrofit.");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
}