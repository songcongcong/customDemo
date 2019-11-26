package com.example.test.myapplicationdemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import org.greenrobot.eventbus.EventBus;

public class QQMessageBubbleView extends View {
    /**
     * 画圆的Paint画笔
     */
    Paint mPaint;
    /**
     * 字体画笔
     */
    Paint textPaint;
    /**
     * 消失图片的画笔
     */
    Paint disappearPaint;
    /**
     * 贝塞尔曲线
     */
    Path mPath;
    /**
     * 字体垂直偏移量
     */
    float textMove;
    /**
     * 中心圆半径，初始位的圆
     */
    float centerRadius;
    /**
     * 拖拽圆的半径
     */
    float dragRadius;
    /**
     * 手指移动坐标x
     */
    int dragCircleX;
    /**
     * 手指移动坐标y
     */
    int dragCircleY;
    /**
     * 控件中心点坐标x
     */
    int centerCircleX;
    /**
     * 控件中心点坐标y
     */
    int centerCircleY;
    /**
     * 两个圆的距离
     */
    float distance;
    int mWidth;
    int mHeight;
    /**
     * 显示的文本内容
     */
    String mNumber;
    /**
     * 最大可拖拽距离
     */
    int maxDragLength;
    /**
     * 用户设定的字体大小
     */
    float textSize;
    /**
     * 用户设定的字体颜色
     */
    int textColor;
    /**
     * 用户设定的圆圈颜色
     */
    int circleColor;
    /**
     * 图片资源id
     */
    int[] disappearPic;
    /**
     * 图片位图
     */
    Bitmap[] disappearBitmap;
    Rect bitmapRect;
    /**
     * 消失动画播放图片的index
     */
    int bitmapIndex;
    /**
     * 判断是否正在播放消失动画，防止死循环重复绘制
     */
    boolean startDisappear;

    ActionListener actionListener;

    /**
     * 当前状态
     */
    int curState;
    /**
     * 原位，初始位
     */
    public static int STATE_NORMAL = 0;
    /**
     * 消失
     */
    public static int STATE_DISAPPEAR = 1;
    /**
     * 拖拽
     */
    public static int STATE_DRAGING = 2;
    /**
     * 移动（无粘连效果）
     */
    public static int STATE_MOVE = 3;

    public QQMessageBubbleView(Context context) {
        super(context);
    }

