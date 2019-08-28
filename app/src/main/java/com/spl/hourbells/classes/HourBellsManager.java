package com.spl.hourbells.classes;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import es.dmoral.toasty.Toasty;

/**
 2 sounds: for the 0 and 1:

 1 bit	0	12:00, 24:00 = 00:00
 		1	13:00, 01:00
 2 bit	00 = 0	<<< NO		<<< TAKE	20:00, 08:00
 		01 = 1	<<< NO		<<< TAKE	21:00, 09:00
 		10	14:00, 02:00
 		11	15:00, 03:00
 3 bit	000 = 0	<<< NO		<<< TAKE	22:00, 10:00
 		001 = 1	<<< NO		<<< TAKE	23:00, 11:00
 		010 = 2	<<< NO		<<< TAKE MID HOURS
 		011 = 3	<<< NO		<<< ADVISE
 		100	16:00, 04:00
 		101	17:00, 05:00
 		110	18:00, 06:00
 		111	19:00, 07:00
 */
public class HourBellsManager {

	public static final int ALARM_REQUEST_CODE		= 555;
	public static final int ONE_HOUR_MILLIS			= 3600000;		// 1000*60*60

	public static final String SHARED_PREFERENCES	= "shared";

	public static final String ACTIVE				= "active";		// If the alarm is active or not
	public static final String SHORT_MODE			= "short";
	public static final String HALF_HOURS			= "hh";
	public static final String UTC_MODE				= "utc";
	public static final String SOUND_TYPE			= "sound";

	public static final int SOUND_TIBET_BELL		= 0;
	public static final int SOUND_CUCKOO			= 1;
	public static final int SOUND_GUN_SILENCER		= 2;
	public static final int SOUND_PUNCH				= 3;
	public static final int SOUND_CAT				= 4;
	public static final int SOUND_DOG				= 5;

	public static ArrayList<String> getSoundTypes(){
		ArrayList<String> listSoundTypes = new ArrayList<>();
		listSoundTypes.add( "TIBET_BELL" );
		listSoundTypes.add( "CUCKOO" );
		listSoundTypes.add( "GUN_SILENCER" );
		listSoundTypes.add( "PUNCH" );
		listSoundTypes.add( "CAT" );
		listSoundTypes.add( "DOG" );
		return listSoundTypes;
	}

	public static boolean getActive( Context context ){
		return context.getSharedPreferences( SHARED_PREFERENCES, Context.MODE_PRIVATE ).getBoolean( ACTIVE, false );
	}
	public static boolean getShortMode( Context context ){
		return context.getSharedPreferences( SHARED_PREFERENCES, Context.MODE_PRIVATE ).getBoolean( SHORT_MODE, false );
	}
	public static boolean getHalfHoursMode( Context context ){
		return context.getSharedPreferences( SHARED_PREFERENCES, Context.MODE_PRIVATE ).getBoolean( HALF_HOURS, false );
	}
	public static boolean getUtcMode( Context context ){
		return context.getSharedPreferences( SHARED_PREFERENCES, Context.MODE_PRIVATE ).getBoolean( UTC_MODE, false );
	}
	public static int getSoundType( Context context ){
		return context.getSharedPreferences( SHARED_PREFERENCES, Context.MODE_PRIVATE ).getInt( SOUND_TYPE, SOUND_TIBET_BELL );
	}

	public static void setActive( Context context, boolean isChecked ){
		context.getSharedPreferences( SHARED_PREFERENCES, Context.MODE_PRIVATE )
				.edit()
				.putBoolean( ACTIVE, isChecked )
				.commit();
	}
	public static void setShortMode( Context context, boolean isChecked ){
		context.getSharedPreferences( SHARED_PREFERENCES, Context.MODE_PRIVATE )
				.edit()
				.putBoolean( SHORT_MODE, isChecked )
				.commit();
	}
	public static void setHalfHoursMode( Context context, boolean isChecked ){
		context.getSharedPreferences( SHARED_PREFERENCES, Context.MODE_PRIVATE )
				.edit()
				.putBoolean( HALF_HOURS, isChecked )
				.commit();
	}
	public static void setUtcMode( Context context, boolean isChecked ){
		context.getSharedPreferences( SHARED_PREFERENCES, Context.MODE_PRIVATE )
				.edit()
				.putBoolean( UTC_MODE, isChecked )
				.commit();
	}
	public static void setSoundType( Context context, int soundType ){
		context.getSharedPreferences( SHARED_PREFERENCES, Context.MODE_PRIVATE )
				.edit()
				.putInt( SOUND_TYPE, soundType )
				.commit();
	}

