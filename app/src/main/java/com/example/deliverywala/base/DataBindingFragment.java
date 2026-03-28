package com.example.deliverywala.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

/**
 * BaseFragment for keeping an instance of ViewDataBinding
 */
public abstract class DataBindingFragment<VB extends ViewDataBinding> extends BaseFragment {
    protected VB vb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vb = DataBindingUtil.inflate(inflater, layoutId(), container, false);
        return vb.getRoot();
    }
}