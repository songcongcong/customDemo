package com.example.test.myapplicationdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MainActivity extends AppCompatActivity {

    private HomeFragment homeFragment;
    private MyFragment myFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        TextView mHome = findViewById(R.id.tv_home);
        TextView mMy = findViewById(R.id.tv_my);
        one();
        mHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                one();
            }
        });
        mMy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                two();
            }
        });

    }

    @Subscribe
    public void mainEvent(CancleEventBus cancleEventBus) {
        if (cancleEventBus != null) {
            if (cancleEventBus.isDisaddpear()) {
                EventBus.getDefault().post(new DateEventBus(true));
            }
        }
    }
    public void one() {
        if (homeFragment == null) {
            homeFragment = new HomeFragment();
        }
        FragmentUtil.showFragment(this, R.id.rame, homeFragment);
    }

    public void two() {
        if (myFragment == null) {
            myFragment = new MyFragment();
        }
        FragmentUtil.showFragment(this, R.id.rame, myFragment);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
