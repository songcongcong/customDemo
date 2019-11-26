package com.example.test.myapplicationdemo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.PointerIcon;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * @author
 * @data 2019/11/26
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private List<String> mList;
    private Context context;

    private boolean isCancel;
    int[] disappearPic = new int[]{R.drawable.explosion_one, R.drawable.explosion_two
            , R.drawable.explosion_three
            , R.drawable.explosion_four, R.drawable.explosion_five
    };

    public MyAdapter(List<String> mList, Context context) {
        this.mList = mList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = View.inflate(context, R.layout.adapter_item_layout, null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int i) {
        viewHolder.mItem.setText(mList.get(i));

        /**
         * 未实现qq的连带爆炸效果
         */
        viewHolder.messageBubbleView.setDisappearPic(disappearPic);
        if (isCancel) {
            viewHolder.messageBubbleView.disappearAnimtion(disappearPic);
            viewHolder.messageBubbleView.invalidate();
            EventBus.getDefault().post(new DateEventBus(false));
        }
    }

    public void setCancel(boolean cancel) {
        isCancel = cancel;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView mItem;
        private final QQMessageBubbleView messageBubbleView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mItem = itemView.findViewById(R.id.tv_item);
            messageBubbleView = itemView.findViewById(R.id.bubbview);
        }
    }
}
