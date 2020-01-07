package com.example.test.myapplicationdemo.flowlayout.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.test.myapplicationdemo.R;

import java.util.List;

/**
 * @author
 * @data 2020/1/2
 */
public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {

    private List<String> mList;
    private Context mContext;

    public RecycleAdapter(List<String> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = View.inflate(mContext, R.layout.item_tag, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder myViewHolder, int i) {
        myViewHolder.mTag.setText(mList.get(i));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mTag;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTag = itemView.findViewById(R.id.tv_tag);
        }
    }
}
