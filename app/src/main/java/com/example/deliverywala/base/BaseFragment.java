package com.example.deliverywala.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.LayoutRes;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.DefaultLifecycleObserver;


public abstract class BaseFragment extends Fragment implements DefaultLifecycleObserver {

    @LayoutRes
    public abstract int layoutId();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(layoutId(), container, false);
    }
}