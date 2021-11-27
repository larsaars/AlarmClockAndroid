package com.larsaars.alarmclock.ui.adapter.draglv;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.larsaars.alarmclock.R;
import com.larsaars.alarmclock.app.activity.MainActivity;
import com.larsaars.alarmclock.app.receiver.DismissUpcomingAlarmReceiver;
import com.larsaars.alarmclock.ui.view.AnimatedTextView;
import com.larsaars.alarmclock.ui.view.clickableiv.ShiftingClickableImageView;
import com.larsaars.alarmclock.utils.Constants;
import com.larsaars.alarmclock.utils.Executable;
import com.larsaars.alarmclock.utils.alarm.Alarm;
import com.larsaars.alarmclock.utils.alarm.AlarmController;
import com.larsaars.alarmclock.utils.settings.Settings;
import com.larsaars.alarmclock.utils.settings.SettingsLoader;
import com.woxthebox.draglistview.DragItemAdapter;

import java.util.ArrayList;

public class ActiveAlarmsAdapter extends DragItemAdapter<Alarm, ActiveAlarmsAdapter.ViewHolder> {

    MainActivity mainActivity;
    Settings settings;

    public ActiveAlarmsAdapter(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.settings = SettingsLoader.load(mainActivity);

        setItemList(new ArrayList<>(AlarmController.activeAlarms(mainActivity)));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alarm, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Alarm alarm = mItemList.get(position);
        holder.tv.setText(alarm.formatToText(mainActivity));
        // set as view tag the alarm in order to retrieve it in the on click actions
        holder.itemView.setTag(alarm);
    }

    @Override
    public long getUniqueItemId(int position) {
        return mItemList.get(position).id;
    }

    class ViewHolder extends DragItemAdapter.ViewHolder {

        AppCompatTextView tv;
        ShiftingClickableImageView minus, plus, delete;

        ViewHolder(final View itemView) {
            super(itemView, R.id.itemActiveAlarmText, true);

            // init views
            tv = itemView.findViewById(R.id.itemActiveAlarmText);

            minus = itemView.findViewById(R.id.itemActiveAlarmMinus);
            plus = itemView.findViewById(R.id.itemActiveAlarmsPlus);
            delete = itemView.findViewById(R.id.itemActiveAlarmsDelete);

            // place on click listeners
            // on each of these actions the whole adapter has to be reloaded afterwards
            // because the active alarms change
            minus.setOnClickListener(v ->
                performAlarmAction(alarm ->
                        AlarmController.scheduleAlarm(mainActivity, null, alarm.time - settings.rescheduleTime, true))
            );

            plus.setOnClickListener(v ->
                performAlarmAction(alarm ->
                        AlarmController.scheduleAlarm(mainActivity, null, alarm.time + settings.rescheduleTime, true))
            );

            delete.setOnClickListener(v -> performAlarmAction(null));
        }

        void performAlarmAction(Executable<Alarm> actionBeforeRemoveAndUpdate) {
            // get alarm from view tag
            Alarm alarm = (Alarm) itemView.getTag();

            // perform given action
            if (actionBeforeRemoveAndUpdate != null)
                actionBeforeRemoveAndUpdate.run(alarm);

            // remove the active alarm, remove upcoming alarm notification and update dataset
            // do this by calling the according receiver
            Intent dismissIntent = new Intent(mainActivity, DismissUpcomingAlarmReceiver.class);
            dismissIntent.putExtra(Constants.EXTRA_ALARM_ID, alarm.id);
            mainActivity.sendBroadcast(dismissIntent);

            mainActivity.updateActiveAlarms();

        }
    }
}