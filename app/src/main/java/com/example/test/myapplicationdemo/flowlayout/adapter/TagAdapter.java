package com.example.test.myapplicationdemo.flowlayout.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author
 * @data 2020/1/2
 */
public abstract class TagAdapter {
    public abstract int getItemCount();

    public abstract View createView(LayoutInflater inflater, ViewGroup parent, int position);

    public abstract void bindView(View view, int position);

    public void onItemViewClick(View v, int position) {
    };

    public void tipForSelectMax(View v, int mMaxSelectCount) {
    };

    public void onItemSelested(View v, int position) {
    };

    public void onItemUnSelested(View v, int position) {
    };


    public OnDataSetChangedListener onDataSetChangedListener;
    public static interface  OnDataSetChangedListener{
        void onDataChanged();
    }
    public void setOnDataSetChangedListener(OnDataSetChangedListener dataSetChangedListener) {
       onDataSetChangedListener = dataSetChangedListener;
    }

    public void notifyDataSetChanged() {
        // 数据发生变化，去通知刷新
        if (onDataSetChangedListener != null) {
            onDataSetChangedListener.onDataChanged();
        }
    };
}
