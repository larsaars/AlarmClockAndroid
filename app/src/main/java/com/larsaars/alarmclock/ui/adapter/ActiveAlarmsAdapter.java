package com.larsaars.alarmclock.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.larsaars.alarmclock.R;
import com.larsaars.alarmclock.app.activity.MainActivity;
import com.larsaars.alarmclock.app.receiver.DismissUpcomingAlarmReceiver;
import com.larsaars.alarmclock.ui.view.clickableiv.ShiftingClickableImageView;
import com.larsaars.alarmclock.utils.Constants;
import com.larsaars.alarmclock.utils.Executable;
import com.larsaars.alarmclock.utils.alarm.Alarm;
import com.larsaars.alarmclock.utils.alarm.AlarmController;
import com.larsaars.alarmclock.utils.settings.Settings;
import com.larsaars.alarmclock.utils.settings.SettingsLoader;

import java.util.Collections;
import java.util.List;

public class ActiveAlarmsAdapter extends RecyclerView.Adapter<ActiveAlarmsAdapter.ViewHolder> {

    MainActivity mainActivity;
    Settings settings;
    public List<Alarm> alarms;

    public ActiveAlarmsAdapter(MainActivity mainActivity) {
        super();
        this.mainActivity = mainActivity;
        this.settings = SettingsLoader.load(mainActivity);

        alarms = AlarmController.activeAlarms(mainActivity);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void reloadAlarmsList() {
        // add all items to old instance, sort
        alarms.clear();
        alarms.addAll(AlarmController.activeAlarms(mainActivity));
        Collections.sort(alarms);
        // notify the recycler view the data set has been updated
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alarm, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return alarms.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Alarm alarm = alarms.get(position);
        holder.tv.setText(alarm.formatToText(mainActivity));
        // set as view tag the alarm in order to retrieve it in the on click actions
        holder.itemView.setTag(alarm);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView tv;
        ShiftingClickableImageView minus, plus, delete;

        ViewHolder(final View itemView) {
            super(itemView);

            // init views
            tv = itemView.findViewById(R.id.itemAlarmText);

            minus = itemView.findViewById(R.id.itemAlarmMinus);
            plus = itemView.findViewById(R.id.itemAlarmPlus);
            delete = itemView.findViewById(R.id.itemAlarmDelete);

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

            delete.setOnClickListener(v ->
                    YoYo.with(Techniques.Pulse)
                            .duration(150)
                            .onEnd(animator -> performAlarmAction(null)
                            )
                            .playOn(itemView)
            );
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