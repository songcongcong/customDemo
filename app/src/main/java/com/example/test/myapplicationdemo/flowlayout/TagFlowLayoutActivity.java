package com.example.test.myapplicationdemo.flowlayout;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test.myapplicationdemo.R;
import com.example.test.myapplicationdemo.flowlayout.adapter.TagAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TagFlowLayoutActivity extends AppCompatActivity {

    private TagFlowLayout mTag;
    private static final List<String> mDateList = Arrays.asList("Android", "hyman", "imooc.com", "Android", "hyman", "imooc.com", "Android", "hyman", "imooc.com", "Android", "hyman", "imooc.com",
            "Android", "hyman", "imooc.com", "Android", "hyman", "imooc.com", "Android", "hyman", "imooc.com", "Android", "hyman", "imooc.com",
            "Android", "hyman", "imooc.com", "Android", "hyman", "imooc.com", "Android", "hyman", "imooc.com");

    private TagAdapter mAdapter;
    private List<String> mList = new ArrayList<>(mDateList);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_flow_layout);
        mTag = findViewById(R.id.tagflowlayout);
        Button mChange = findViewById(R.id.btn_change);

        mTag.setmMaxSelectCount(3);
        mTag.setmAdapter(mAdapter = new TagAdapter() {
            @Override
            public int getItemCount() {
                return mList.size();
            }

            @Override
            public View createView(LayoutInflater inflater, ViewGroup parent, int position) {
                return inflater.inflate(R.layout.selector_tag, parent, false);
            }

            @Override
            public void bindView(View view, int position) {
                TextView mTag = view.findViewById(R.id.tv_tag);
                mTag.setText(mList.get(position));
            }

            @Override
            public void onItemViewClick(View v, int position) {
                Toast.makeText(v.getContext(), "最多选择：" + mTag.getSelectedItemPositionList(), Toast.LENGTH_LONG).show();

            }

            @Override
            public void tipForSelectMax(View v, int mMaxSelectCount) {
                super.tipForSelectMax(v, mMaxSelectCount);
                Toast.makeText(v.getContext(), "最多选择：" + mMaxSelectCount, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onItemSelested(View v, int position) {
                super.onItemSelested(v, position);
                TextView mTag = v.findViewById(R.id.tv_tag);
                mTag.setTextColor(Color.RED);
            }

            @Override
            public void onItemUnSelested(View v, int position) {
                super.onItemUnSelested(v, position);
                TextView mTag = v.findViewById(R.id.tv_tag);
                mTag.setTextColor(Color.BLACK);
            }

        });

        mChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mList.clear();
                    mList.addAll(new ArrayList<>(Arrays.asList("爱词霸英语为广大网友提供在线翻译","爱词霸英语为广大网友提供在线翻译","爱词霸英语为广大网友提供在线翻译",
                            "爱词霸英语为广大网友提供在线翻译","爱词霸英语为广大网友提供在线翻译","爱词霸英语为广大网友提供在线翻译","爱词霸英语为广大网友提供在线翻译",
                            "爱词霸英语为广大网友提供在线翻译","爱词霸英语为广大网友提供在线翻译","爱词霸英语为广大网友提供在线翻译","爱词霸英语为广大网友提供在线翻译")));

                    mTag.setmMaxSelectCount(1);
                    mAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("song","异常："+e.toString());
                }

            }
        });
    }
}
