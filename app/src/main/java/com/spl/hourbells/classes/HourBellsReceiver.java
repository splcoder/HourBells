package com.spl.hourbells.classes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

/**
In the manifest -> application:
		<receiver android:name=".classes.HourBellsReceiver">
			<intent-filter>
				<action android:name="me.proft.alarms.ACTION_ALARM"/>

				<action android:name="android.intent.action.BOOT_COMPLETED"/>
				<action android:name="android.intent.action.QUICKBOOT_POWERON" />	<!-- NOT REQUIRED -->
			</intent-filter>
		</receiver>
*/
public class HourBellsReceiver extends BroadcastReceiver {

	public static final String ACTION_ALARM = "com.spl.hourbells.action.ACTION_ALARM";		// unique

	private HourBellsPlayer hourBellsPlayer = null;

	@Override
	public void onReceive( Context context, Intent intent ) {
		if( HourBellsManager.getActive( context ) ) {
			if( ACTION_ALARM.equals( intent.getAction() ) ){
				PowerManager pm = (PowerManager) context.getSystemService( Context.POWER_SERVICE );
				PowerManager.WakeLock wl = pm.newWakeLock( PowerManager.PARTIAL_WAKE_LOCK, "HourBellsReceiver:" );
				// Acquire the lock
				wl.acquire();
				//--------------------------------------------------------------------------------------

				// You can do the processing here.
				int[] notes = HourBellsManager.getNotes( context );
				hourBellsPlayer = new HourBellsPlayer( context );
				hourBellsPlayer.play( notes );
				hourBellsPlayer.release();
				Toasty.success( context, "Bells", Toast.LENGTH_SHORT, true ).show();
				// Create the next alarm
				HourBellsManager.createAlarm( context );

				//--------------------------------------------------------------------------------------
				// Release the lock
				wl.release();
			} else if( intent.getAction().equals( "android.intent.action.BOOT_COMPLETED" ) ){
				// Start alarm
				HourBellsManager.createAlarm( context );
			}
		}
	}
}
