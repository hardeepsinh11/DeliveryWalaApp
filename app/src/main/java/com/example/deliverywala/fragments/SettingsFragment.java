package com.example.deliverywala.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import com.example.deliverywala.R;
import com.example.deliverywala.activities.EditAccountActivity;
import com.example.deliverywala.activities.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SettingsFragment extends Fragment {
    private static final String CHANNEL_ID = "my_channel_id";
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_NAME = "My Channel";
    
    private Button btnClearHistory;
    private Button btnDeleteAccount;
    private Button btnEditAccount;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, 
                           @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        
        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = requireContext().getSharedPreferences(
            getString(R.string.preference_file_name),
            Context.MODE_PRIVATE
        );
        
        btnEditAccount = view.findViewById(R.id.btnEditAccount);
        btnClearHistory = view.findViewById(R.id.btnClearHistory);
        btnDeleteAccount = view.findViewById(R.id.btnDeleteAccount);
        
        btnEditAccount.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), EditAccountActivity.class);
            startActivity(intent);
        });
        
        btnDeleteAccount.setOnClickListener(v -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
            dialog.setTitle("Delete Account?");
            dialog.setMessage("Are you sure , you want to delete your account?");
            dialog.setPositiveButton("Yes", (dialogInterface, which) -> {
                String userId = mAuth.getCurrentUser().getUid();
                FirebaseDatabase.getInstance().getReference("user")
                    .child(userId).removeValue();
                
                mAuth.getCurrentUser().delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            showNotification(requireActivity(), 
                                "Your Account Deleted Successfully",
                                "We are sorry to see you go!☹️😥  If you ever decide to come back, we'll be here for you");
                            Toast.makeText(getContext(), 
                                "Your account deleted", Toast.LENGTH_SHORT).show();
                        }
                    });
                
                sharedPreferences.edit().putBoolean("isLoggedIn", false).apply();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            });
            dialog.setNegativeButton("No", (dialogInterface, which) -> {});
            dialog.setIcon(R.drawable.ic_profile);
            dialog.create().show();
        });
        
        btnClearHistory.setOnClickListener(v -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(requireActivity());
            dialog.setTitle("Clear History?");
            dialog.setMessage("History will be cleared permanently");
            dialog.setPositiveButton("Yes", (dialogInterface, which) -> {
                String userId = mAuth.getCurrentUser().getUid();
                FirebaseDatabase.getInstance().getReference("user")
                    .child(userId).child("orders").removeValue();
            });
            dialog.setNegativeButton("No", (dialogInterface, which) -> {});
            dialog.create().show();
        });
        
        return view;
    }

    private void showNotification(Context context, String title, String message) {
        NotificationManager notificationManager = 
            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }
        
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_logo_b_round)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true);
        
        notificationManager.notify(NOTIFICATION_ID, notification.build());
    }
}