	public static void createAlarm( Context context ){
		AlarmManager alarmManager = (AlarmManager) context.getSystemService( Context.ALARM_SERVICE );
		Intent intent = new Intent( context, HourBellsReceiver.class );
		intent.setAction( HourBellsReceiver.ACTION_ALARM );	// <<<

		// Pass something...:
		// Bundle bundle = new Bundle();
		// bundle.putString("TAG", "FOO");
		// intentToFire.putExtra("BUNDLE", bundle);

		PendingIntent pendingIntent = PendingIntent.getBroadcast( context, ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT );

		//long delay = TimeUnit.HOURS.toMillis(10L);
		//long time = System.currentTimeMillis() + delay;

		Calendar calendar = Calendar.getInstance();
		if( getUtcMode( context ) ){
			calendar.setTimeInMillis( System.currentTimeMillis() );
		}
		setNextAlarm( calendar, getHalfHoursMode( context ) );

		//alarmManager.setExact( AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent );
		if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
			//alarmManager.setAndAllowWhileIdle( AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent );
			alarmManager.setAndAllowWhileIdle( AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent );
		}
		else if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
			// Wakes up the device in Idle Mode
			alarmManager.setExact( AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent );
		} else {
			// Old APIs
			alarmManager.setExact( AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent );
		}
		//Toasty.success( context, "The alarm was created", Toast.LENGTH_SHORT, true ).show();
		setActive( context, true );
	}

	public static void cancelAlarm( Context context ){
		// TODO NO ME DETECTA QUE ESTÉ ACTIVO !!!
		AlarmManager alarmManager = (AlarmManager) context.getSystemService( Context.ALARM_SERVICE );
		Intent intent = new Intent( context, HourBellsReceiver.class );
		// With FLAG_NO_CREATE it will return null if the PendingIntent doesn't already exist. If it already exists it returns reference to the existing PendingIntent
		PendingIntent pendingIntent = PendingIntent.getBroadcast( context, ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_NO_CREATE );
		if( pendingIntent != null ){
			alarmManager.cancel( pendingIntent );
			Toasty.success( context, "The alarm was canceled", Toast.LENGTH_SHORT, true ).show();
			//setActive( context, false );
		}
		else{
			Toasty.warning( context, "No alarm was set before !", Toast.LENGTH_SHORT, true ).show();
		}
		setActive( context, false );
	}

	private static void setNextAlarm( Calendar calendar, boolean withHalfHours ){
		// Get first actual hour and minutes
		int hour = calendar.get( Calendar.HOUR_OF_DAY );
		int minutes = calendar.get( Calendar.MINUTE );
		if( minutes < 29 ){
			if( withHalfHours ){
				calendar.set( Calendar.MINUTE, 30 );
			}
			else{
				if( hour == 23 )	calendar.setTimeInMillis( calendar.getTimeInMillis() + (60 - minutes)*60*1000 );
				else				calendar.set( Calendar.HOUR_OF_DAY, ++hour );
				calendar.set( Calendar.MINUTE, 0 );
			}
		}
		else if( minutes < 59 ){
			if( hour == 23 )	calendar.setTimeInMillis( calendar.getTimeInMillis() + (60 - minutes)*60*1000 );
			else				calendar.set( Calendar.HOUR_OF_DAY, ++hour );
			calendar.set( Calendar.MINUTE, 0 );
		}
		else{	// 59' X''
			if( withHalfHours ){
				if( hour == 23 )	calendar.setTimeInMillis( calendar.getTimeInMillis() + 120*1000 );
				else				calendar.set( Calendar.HOUR_OF_DAY, ++hour );
				calendar.set( Calendar.MINUTE, 30 );
			}
			else{
				calendar.setTimeInMillis( calendar.getTimeInMillis() + ONE_HOUR_MILLIS + 120*1000 );
				calendar.set( Calendar.MINUTE, 0 );
			}
		}
		calendar.set( Calendar.SECOND, 0 );
	}

	public static int[] getNotes( Context context ){
		int[] notes = null;
		//-----------------
		boolean useShort = getShortMode( context );
		boolean useHalfHours = getHalfHoursMode( context );
		boolean useUTC = getUtcMode( context );
		//
		Calendar calendar = Calendar.getInstance();
		if( useUTC ){
			calendar.setTimeInMillis( System.currentTimeMillis() );
		}
		int minutes = calendar.get( Calendar.MINUTE );
		if( minutes == 30 ){
			if( useHalfHours )	notes = new int[]{ 0, 1, 0 };
		}
		else if( minutes == 0 ){
			int hour = calendar.get( Calendar.HOUR );
			switch( hour ){
				case 0:		notes = new int[]{ 0 };				break;
				case 1:		notes = new int[]{ 1 };				break;
				case 2:		notes = new int[]{ 1, 0 };			break;
				case 3:		notes = new int[]{ 1, 1 };			break;
				case 4:		notes = new int[]{ 1, 0, 0 };		break;
				case 5:		notes = new int[]{ 1, 0, 1 };		break;
				case 6:		notes = new int[]{ 1, 1, 0 };		break;
				case 7:		notes = new int[]{ 1, 1, 1 };		break;
				case 8: {
					if(useShort)	notes = new int[]{ 0, 0 };
					else			notes = new int[]{ 1, 0, 0, 0 };
					break;
				}
				case 9: {
					if(useShort)	notes = new int[]{ 0, 1 };
					else			notes = new int[]{ 1, 0, 0, 1 };
					break;
				}
				case 10: {
					if(useShort)	notes = new int[]{ 0, 0, 0 };
					else			notes = new int[]{ 1, 0, 1, 0 };
					break;
				}
				case 11: {
					if(useShort)	notes = new int[]{ 0, 0, 1 };
					else			notes = new int[]{ 1, 0, 1, 1 };
					break;
				}
			}
		}
		return notes;
	}
}
