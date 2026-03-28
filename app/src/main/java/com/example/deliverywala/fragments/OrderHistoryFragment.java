package com.example.deliverywala.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
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
import com.example.deliverywala.adapters.OrderHistoryRecyclerAdapter;
import com.example.deliverywala.model.OrderHistory;
import com.example.deliverywala.util.ConnectionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class OrderHistoryFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    private RecyclerView recyclerOrder;
    private RecyclerView.LayoutManager layoutManager;
    private OrderHistoryRecyclerAdapter recyclerAdapter;
    private ArrayList<OrderHistory> foodList = new ArrayList<>();
    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_order_history, container, false);

        recyclerOrder = view.findViewById(R.id.recyclerOrder);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerOrder.setLayoutManager(layoutManager);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        sharedPreferences = requireContext().getSharedPreferences(
                getString(R.string.preference_file_name),
                Context.MODE_PRIVATE
        );

        // ૧. સીધો મેઈન પાથ પકડો (તારીખ કે યુઝર આઈડી નહીં)
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("FoodOrderDetails");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                foodList.clear();

                // ૨. આ લૂપ દરેક "તારીખ" વાળા ફોલ્ડર (Order) પર ફરશે
                for (DataSnapshot postsnapshot : snapshot.getChildren()) {

                    String orderUid = "";

                    // ૩. યુઝર આઈડી ક્યાં છે એ શોધો (તારા ડેટામાં બે જગ્યાએ હોઈ શકે છે)
                    if (postsnapshot.hasChild("userId")) {
                        // જો સીધું બહાર હોય
                        orderUid = String.valueOf(postsnapshot.child("userId").getValue());
                    } else if (postsnapshot.child("0").hasChild("userId")) {
                        // જો આઈટમ "0" ની અંદર હોય
                        orderUid = String.valueOf(postsnapshot.child("0").child("userId").getValue());
                    }

                    // ૪. જો આ ઓર્ડર અત્યારે લોગિન થયેલા યુઝરનો જ હોય, તો જ તેને લિસ્ટમાં લો
                    if (orderUid.equals(userId)) {

                        ArrayList<String> fNames = new ArrayList<>();
                        ArrayList<String> fPrices = new ArrayList<>();
                        ArrayList<String> fQtys = new ArrayList<>();
                        ArrayList<String> oIds = new ArrayList<>();

                        String dateTime = String.valueOf(postsnapshot.child("date_time").getValue());
                        String totalCost = String.valueOf(postsnapshot.child("total_cost").getValue());
                        String status = String.valueOf(postsnapshot.child("orderStatus").getValue());
                        String resName = "";

                        // આઈટમ્સ (0, 1, 2...) માટે લૂપ
                        for (DataSnapshot item : postsnapshot.getChildren()) {
                            if (item.hasChild("foodName")) {
                                resName = String.valueOf(item.child("restaurantName").getValue());
                                fNames.add(String.valueOf(item.child("foodName").getValue()));
                                fPrices.add(String.valueOf(item.child("price").getValue()));
                                fQtys.add(String.valueOf(item.child("quantity").getValue()));
                                oIds.add(String.valueOf(item.child("orderId").getValue()));
                            }
                        }

                        OrderHistory history = new OrderHistory(
                                resName, dateTime, dateTime, totalCost,
                                fNames, fQtys, fPrices, oIds, "", "", status, userId
                        );
                        foodList.add(history);
                    }
                }

                // ૫. એડેપ્ટર અપડેટ કરો
                if (getActivity() != null && !foodList.isEmpty()) {
                    recyclerAdapter = new OrderHistoryRecyclerAdapter(getActivity(), foodList);
                    recyclerOrder.setAdapter(recyclerAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return view;
    }

}