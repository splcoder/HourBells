package com.spl.hourbells.classes;

import android.content.Context;
import android.media.MediaPlayer;

import com.spl.hourbells.R;

import java.io.IOException;

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
public class HourBellsPlayer {

	public final int TIME_LAPSE = 1250;

	private MediaPlayer[] mp = new MediaPlayer[ 2 ];
	private Context context = null;

	public HourBellsPlayer( Context context ){
		this.context = context;
		if( this.context != null ){
			mp[ 0 ] = MediaPlayer.create( this.context, R.raw.tibet_bell_0 );
			mp[ 1 ] = MediaPlayer.create( this.context, R.raw.tibet_bell_1 );

			mp[ 0 ].setVolume( 1.0f, 1.0f );
			mp[ 1 ].setVolume( 1.0f, 1.0f );
		}
	}

	private void stop( MediaPlayer mediaPlayer ){
		mediaPlayer.stop();
		try {
			mediaPlayer.prepare();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sleep(){
		try {
			Thread.sleep( TIME_LAPSE );
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void play( int[] tones ){
		if( tones != null && tones.length > 0 ){
			int i = 0;
			mp[ (tones[ i ] & 1) ].start();
			for( i = 1; i < tones.length; i++ ){
				sleep();
				stop( mp[ (tones[ i - 1 ] & 1) ] );
				mp[ (tones[ i ] & 1) ].start();
			}
			sleep();
			stop( mp[ (tones[ i - 1 ] & 1) ] );
		}
	}

	public void release(){
		if( mp[ 0 ] != null ){
			mp[ 0 ].release();
			mp[ 1 ].release();
		}
	}
}