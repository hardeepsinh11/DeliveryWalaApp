package com.example.deliverywala.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.deliverywala.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {
    private TextView txtName;
    private TextView txtPhoneNumber;
    private TextView txtAddress;
    private TextView txtEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, 
                           @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        txtName = view.findViewById(R.id.txtName);
        txtPhoneNumber = view.findViewById(R.id.txtPhoneNumber);
        txtAddress = view.findViewById(R.id.txtAddress);
        txtEmail = view.findViewById(R.id.txtEmail);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("user").child(userId);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue(String.class);
                String email = snapshot.child("email").getValue(String.class);
                String mobileNumber = snapshot.child("mobileNumber").getValue(String.class);
                String address = snapshot.child("address").getValue(String.class);

                txtName.setText(name);
                txtEmail.setText(email);
                txtAddress.setText(address);
                txtPhoneNumber.setText(mobileNumber);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error reading data: " + error.getMessage());
            }
        });

        return view;
    }
}