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

	public HourBellsPlayer( Context context, int soundType ){
		this.context = context;
		if( this.context != null ){
			switch( soundType ){
				case HourBellsManager.SOUND_TIBET_BELL: {
					mp[ 0 ] = MediaPlayer.create( this.context, R.raw.tibet_bell_0 );
					mp[ 1 ] = MediaPlayer.create( this.context, R.raw.tibet_bell_1 );
					break;
				}
				case HourBellsManager.SOUND_CUCKOO: {
					mp[ 0 ] = MediaPlayer.create( this.context, R.raw.cuco );
					mp[ 1 ] = MediaPlayer.create( this.context, R.raw.cuckoo_1 );
					break;
				}
				case HourBellsManager.SOUND_GUN_SILENCER: {
					mp[ 0 ] = MediaPlayer.create( this.context, R.raw.gun_silencer_2 );
					mp[ 1 ] = MediaPlayer.create( this.context, R.raw.gun_silencer_1 );
					break;
				}
				case HourBellsManager.SOUND_PUNCH: {
					mp[ 0 ] = MediaPlayer.create( this.context, R.raw.upper_cut );
					mp[ 1 ] = MediaPlayer.create( this.context, R.raw.jab );

					//mp[ 0 ] = MediaPlayer.create( this.context, R.raw.right_cross );
					//mp[ 1 ] = MediaPlayer.create( this.context, R.raw.left_hook );
					break;
				}
				case HourBellsManager.SOUND_CAT: {
					mp[ 0 ] = MediaPlayer.create( this.context, R.raw.cat_0 );
					mp[ 1 ] = MediaPlayer.create( this.context, R.raw.cat_1 );
					break;
				}
				case HourBellsManager.SOUND_DOG: {
					mp[ 0 ] = MediaPlayer.create( this.context, R.raw.dog_0 );
					mp[ 1 ] = MediaPlayer.create( this.context, R.raw.dog_1 );
					break;
				}
			}

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
