package com.example.test.myapplicationdemo.flowlayout.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.example.test.myapplicationdemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author scc
 * @data 2019/12/31
 * 自定义view流式布局，继承viewgroup，相当于根布局
 * flowlayout用于xml文件中
 */

/**
 * 自定义  view
 */
public class FlowCustomLayout extends ViewGroup {
    // 表示一行一行的数据，一行一行的遍历
    private List<List<View>> mViewAllList = new ArrayList<>();
    // 表示第一行多高，第二行多高，每一行的行高
    private List<Integer> mLineHeightList = new ArrayList<>();
    // 使用系统自带的值来实现自定义view
    private static final int[] lien = new int[]{android.R.attr.maxLines};
    private final int lineMax;

    public FlowCustomLayout(Context context, AttributeSet attrs) {
        super(context, attrs);



//        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FlowLayout);
//        int lineMax = typedArray.getInt(R.styleable.FlowLayout_maxLines, Integer.MAX_VALUE);

        // 使用系统自带的属性添加自定义view属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs, lien);
        lineMax = typedArray.getInt(0, Integer.MAX_VALUE);
        typedArray.recycle(); // 释放一下
        Log.d("song", "lineMax = " + lineMax);

    }

    /**
     * 使用onmeasure方法之前先确定它的使用场景
     * flowlayout :宽度：是一定确定的
     * 高度：wrapcontent,exactly,unspe
     *
     * @param widthMeasureSpec  建议宽度 + mode
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewAllList.clear();;
        mLineHeightList.clear();

        // widthMeasureSpec 两种信息
        //1、 建议宽度 + mode
        // 1、比如我设置300dp  + exactly(他是一个精确地值)
        // 2、 parent width   + at_most （最大不能超过patent）
        // 3、 0 ,parent width   +  unspecified(一般用于可滑动view比如scrollview; 它以自己的测量宽度为准，不需要参考建议宽度，无论传什么值都不影响)

        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec); // 宽度一定是确定的
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);

        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec); // 传入的建议值
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
        int lienWith = 0;
        int lineHeight = 0; //比如有三个子view，高度不相等，取三个里面子view最大的高度，累加起来等于height
        int height = 0;
        //根据子view来确定高度
        int childCount = getChildCount();
        List<View> lineViews = new ArrayList<>();
        // 遍历的目的：拿到当前所有child需要占据的高度，设置给容器
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            // gone情况下
            if (childAt.getVisibility() == View.GONE) {
                continue;
            }
            // childat也要确定宽高
            // child 的是通过mode +size
            // 通过两种方式来确定的 1、 xml里面自己定义的值  2、父控件当前的mode
            // 父控件也会调用这个方法，就需要调用setMeasuredDimension方法
            measureChild(childAt, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams layoutParams = (MarginLayoutParams) childAt.getLayoutParams(); // 添加间距
            // 子view的宽和高
            int measuredWidth = childAt.getMeasuredWidth() + layoutParams.leftMargin + layoutParams.rightMargin;
            int measuredHeight = childAt.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin;

            // 判断当前宽度的和  > 容器的宽度 ，就换行  不考虑padding值 lienWith + measuredWidth > sizeWidth
            // 考虑有padding值之后做如下修改
            // lienWith + measuredWidth > sizeWidth - (getPaddingLeft()+ getPaddingRight())
            if (lienWith + measuredWidth > sizeWidth - (getPaddingLeft()+ getPaddingRight())) {
                // 换行处理
                height += lineHeight; //每一行最大的行高累加，height 是整个容器的高度
                mLineHeightList.add(lineHeight);

                mViewAllList.add(lineViews);
                lineViews = new ArrayList<>();
                lineViews.add(childAt);  // 已经换行了，当前的child不能丢

                lienWith = measuredWidth; // lienWith等于当前child，已经换了新一行
                lineHeight = measuredHeight; // 换了新的一行需要重置高度

            } else {
                // 累加处理
                lienWith += measuredWidth; // 如果超过容器宽度进行换行，否则就将宽度相加
                lineHeight = Math.max(lineHeight, measuredHeight); // 取当前一行里view最大的高度

                // 将当前view添加集合
                lineViews.add(childAt);

            }
            // 最后一行始终满足不换行的处理，所以height不计算最后一行的高度
            if (i == childCount - 1) {
                height += lineHeight;
                mLineHeightList.add(lineHeight);
                mViewAllList.add(lineViews);
            }
        }
        // 矫正高度
        if (lineMax < mLineHeightList.size()) {
            height = getMacLineHeight();
        }


        // 可以前移优化
        if (modeHeight == MeasureSpec.EXACTLY) { // 考虑padding，精确值不会受影响
            height = sizeHeight;
        } else if (modeHeight == MeasureSpec.AT_MOST) {
            height = Math.min(sizeHeight, height); // 最大值不能超过容器设定的高度
            // 考虑padding
            height = height + getPaddingTop() + getPaddingBottom();
        } else if (modeHeight == MeasureSpec.UNSPECIFIED) {
            // 考虑padding
            height = height + getPaddingTop() + getPaddingBottom();
        }
        setMeasuredDimension(widthMeasureSpec, height);
    }
    // 高度
    private int getMacLineHeight(){
        int height = 0;
        for (int i = 0; i < lineMax; i++) {
            height += mLineHeightList.get(i);
        }
        return height;
    }
    

    // 确定view位于父控件的位置
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // 摆放view
        /**
         * 考虑padding 摆放第一个元素
         *  int left = getPaddingLeft();
         *  int top = getPaddingTop();
         */
        /**
         * 不考虑padding
         * int left = 0;
         * int top = 0;
         */
        int left = getPaddingLeft();
        int top = getPaddingTop();
        int lineNums = mViewAllList.size(); // 总行数
        for (int i = 0; i < lineNums; i++) {
            // 当前每一行的view
            List<View> viewList = mViewAllList.get(i);
            // 当前行的高度
            int lineHeight = mLineHeightList.get(i);
            // 遍历每一行的view
            for (int j = 0; j < viewList.size(); j++) {
                View child = viewList.get(j); // 得到每一个子child
                MarginLayoutParams layoutParams = (MarginLayoutParams) child.getLayoutParams();
                // 摆放确定四个位置，left, top,right, bottom,
                int lc = left + layoutParams.leftMargin;
                int tc = top + layoutParams.topMargin;
                int rc = lc + child.getMeasuredWidth();
                int bc = tc + child.getMeasuredHeight();

                child.layout(lc, tc, rc, bc);
                left += child.getMeasuredWidth() + layoutParams.leftMargin + layoutParams.rightMargin;
            }
            /**
             * 不考虑padding
             * left = 0;
             */
            /**
             * 考虑padding
             * left = getPaddingLeft();
             */
            left = getPaddingLeft();
            top += lineHeight;
        }
    }

    /**
     * 有的时候view是直接new出来的，
     * 如果动态添加view，没有设置layoutparams，就会调用这个方法
     *
     * @return
     */
    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    /**
     * 会读取xml里面的属性信息
     *
     * @param attrs
     * @return
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    /**
     * addview 判断当前 LayoutParams不是我们所要的，会把LayoutParams传进来转换一下
     * @param p
     * @return
     */
    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    /**
     * addview 判断当前 LayoutParams不是我们所要的，会把LayoutParams传进来转换一下
     * @param p
     * @return
     */
    @Override
    protected boolean checkLayoutParams(LayoutParams p) {
        return p instanceof  MarginLayoutParams;
    }
}
