package com.ex.FG002.OpenSource;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;


public class NetworkChangeListener extends BroadcastReceiver {

    public static boolean syncStatus;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!ConnectionStatus.isConnected(context)) {
            syncStatus = false;
            //not connected
            Toast.makeText(context, "not connected to internet", Toast.LENGTH_SHORT).show();
        } else {
            syncStatus = true;
            Toast.makeText(context, "conected to internet", Toast.LENGTH_SHORT).show();
        }
    }


}
