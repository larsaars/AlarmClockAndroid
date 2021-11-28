package com.larsaars.alarmclock.ui.adapter;

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
import com.larsaars.alarmclock.ui.view.clickableiv.ShiftingClickableImageView;
import com.larsaars.alarmclock.utils.Constants;
import com.larsaars.alarmclock.utils.alarm.Alarm;
import com.larsaars.alarmclock.utils.alarm.AlarmController;
import com.larsaars.alarmclock.utils.settings.Settings;
import com.larsaars.alarmclock.utils.settings.SettingsLoader;

public class RegularAndCountdownAdapter extends SortedAlarmAdapter<RegularAndCountdownAdapter.ViewHolder> {

    Settings settings;
    MainActivity mainActivity;


    public RegularAndCountdownAdapter(MainActivity mainActivity) {
        super();

        this.mainActivity = mainActivity;
        settings = SettingsLoader.load(mainActivity);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alarm, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Alarm alarm = get(position);
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
            minus.setOnClickListener(v -> {
                Alarm alarm = (Alarm) itemView.getTag();
                int index = indexOf(alarm);

                alarm.time = Math.max(0, alarm.time - settings.rescheduleTime);

                updateItemAt(index, alarm);
            });

            plus.setOnClickListener(v -> {
                Alarm alarm = (Alarm) itemView.getTag();
                int index = indexOf(alarm);

                alarm.time = Math.min(Constants.HOUR * 24, alarm.time + settings.rescheduleTime);

                updateItemAt(index, alarm);
            });

            // on delete play animation, then notify of removing item
            delete.setOnClickListener(v -> {
                Alarm alarm = (Alarm) itemView.getTag();

                YoYo.with(Techniques.Pulse)
                        .duration(150)
                        .onEnd(animator -> remove(alarm))
                        .playOn(itemView);
            });

            // on text view click schedule alarm
            tv.setOnClickListener(v -> {
                // animate
                YoYo.with(Techniques.Pulse)
                        .duration(150)
                        .playOn(tv);
                // schedule, conversion to active alarm happens automatically in schedule method
                AlarmController.scheduleAlarm(v.getContext(), (Alarm) itemView.getTag(), -1, true);
                // tell main activity that active alarms have been updated
                mainActivity.updateActiveAlarms();
            });
        }
    }
}