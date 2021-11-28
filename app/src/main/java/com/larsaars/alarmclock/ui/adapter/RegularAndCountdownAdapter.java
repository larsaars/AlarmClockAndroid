package com.larsaars.alarmclock.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
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

import java.util.List;

public class RegularAndCountdownAdapter extends RecyclerView.Adapter<RegularAndCountdownAdapter.ViewHolder> {

    Settings settings;
    MainActivity mainActivity;

    List<Alarm> alarms;

    public RegularAndCountdownAdapter(MainActivity mainActivity, List<Alarm> alarms) {
        super();
        this.mainActivity = mainActivity;
        this.alarms = alarms;
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

        Alarm alarm = alarms.get(position);
        holder.tv.setText(alarm.formatToText(mainActivity));
        // set as view tag the alarm in order to retrieve it in the on click actions
        holder.itemView.setTag(alarm);
    }

    @Override
    public int getItemCount() {
        return alarms.size();
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
                int index = alarms.indexOf(alarm);

                alarm.time = Math.max(0, alarm.time - settings.rescheduleTime);

                RegularAndCountdownAdapter.this.notifyItemChanged(index);
            });

            plus.setOnClickListener(v -> {
                Alarm alarm = (Alarm) itemView.getTag();
                int index = alarms.indexOf(alarm);

                alarm.time = Math.min(Constants.HOUR * 24, alarm.time + settings.rescheduleTime);

                RegularAndCountdownAdapter.this.notifyItemChanged(index);
            });

            // on delete play animation, then notify of removing item
            delete.setOnClickListener(v -> {
                Alarm alarm = (Alarm) itemView.getTag();
                int index = alarms.indexOf(alarm);

                alarms.remove(index);

                YoYo.with(Techniques.Pulse)
                        .duration(150)
                        .onEnd(animator -> RegularAndCountdownAdapter.this.notifyItemRemoved(index))
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