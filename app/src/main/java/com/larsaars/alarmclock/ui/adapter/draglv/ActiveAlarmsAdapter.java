package com.larsaars.alarmclock.ui.adapter.draglv;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.larsaars.alarmclock.R;
import com.larsaars.alarmclock.ui.view.AnimatedTextView;
import com.larsaars.alarmclock.utils.alarm.Alarm;
import com.larsaars.alarmclock.utils.alarm.AlarmController;
import com.woxthebox.draglistview.DragItemAdapter;

import java.util.ArrayList;

public class ActiveAlarmsAdapter extends DragItemAdapter<Alarm, ActiveAlarmsAdapter.ViewHolder> {

    public ActiveAlarmsAdapter(Context context) {
        setItemList(new ArrayList<>(AlarmController.activeAlarms(context)));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_active_alarm, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Alarm alarm = mItemList.get(position);
        holder.tv.set(alarm.formatToText());
        // set as view tag the alarm in order to retrieve it in the on click actions
        holder.itemView.setTag(alarm);
    }

    @Override
    public long getUniqueItemId(int position) {
        return mItemList.get(position).id;
    }

    static class ViewHolder extends DragItemAdapter.ViewHolder {
        AnimatedTextView tv;

        ViewHolder(final View itemView) {
            super(itemView, R.id.itemActiveAlarmText, true);
            tv = itemView.findViewById(R.id.itemActiveAlarmText);
            tv.slideOnChange = false;
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