package com.rantmedia.hito.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TemperatureCheckBroadcastReceiver extends BroadcastReceiver {

    public static final int REQUEST_CODE = 12345;
    public static final String ACTION = "com.rantmedia.hito.android.alarm";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Intent i = new Intent(context, TemperatureCheckIntentService.class);
        context.startService(i);
    }
}
