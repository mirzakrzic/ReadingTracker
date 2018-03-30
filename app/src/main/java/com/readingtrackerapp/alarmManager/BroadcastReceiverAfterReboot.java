package com.readingtrackerapp.alarmManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Anes on 3/30/2018.
 */

public class BroadcastReceiverAfterReboot extends BroadcastReceiver {

    // started after every reboot
    // alarms are removed after reboot
    // alarms should be set again

    @Override
    public void onReceive(Context context, Intent intent) {

        // starting intent service for setting alarms for notifications after reboot
        Intent service = new Intent(context, AfterRebootService.class);
        context.startService(service);

    }
}
