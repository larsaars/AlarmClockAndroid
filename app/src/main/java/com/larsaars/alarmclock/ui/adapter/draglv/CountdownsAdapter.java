package com.larsaars.alarmclock.ui.adapter.draglv;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.larsaars.alarmclock.R;
import com.larsaars.alarmclock.ui.view.AnimatedTextView;
import com.larsaars.alarmclock.utils.DateUtils;
import com.larsaars.alarmclock.utils.alarm.Alarm;
import com.woxthebox.draglistview.DragItemAdapter;

import java.util.ArrayList;

public class CountdownsAdapter extends DragItemAdapter<Alarm, CountdownsAdapter.ViewHolder> {

    final int mLayoutId;
    final int mGrabHandleId;
    final boolean mDragOnLongPress;

    public CountdownsAdapter(ArrayList<Alarm> list, int layoutId, int grabHandleId, boolean dragOnLongPress) {
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
        Alarm alarm = mItemList.get(position);
        // on binding set formatted text according to the alarm type
        holder.tv.set(formatText(alarm));
        // set as view tag the alarm in order to retrieve it in the on click actions
        holder.itemView.setTag(alarm);
    }

    // show different text for every format of alarm
    public String formatText(Alarm alarm) {
        switch (alarm.alarmType) {
            case REGULAR:
                return DateUtils.formatDuration_HH_mm(alarm.time, DateUtils.DURATION_FORMAT_HH_colon_MM);
            case COUNTDOWN:
                return DateUtils.formatDuration_HH_mm(alarm.time, DateUtils.DURATION_FORMAT_HHhMMm);
            case ACTIVE:
            default:
                return DateUtils.getTimeStringH_mm_a(alarm.time);
        }
    }

    @Override
    public long getUniqueItemId(int position) {
        return mItemList.get(position).id;
    }

    public class ViewHolder extends DragItemAdapter.ViewHolder {
        public AnimatedTextView tv;

        ViewHolder(final View itemView) {
            super(itemView, mGrabHandleId, mDragOnLongPress);
            tv = itemView.findViewById(R.id.text);
        }

        @Override
        public void onItemClicked(View view) {
            Toast.makeText(view.getContext(), "Item clicked" + view.getTag(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onItemLongClicked(View view) {
            Toast.makeText(view.getContext(), "Item long clicked" + view.getTag(), Toast.LENGTH_SHORT).show();
            return true;
        }
    }
}