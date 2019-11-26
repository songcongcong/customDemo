package com.example.test.myapplicationdemo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * @author
 * @data 2019/11/25
 */
public class HomeFragment extends Fragment {

    private RecyclerView mRecycle;
    private MyAdapter myAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getContext(),R.layout.fragment_home_layout, null);
        mRecycle = view.findViewById(R.id.recycle);
        EventBus.getDefault().register(this);
        init();
        return view;
    }

    /**
     * 初始化数据
     */
    private void init() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add("第 "+i+" 项");
        }
        myAdapter = new MyAdapter(list, getContext());
        mRecycle.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycle.setAdapter(myAdapter);
    }

    @Subscribe
    public void mainEvent(DateEventBus dateEventBus) {
        if (dateEventBus != null) {
            if (dateEventBus.isDisaddpear()) {
                myAdapter.setCancel(dateEventBus.isDisaddpear());
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
