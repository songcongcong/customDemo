package com.example.test.myapplicationdemo.flowlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.example.test.myapplicationdemo.MainActivity;
import com.example.test.myapplicationdemo.flowlayout.adapter.TagAdapter;
import com.example.test.myapplicationdemo.flowlayout.custom.FlowCustomLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 主要做一些tag相关操作
 * @data 2020/1/2
 */
public class TagFlowLayout extends FlowCustomLayout implements TagAdapter.OnDataSetChangedListener {
    // FlowCustomLayout 用来测量  布局  摆放位置  params

    public TagAdapter mAdapter;
    private int mMaxSelectCount;

    public void setmMaxSelectCount(int mMaxSelectCount) {
        this.mMaxSelectCount = mMaxSelectCount;
    }

    public void setmAdapter(TagAdapter mAdapter) {
        this.mAdapter = mAdapter;
        mAdapter.setOnDataSetChangedListener(this);
        onDataChanged();
    }


    /**
     * @param position position
     * @param view     view
     */
    private void bindViewMethod(final int position, View view) {
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.onItemViewClick(v, position);
                if (mMaxSelectCount <= 0) {
                    return;
                }

                // view 状态
                if (!v.isSelected()) {
                    // 未选中
                    if (getSelectViewCount() >= mMaxSelectCount) {
                        // 已经选择到达最大值
                        if (getSelectViewCount() == 1) {
                            // 单选
                            View selectview = getSelectedView();
                            if (selectview != null) {
                                selectview.setSelected(false);
                                mAdapter.onItemUnSelested(selectview, getPositionForChild(selectview));
                            }
                        } else {
                            // 多选
                            mAdapter.tipForSelectMax(v, mMaxSelectCount);
                            return;
                        }
                    }
                }
                if (v.isSelected()) {
                    v.setSelected(false);
                    mAdapter.onItemUnSelested(v, position);
                } else {
                    v.setSelected(true);
                    mAdapter.onItemSelested(v, position);
                }
            }


        });
    }

    private int getPositionForChild(View selectview) {
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            if (childAt == selectview) {
                return i;
            }
        }
        return 0;
    }

    private View getSelectedView() {
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            if (childAt.isSelected()) {
                return childAt;
            }
        }
        return null;
    }

    private int getSelectViewCount() {
        int result = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            if (childAt.isSelected()) {
                result++;
            }
        }
        return result;
    }

    public TagFlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onDataChanged() {
        removeAllViews();
        TagAdapter adapter = mAdapter;
        for (int i = 0; i < adapter.getItemCount(); i++) {
            View view = adapter.createView(LayoutInflater.from(getContext()), this, i);
            adapter.bindView(view, i);
            addView(view);

            if (view.isSelected()) {
                adapter.onItemSelested(view, i);
            } else {
                adapter.onItemUnSelested(view, i);
            }
            // 实现单选，创建方法，实现点击
            bindViewMethod(i, view);
        }
    }

    public List<Integer> getSelectedItemPositionList() {
        List<Integer> selectList = new ArrayList<>();
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            if (childAt.isSelected()) {
                selectList.add(i);
            }
        }
        return selectList;
    }

}
