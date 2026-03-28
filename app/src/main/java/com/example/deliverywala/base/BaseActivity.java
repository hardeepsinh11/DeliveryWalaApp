package com.example.deliverywala.base;

import android.os.Bundle;
import androidx.annotation.CallSuper;
import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;


public abstract class BaseActivity extends AppCompatActivity {


    @LayoutRes
    public abstract int layoutId();

    @CallSuper
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutId());
    }
}