/*
 *  Created by Lars Specht
 *  Copyright (c) 2021. All rights reserved.
 *  last modified by me on 28.11.21, 02:22
 *  project Alarm Clock in module Alarm_Clock.app
 */

package com.larsaars.alarmclock.ui.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.larsaars.alarmclock.utils.alarm.Alarm;

import java.util.Arrays;
import java.util.List;

public class SortedAlarmAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    protected SortedList<Alarm> alarms;

    public SortedAlarmAdapter() {
        super();
        this.alarms = new SortedList<>(Alarm.class, new SortedList.Callback<Alarm>() {
            @Override
            public int compare(Alarm o1, Alarm o2) {
                return o1.compareTo(o2);
            }

            @Override
            public void onInserted(int position, int count) {
                notifyItemRangeInserted(position, count);
            }

            @Override
            public void onRemoved(int position, int count) {
                notifyItemRangeRemoved(position, count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                notifyItemMoved(fromPosition, toPosition);
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }

            @Override
            public boolean areContentsTheSame(Alarm oldItem, Alarm newItem) {
                // return whether the items' visual representations are the same or not.
                return oldItem.time == newItem.time;
            }

            @Override
            public boolean areItemsTheSame(Alarm o1, Alarm o2) {
                return o1 == o2;
            }
        });
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // do nothing since this shall
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {

    }

    @Override
    public int getItemCount() {
        return alarms.size();
    }

    // region PageList Helpers
    public Alarm get(int position) {
        return alarms.get(position);
    }

    public int add(Alarm item) {
        return alarms.add(item);
    }

    public int indexOf(Alarm item) {
        return alarms.indexOf(item);
    }

    public void updateItemAt(int index, Alarm item) {
        alarms.updateItemAt(index, item);
    }

    public void addAll(List<Alarm> items) {
        alarms.beginBatchedUpdates();
        for (Alarm item : items) {
            alarms.add(item);
        }
        alarms.endBatchedUpdates();
    }

    public void addAll(Alarm[] items) {
        addAll(Arrays.asList(items));
    }

    public boolean remove(Alarm item) {
        return alarms.remove(item);
    }

    public Alarm removeItemAt(int index) {
        return alarms.removeItemAt(index);
    }

    public void clear() {
        alarms.beginBatchedUpdates();
        //remove items at end, to avoid unnecessary array shifting
        while (alarms.size() > 0) {
            alarms.removeItemAt(alarms.size() - 1);
        }
        alarms.endBatchedUpdates();
    }
}
