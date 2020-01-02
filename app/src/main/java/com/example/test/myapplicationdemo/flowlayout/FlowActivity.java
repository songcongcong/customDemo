package com.example.test.myapplicationdemo.flowlayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.test.myapplicationdemo.R;
import com.example.test.myapplicationdemo.flowlayout.custom.FlowCustomLayout;

import java.util.Arrays;
import java.util.List;

public class FlowActivity extends AppCompatActivity {

    private static final List<String> mDateList = Arrays.asList("Android","hyman","imooc.com","Android","hyman","imooc.com","Android","hyman","imooc.com","Android","hyman","imooc.com",
            "Android","hyman","imooc.com","Android","hyman","imooc.com","Android","hyman","imooc.com","Android","hyman","imooc.com",
            "Android","hyman","imooc.com","Android","hyman","imooc.com","Android","hyman","imooc.com");
    private FlowCustomLayout mFlowLayout;
    private Button mAddTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flow2);
        mFlowLayout = findViewById(R.id.flow_layout);
        mAddTag = findViewById(R.id.btn_add_tag);
//        getflowLayout();
        // 每次点击按钮添加一个tag（TextView）
        mAddTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTag();
            }
        });



    }
    // 添加流式布局
    private void getflowLayout(){
        Button button = new Button(this);
        button.setText("add");
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        button.setLayoutParams(layoutParams);
        mFlowLayout.addView(button);
    }

    private void addTag() {
        // 添加inflate(R.layout.item_tag, mFlowLayout, false); 付父布局时必须加上第三个参数为false，将xml转化为view
        TextView mTag = (TextView) LayoutInflater.from(this).inflate(R.layout.item_tag, mFlowLayout, false);
        mTag.setText(mDateList.get((int) (Math.random() * mDateList.size()-1))); // 下标设置随机数，遍历也可以
        mFlowLayout.addView(mTag);
    }
}
