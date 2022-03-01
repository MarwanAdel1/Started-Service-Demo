package com.example.seventhday;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent nextIntent = new Intent(context.getApplicationContext(), NewActivity.class);
        nextIntent.putExtra("file",intent.getStringExtra("file"));
        nextIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(nextIntent);

    }
}