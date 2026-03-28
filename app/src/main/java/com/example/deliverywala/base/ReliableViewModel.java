package com.example.deliverywala.base;

import android.os.Bundle;
import androidx.lifecycle.LifecycleObserver;

public interface ReliableViewModel extends LifecycleObserver {
    default void writeTo(Bundle bundle) {
        // Default empty implementation
    }

    default void readFrom(Bundle bundle) {
        // Default empty implementation
    }
}