package com.example.test.myapplicationdemo.flowlayout.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @author
 * @data 2019/12/31
 */
public class FlowLayout2 extends ViewGroup {
    private List<List<View>>  mAllViews = new ArrayList<>(); //所有的view
    private List<Integer> mLineHeight = new ArrayList<>(); // 每一行的高度

    public FlowLayout2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 测量view的宽高
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 防止重复添加，造成view过多，添加超出屏幕之外，就清空集合，每次从最新的添加
        mAllViews.clear();
        mLineHeight.clear();

        //首先得到宽度，模式；高度，模式
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);

        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
        int lineWidth = 0; // 用来计算子view的宽度
        int lineHeight = 0; // 用来计算子view的高度
        int height = 0; // 整个view的总体高度
        // 流式布局宽度是一定的，计算出高度
        // 得到子view的高度
        int childCount = getChildCount();
        // 创建集合用来存放每一个子view
        List<View> childViews = new ArrayList<>();
        // 主要为了确定view的高度
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i); // 得到每一个子view

            // 子view也需要设置高度
            measureChild(childAt, widthMeasureSpec, heightMeasureSpec);
            // 设置子view的间距  ---MarginLayoutParams 在代码中设置间距
            MarginLayoutParams lp = (MarginLayoutParams) childAt.getLayoutParams();
            // 子view的宽和高
            int childWidth = childAt.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = childAt.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

            // 判断子view的宽度和 > 容器的宽度就换行
            if (lineWidth + childWidth > sizeWidth) {
                // 换行
                height += lineHeight;  // lineHeight 每一行的高度，height 是整体的高度
                mLineHeight.add(lineHeight);

                mAllViews.add(childViews);
                childViews = new ArrayList<>();
                childViews.add(childAt); // 换行之后保存当前的子view

                // 换行之后需要对宽度重置
                lineWidth = childWidth;
                lineHeight = childHeight;
            } else {
                // 不换行
                lineWidth += childWidth;

                // 获取view当中的高度最高的view值
                lineHeight = Math.max(lineHeight, childHeight);

                childViews.add(childAt);
            }
            // 当最后一行的时候：因为每一行的高度是在未换行的时候添加的，所以最后一行每次都添加不上，这样就要特殊处理
            if (i == childCount - 1) {
                height += lineHeight;
                mLineHeight.add(lineHeight);
                mAllViews.add(childViews);
            }
        }

        if (modeHeight == MeasureSpec.EXACTLY) {
            height = sizeHeight;
        } else if (modeHeight == MeasureSpec.AT_MOST) {
            height = Math.min(sizeHeight, height);// 最大不能超过容器设定的高度
        }
        setMeasuredDimension(widthMeasureSpec, height); //防止父view和子view同时调用measureChild方法
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left = 0;
        int top = 0;
        // 确定子view位于父控件的什么位置，怎么来摆放
        //在这里也需要重新获取一遍子view，所以就用集合来处理，避免重复遍历查找子view
        for (int i = 0; i < mAllViews.size(); i++) {
            // 得到每一行的view
            List<View> viewList = mAllViews.get(i);
            // 每一行的高度
            int lineHeight = mLineHeight.get(i);
            for (int j = 0; j < viewList.size(); j++) {
                View child = viewList.get(j);
                // 确定view的一个位置
               MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

               int lc = left + lp.leftMargin;
               int tc = top + lp.topMargin;
               int rc = lc + child.getMeasuredWidth();
               int bc = tc + child.getMeasuredHeight();
               child.layout(lc, tc, rc, bc);
               left += child.getMeasuredWidth() +lp.leftMargin+lp.rightMargin; // 一行一行的排
            }

            left = 0;
            top +=lineHeight;
        }
    }


    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        // 动态添加view默认宽高掉这个方法
        return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        // xml属性相关
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        // 自定义layoutparams时用来判断是否是我们指定的params
        return new MarginLayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(LayoutParams p) {
        // 如果不是就传进来强转一下
        return p instanceof MarginLayoutParams;
    }
}
