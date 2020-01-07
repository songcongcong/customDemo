package com.example.test.myapplicationdemo.flowlayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.example.test.myapplicationdemo.R;
import com.example.test.myapplicationdemo.flowlayout.adapter.RecycleAdapter;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FlexBoxLayoutManagerActivity extends AppCompatActivity {

    private RecyclerView mRecycle;
    private List<String> mList = new ArrayList<>();
    private static final List<String> mDateList = Arrays.asList("Android","hyman","imooc.com","Android","hyman","imooc.com","Android","hyman","imooc.com","Android","hyman","imooc.com",
            "Android","hyman","imooc.com","Android","hyman","imooc.com","Android","hyman","imooc.com","Android","hyman","imooc.com",
            "Android","hyman","imooc.com","Android","hyman","imooc.com","Android","hyman","imooc.com");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flex_box_layout_manager);
        mRecycle = findViewById(R.id.recycleview);
        mList.clear();
        for (int i = 0; i < 400; i++) {
            mList.add(addTag());
        }
        RecycleAdapter recycleAdapter = new RecycleAdapter(mList, this);
        mRecycle.setLayoutManager(new FlexboxLayoutManager(this));
        mRecycle.setAdapter(recycleAdapter);

    }

    private String addTag() {
     return   mDateList.get((int) (Math.random() * mDateList.size()-1)); // 下标设置随机数，遍历也可以
    }
}