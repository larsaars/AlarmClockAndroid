package com.larsaars.alarmclock.ui.adapter;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.larsaars.alarmclock.R;
import com.larsaars.alarmclock.utils.alarm.Alarm;
import com.woxthebox.draglistview.DragItemAdapter;

import java.util.ArrayList;

public class DragLVItemAdapter extends DragItemAdapter<Alarm, DragLVItemAdapter.ViewHolder> {

    final int mLayoutId;
    final int mGrabHandleId;
    final boolean mDragOnLongPress;

    public DragLVItemAdapter(ArrayList<Alarm> list, int layoutId, int grabHandleId, boolean dragOnLongPress) {
        mLayoutId = layoutId;
        mGrabHandleId = grabHandleId;
        mDragOnLongPress = dragOnLongPress;
        setItemList(list);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        String text = formatText(mItemList.get(position));
        holder.mText.setText(text);
        holder.itemView.setTag(mItemList.get(position));
    }

    // show differend
    String formatText(Alarm alarm) {

    }

    @Override
    public long getUniqueItemId(int position) {
        return mItemList.get(position).id;
    }

    class ViewHolder extends DragItemAdapter.ViewHolder {
        TextView mText;

        ViewHolder(final View itemView) {
            super(itemView, mGrabHandleId, mDragOnLongPress);
            mText = (TextView) itemView.findViewById(R.id.text);
        }

        @Override
        public void onItemClicked(View view) {
            Toast.makeText(view.getContext(), "Item clicked", Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onItemLongClicked(View view) {
            Toast.makeText(view.getContext(), "Item long clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
    }
}