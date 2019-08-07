package com.spl.hourbells.classes;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

public class DeviceBootReceiver extends BroadcastReceiver {
	@Override
	public void onReceive( Context context, Intent intent ){
		if( intent.getAction().equals( "android.intent.action.BOOT_COMPLETED" ) ){
			// Start alarm
			//HourBellsManager.createAlarm( this );
		}
	}
}
