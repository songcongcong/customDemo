package com.example.test.myapplicationdemo.flowlayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.test.myapplicationdemo.R;
import com.google.android.flexbox.FlexboxLayout;

import java.util.Arrays;
import java.util.List;

public class FixBoxLayoutActivity extends AppCompatActivity {

    private FlexboxLayout mFlexBox;
    private Button mFlex;
    private static final List<String> mDateList = Arrays.asList("Android","hyman","imooc.com","Android","hyman","imooc.com","Android","hyman","imooc.com","Android","hyman","imooc.com",
            "Android","hyman","imooc.com","Android","hyman","imooc.com","Android","hyman","imooc.com","Android","hyman","imooc.com",
            "Android","hyman","imooc.com","Android","hyman","imooc.com","Android","hyman","imooc.com");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fix_box_layout);
        mFlexBox = findViewById(R.id.flexbox);
        mFlex = findViewById(R.id.btn_flex);
        mFlex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTag();
            }
        });
    }

    private void addTag() {
        // 添加inflate(R.layout.item_tag, mFlowLayout, false); 付父布局时必须加上第三个参数为false，将xml转化为view
        TextView mTag = (TextView) LayoutInflater.from(this).inflate(R.layout.item_tag, mFlexBox, false);
        mTag.setText(mDateList.get((int) (Math.random() * mDateList.size()-1))); // 下标设置随机数，遍历也可以
        mFlexBox.addView(mTag);
    }
}
