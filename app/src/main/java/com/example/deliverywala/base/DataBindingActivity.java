package com.example.deliverywala.base;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

/**
 * BaseActivity for keeping an instance of ViewDataBinding
 */
public abstract class DataBindingActivity<VB extends ViewDataBinding> extends BaseActivity {
    protected VB vb;

    @Override
    public void setContentView(int layoutResID) {
        vb = DataBindingUtil.inflate(getLayoutInflater(), layoutResID, null, false);
        super.setContentView(vb.getRoot());
    }
}