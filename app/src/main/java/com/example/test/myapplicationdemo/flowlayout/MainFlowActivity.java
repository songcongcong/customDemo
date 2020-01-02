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
        // 流式布局
        mFlow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainFlowActivity.this, FlowActivity.class));
            }
        });
    }
}
