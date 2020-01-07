package com.example.test.myapplicationdemo.flowlayout;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.test.myapplicationdemo.R;

public class MainFlowActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flow);
        Button mFlow = findViewById(R.id.btn_flow);
        Button mGoFlexBox = findViewById(R.id.btn_go_flex);
        Button mFlexManager = findViewById(R.id.btn_manager);
        Button mTagFlow = findViewById(R.id.btn_tag_layout);
        // 流式布局
        mFlow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainFlowActivity.this, FlowActivity.class));
            }
        });
        // FlexBoxLayout
        mGoFlexBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainFlowActivity.this, FixBoxLayoutActivity.class));
            }
        });
        // 结合recycleview使用
        mFlexManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainFlowActivity.this, FlexBoxLayoutManagerActivity.class));
            }
        });

        // tagflowlayout
        mTagFlow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainFlowActivity.this, TagFlowLayoutActivity.class));

            }
        });
    }
}