    public QQMessageBubbleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MessageBubble);
        circleColor = ta.getColor(R.styleable.MessageBubble_circleColor, Color.RED);
        textColor = ta.getColor(R.styleable.MessageBubble_textColor, Color.WHITE);
        textSize = ta.getDimension(R.styleable.MessageBubble_textSize, sp2px(12.0f));
        centerRadius = ta.getDimension(R.styleable.MessageBubble_radius, dp2px(10.0f));
        mNumber = ta.getString(R.styleable.MessageBubble_number);
        if (mNumber == null) {//防止xml中未给mNumber赋值造成预览时报错
            mNumber = "";
        }
        ta.recycle();
    }

    public QQMessageBubbleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initView();
    }

    private void initView() {
        //画圆的Paint
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//去除抗锯齿
        mPaint.setColor(circleColor);
        //画数字的Paint
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(textColor);
        textPaint.setTextAlign(Paint.Align.CENTER);//文字书写方向
        textPaint.setTextSize(textSize);
        //画消失图片的Paint
        disappearPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        disappearPaint.setFilterBitmap(false);//对位图进行滤波处理
        startDisappear = false;

        Paint.FontMetrics textFontMetrics = textPaint.getFontMetrics();
        textMove = -textFontMetrics.ascent - (-textFontMetrics.ascent + textFontMetrics.descent) / 2;
        // drawText从baseline开始
        // ，baseline的值为0，baseline的上面为负值，baseline的下面为正值，即这里ascent为负值，descent为正值,比如ascent为-20
        // ，descent为5，那需要移动的距离就是20 - （20 + 5）/ 2

        mPath = new Path();
        if (centerRadius <= 2) {
            //如果不是第一次创建，上次的拖动删除会因为中心圆半径随着拖放变为零
            centerRadius = dragRadius;
        } else {
            dragRadius = centerRadius;
        }

        maxDragLength = (int) (4 * dragRadius);

        //设定圆的初始位置为View正中心
        centerCircleX = getWidth() / 2;
        centerCircleY = getHeight() / 2;
        //防止被拖动圆因上一次拖动而未回到原位
        dragCircleX = centerCircleX;
        dragCircleY = centerCircleY;

        //消失气泡图片
        if (disappearPic == null) {
            disappearPic = new int[]{R.drawable.explosion_one, R.drawable.explosion_two
                    , R.drawable.explosion_three
                    , R.drawable.explosion_four, R.drawable.explosion_five
            };
        }
        disappearBitmap = new Bitmap[disappearPic.length];
        for (int i = 0; i < disappearPic.length; i++) {
            disappearBitmap[i] = BitmapFactory.decodeResource(getResources(), disappearPic[i]);
        }
        curState = STATE_NORMAL;
    }


    @Override
    protected void onMeasure(int widthMeasure, int heightMeasure) {
        int widthMode = MeasureSpec.getMode(widthMeasure);
        int widthSize = MeasureSpec.getSize(widthMeasure);
        int heightMode = MeasureSpec.getMode(heightMeasure);
        int heightSize = MeasureSpec.getSize(heightMeasure);

        if (widthMode == MeasureSpec.EXACTLY) {
            mWidth = widthSize;
        } else {
            mWidth = getPaddingLeft() + 400 + getPaddingRight();
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = heightSize;
        } else {
            mHeight = getPaddingTop() + 400 + getPaddingBottom();
        }
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //父布局禁用拦截事件功能，禁用down事件
                getParent().requestDisallowInterceptTouchEvent(true);
                if (curState != STATE_DISAPPEAR) {
                    //计算点击位置与气泡的距离,内部应用勾股定理a²=b²+c²
                    distance = (float) Math.hypot(centerCircleX - event.getX(), centerCircleY - event.getY());
                    if (distance < centerRadius + 10) {
                        curState = STATE_DRAGING;
                    } else {
                        //距离过大不触发拖拽
                        curState = STATE_NORMAL;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                dragCircleX = (int) event.getX();
                dragCircleY = (int) event.getY();
                if (curState == STATE_DRAGING) {
                    //拖拽状态下计算拖拽距离，超出後不再計算
                    distance = (float) Math.hypot(centerCircleX - event.getX(), centerCircleY - event.getY());
                    if (distance <= maxDragLength - maxDragLength / 7) {
                        centerRadius = dragRadius - distance / 4;
                        if (actionListener != null) {
                            actionListener.onDrag();
                        }
                    } else {
                        centerRadius = 0;
                        curState = STATE_MOVE;
                    }
                } else if (curState == STATE_MOVE) {
                    //超出最大拖拽距离，则中间的圆消失
                    if (actionListener != null) {
                        actionListener.onMove();
                    }
                }
                invalidate();

                break;
            case MotionEvent.ACTION_UP:
                getParent().requestDisallowInterceptTouchEvent(false);
                //当正在拖动时，抬起手指才会做响应的处理
                if (curState == STATE_DRAGING || curState == STATE_MOVE) {
                    distance = (float) Math.hypot(centerCircleX - event.getX(), centerCircleY - event.getY());
                    if (distance > maxDragLength) {
                        //如果拖拽距离大于最大可拖拽距离，则消失
                        curState = STATE_DISAPPEAR;
                        startDisappear = true;
                        disappearAnim();
                        EventBus.getDefault().post(new CancleEventBus(true));
                    } else {
                        //小于可拖拽距离，则复原气泡位置
                        restoreAnim();
                    }
                    invalidate();
                }
                break;
        }
        return true;
    }

    private int roundRectRadius;//圆角矩形半径
    private int textContentLength;//总字体宽度

    @Override
    protected void onDraw(Canvas canvas) {
        textContentLength = (int) textPaint.measureText(mNumber, 0, mNumber.length()) + 20;//计算内容的长度,想要圆角矩形越宽将20该为更大值
        //原位
        if (curState == STATE_NORMAL) {
            roundRectRadius = (int) Math.max(centerRadius * 2, textContentLength); //圆角矩形宽度
            if (roundRectRadius != textContentLength) {
                //画原位圆
                canvas.drawCircle(centerCircleX, centerCircleY, centerRadius, mPaint);
            } else {
                //画圆角矩形
                canvas.drawRoundRect(new RectF(centerCircleX - roundRectRadius / 2, centerCircleY - centerRadius,
                        centerCircleX + roundRectRadius / 2, centerCircleY + centerRadius), centerRadius, centerRadius, mPaint);
            }
            //画数字（要在画完贝塞尔曲线之后绘制，不然会被挡住）
            canvas.drawText(mNumber, centerCircleX, centerCircleY + textMove, textPaint);
        }
        //如果开始拖拽，则画dragCircle
        if (curState == STATE_DRAGING) {
            //画初始时停留的圆
            canvas.drawCircle(centerCircleX, centerCircleY, centerRadius, mPaint);
            //画被拖拽的圆
            roundRectRadius = (int) Math.max(dragRadius * 2, textContentLength);
            if (roundRectRadius != textContentLength) {
                canvas.drawCircle(dragCircleX, dragCircleY, dragRadius, mPaint);
            } else {
                canvas.drawRoundRect(new RectF(dragCircleX - roundRectRadius / 2, dragCircleY - dragRadius,
                        dragCircleX + roundRectRadius / 2, dragCircleY + dragRadius), dragRadius, dragRadius, mPaint);
            }
            drawBezier(canvas);//贝塞尔黏连效果
            canvas.drawText(mNumber, dragCircleX, dragCircleY + textMove, textPaint);
        }
        // 移动（无粘连效果）
        if (curState == STATE_MOVE) {
            roundRectRadius = (int) Math.max(dragRadius * 2, textContentLength);
            if (roundRectRadius != textContentLength) {
                canvas.drawCircle(dragCircleX, dragCircleY, dragRadius, mPaint);
            } else {
                canvas.drawRoundRect(new RectF(dragCircleX - roundRectRadius / 2, dragCircleY - dragRadius,
                        dragCircleX + roundRectRadius / 2, dragCircleY + dragRadius), dragRadius, dragRadius, mPaint);
            }
            canvas.drawText(mNumber, dragCircleX, dragCircleY + textMove, textPaint);
        }
        //消失,通过属性动画动态设置bitmap实现动画效果
        if (curState == STATE_DISAPPEAR && startDisappear) {
            if (disappearBitmap != null) {
                canvas.drawBitmap(disappearBitmap[bitmapIndex], null, bitmapRect, disappearPaint);
            }
        }

    }


    /**
     * 气泡消失动画,动画采用类似帧动画的原理
     */
    public void disappearAnim() {
        bitmapRect = new Rect(dragCircleX - (int) dragRadius, dragCircleY - (int) dragRadius,
                dragCircleX + (int) dragRadius, dragCircleY + (int) dragRadius);
        ValueAnimator disappearAnimator = ValueAnimator.ofInt(0, disappearBitmap.length);
        disappearAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                bitmapIndex = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        disappearAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                startDisappear = false;
                if (actionListener != null) {
                    actionListener.onDisappear();
                }
            }
        });
        disappearAnimator.setInterpolator(new LinearInterpolator());
        disappearAnimator.setDuration(500);
        disappearAnimator.start();
    }


    /**
     * 气泡消失动画,动画采用类似帧动画的原理
     */
    public void disappearAnimtion(int[] bitmap) {
//        bitmapRect = new Rect(dragCircleX - (int) dragRadius, dragCircleY - (int) dragRadius,
//                dragCircleX + (int) dragRadius, dragCircleY + (int) dragRadius);
        ValueAnimator disappearAnimator = ValueAnimator.ofInt(0, bitmap.length);
        disappearAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
//                bitmapIndex = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        disappearAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                startDisappear = false;
                if (actionListener != null) {
                    actionListener.onDisappear();
                }
            }
        });
        disappearAnimator.setInterpolator(new LinearInterpolator());
        disappearAnimator.setDuration(500);
        disappearAnimator.start();
    }

    /**
     * 气泡复原动画
     */
    private void restoreAnim() {
        ValueAnimator valueAnimator = ValueAnimator.ofObject(new MyPointFEvaluator(),
                new PointF(dragCircleX, dragCircleY), new PointF(centerCircleX, centerCircleY));
        valueAnimator.setDuration(200);
        //自定义插值器
        valueAnimator.setInterpolator(new TimeInterpolator() {
            @Override
            public float getInterpolation(float input) {
                float f = 0.571429f;
                return (float) (Math.pow(2, -4 * input) * Math.sin((input - f / 4) * (2 * Math.PI) / f) + 1);
            }
        });
        //系统插值器，运动到终点后，冲过终点后再回弹
//        valueAnimator.setInterpolator(new OvershootInterpolator(5));
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                PointF pointF = (PointF) animation.getAnimatedValue();
                dragCircleX = (int) pointF.x;
                dragCircleY = (int) pointF.y;
                invalidate();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                //复原了
                centerRadius = dragRadius;
                curState = STATE_NORMAL;
                if (actionListener != null) {
                    actionListener.onRestore();
                }
            }
        });
        valueAnimator.start();
    }

    /**
     * 绘制贝塞尔曲线
     *
     * @param canvas canvas
     */
    private void drawBezier(Canvas canvas) {
        float controlX = (centerCircleX + dragCircleX) / 2;//贝塞尔曲线控制点X坐标
        float controlY = (dragCircleY + centerCircleY) / 2;//贝塞尔曲线控制点Y坐标
        //计算曲线的起点终点
        distance = (float) Math.hypot(centerCircleX - dragCircleX, centerCircleY - dragCircleY);
        float sin = (centerCircleY - dragCircleY) / distance;
        float cos = (centerCircleX - dragCircleX) / distance;
        //计算第一条贝塞尔曲线起点终点坐标
        float dragCircleStartX = dragCircleX - dragRadius * sin;
        float dragCircleStartY = dragCircleY + dragRadius * cos;
        float centerCircleEndX = centerCircleX - centerRadius * sin;
        float centerCircleEndY = centerCircleY + centerRadius * cos;
        //计算第二条贝塞尔曲线起点终点坐标
        float centerCircleStartX = centerCircleX + centerRadius * sin;
        float centerCircleStartY = centerCircleY - centerRadius * cos;
        float dragCircleEndX = dragCircleX + dragRadius * sin;
        float dragCircleEndY = dragCircleY - dragRadius * cos;

        mPath.reset();
        mPath.moveTo(centerCircleStartX, centerCircleStartY);
        mPath.quadTo(controlX, controlY, dragCircleEndX, dragCircleEndY);
        mPath.lineTo(dragCircleStartX, dragCircleStartY);
        mPath.quadTo(controlX, controlY, centerCircleEndX, centerCircleEndY);
        mPath.close();

        canvas.drawPath(mPath, mPaint);
    }


    /**
     * 重置
     */
    public void resetBezierView() {
        initView();
        invalidate();
    }

    /**
     * 设置显示的消息数量(超过99需要自己定义为"99+")
     *
     * @param number 消息的数量
     */
    public void setNumber(String number) {
        mNumber = number;
        invalidate();
    }

    /**
     * 设置消失动画
     *
     * @param disappearPic
     */
    public void setDisappearPic(int[] disappearPic) {
        if (disappearPic != null) {
            this.disappearPic = disappearPic;
        }
    }

    /**
     * 自定义监听，对外接口
     */
    public interface ActionListener {
        /**
         * 被拖动时
         */
        void onDrag();

        /**
         * 消失后
         */
        void onDisappear();

        /**
         * 拖动距离不足，气泡回到原位后
         */
        void onRestore();

        /**
         * 拖动时超出了最大粘连距离，气泡单独移动时
         */
        void onMove();
    }

    public void setOnActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    /**
     * PointF动画估值器(复原时的振动动画)
     */
    private class MyPointFEvaluator implements TypeEvaluator<PointF> {

        @Override
        public PointF evaluate(float fraction, PointF startPointF, PointF endPointF) {
            float x = startPointF.x + fraction * (endPointF.x - startPointF.x);
            float y = startPointF.y + fraction * (endPointF.y - startPointF.y);
            return new PointF(x, y);
        }
    }

    public float dp2px(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public float sp2px(float sp) {
        final float scale = getResources().getDisplayMetrics().scaledDensity;
        return sp * scale;
    }
}
