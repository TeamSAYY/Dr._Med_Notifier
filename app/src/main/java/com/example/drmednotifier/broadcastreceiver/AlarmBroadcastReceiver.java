package com.example.drmednotifier.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.example.drmednotifier.data.Medication;
import com.example.drmednotifier.service.AlarmService;
import com.example.drmednotifier.service.RescheduleAlarmsService;

import java.util.Calendar;
import java.util.Random;

public class AlarmBroadcastReceiver extends BroadcastReceiver {
    public static final String MED_ID = "MED_ID";
    public static final String MONDAY = "MONDAY";
    public static final String TUESDAY = "TUESDAY";
    public static final String WEDNESDAY = "WEDNESDAY";
    public static final String THURSDAY = "THURSDAY";
    public static final String FRIDAY = "FRIDAY";
    public static final String SATURDAY = "SATURDAY";
    public static final String SUNDAY = "SUNDAY";
    public static final String RECURRING = "RECURRING";
    public static final String TITLE = "TITLE";
    public static final String MED_NAME = "MED_NAME";
    public static final String MED_DOSE = "MED_DOSE";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            String toastText = String.format("Alarm Reboot");
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
            startRescheduleAlarmsService(context);
        }
        else {
            String toastText = String.format("Alarm Received");
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
            if (!intent.getBooleanExtra(RECURRING, false)) {
                startAlarmService(context, intent);
            } else  {
                if (alarmIsToday(intent)) {
                    startAlarmService(context, intent);
                }

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                Medication medication = new Medication(
                        intent.getIntExtra(MED_ID, new Random().nextInt(Integer.MAX_VALUE)),
                        intent.getStringExtra(MED_NAME),
                        "",
                        0,
                        0,
                        System.currentTimeMillis(),
                        intent.getBooleanExtra(MONDAY, true),
                        intent.getBooleanExtra(TUESDAY, true),
                        intent.getBooleanExtra(WEDNESDAY, true),
                        intent.getBooleanExtra(THURSDAY, true),
                        intent.getBooleanExtra(FRIDAY, true),
                        intent.getBooleanExtra(SATURDAY, true),
                        intent.getBooleanExtra(SUNDAY, true),
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        intent.getIntExtra(MED_DOSE, 0)
                );
                medication.schedule(context);

                Log.d("myTag", String.format("New alarm scheduled: %02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));
            }
        }
    }

    private boolean alarmIsToday(Intent intent) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int today = calendar.get(Calendar.DAY_OF_WEEK);

        switch(today) {
            case Calendar.MONDAY:
                if (intent.getBooleanExtra(MONDAY, false))
                    return true;
                return false;
            case Calendar.TUESDAY:
                if (intent.getBooleanExtra(TUESDAY, false))
                    return true;
                return false;
            case Calendar.WEDNESDAY:
                if (intent.getBooleanExtra(WEDNESDAY, false))
                    return true;
                return false;
            case Calendar.THURSDAY:
                if (intent.getBooleanExtra(THURSDAY, false))
                    return true;
                return false;
            case Calendar.FRIDAY:
                if (intent.getBooleanExtra(FRIDAY, false))
                    return true;
                return false;
            case Calendar.SATURDAY:
                if (intent.getBooleanExtra(SATURDAY, false))
                    return true;
                return false;
            case Calendar.SUNDAY:
                if (intent.getBooleanExtra(SUNDAY, false))
                    return true;
                return false;
        }
        return false;
    }

    private void startAlarmService(Context context, Intent intent) {
        Intent intentService = new Intent(context, AlarmService.class);
        intentService.putExtra(TITLE, intent.getStringExtra(TITLE));
        intentService.putExtra(MED_NAME, intent.getStringExtra(MED_NAME));
        intentService.putExtra(MED_DOSE, intent.getIntExtra(MED_DOSE, 0));

        Log.d("myTag", String.format("ALARM NAME: %s", intent.getStringExtra(MED_NAME)));

        Log.d("myTag", String.format("ALARM DOSE: %d", intent.getIntExtra(MED_DOSE, 0)));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentService);
        } else {
            context.startService(intentService);
        }
    }

    private void startRescheduleAlarmsService(Context context) {
        Intent intentService = new Intent(context, RescheduleAlarmsService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentService);
        } else {
            context.startService(intentService);
        }
    }
}
