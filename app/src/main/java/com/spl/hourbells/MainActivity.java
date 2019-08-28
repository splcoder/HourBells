package com.spl.hourbells;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.spl.hourbells.classes.HourBellsManager;
import com.spl.hourbells.classes.HourBellsPlayer;
import com.spl.hourbells.classes.HourBellsReceiver;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, AdapterView.OnItemSelectedListener {

	public final MainActivity that = this;

	Button btnStartAlarms, btnStopAlarms;
	CheckBox cbShort, cbHalfHour, cbUTC;
	Button btnCheckBell0, btnCheckBell1;
	Spinner spinnerSoundType;

	private HourBellsPlayer hourBellsPlayer = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		btnStartAlarms = findViewById( R.id.btnStartAlarms );
		btnStopAlarms = findViewById( R.id.btnStopAlarms );
		cbShort = findViewById( R.id.cbShort );
		cbHalfHour = findViewById( R.id.cbHalfHour );
		cbUTC = findViewById( R.id.cbUTC );
		btnCheckBell0 = findViewById( R.id.btnCheckBell0 );
		btnCheckBell1 = findViewById( R.id.btnCheckBell1 );
		spinnerSoundType = findViewById( R.id.spinnerSoundType );

		boolean isActive = HourBellsManager.getActive( this );
		btnStartAlarms.setEnabled( ! isActive );
		btnStopAlarms.setEnabled( isActive );

		btnStartAlarms.setOnClickListener( this );
		btnStopAlarms.setOnClickListener( this );
		btnCheckBell0.setOnClickListener( this );
		btnCheckBell1.setOnClickListener( this );

		cbShort.setChecked( HourBellsManager.getShortMode( this ) );
		cbHalfHour.setChecked( HourBellsManager.getHalfHoursMode( this ) );
		cbUTC.setChecked( HourBellsManager.getUtcMode( this ) );

		int savedSoundType = HourBellsManager.getSoundType( this );

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>( this, android.R.layout.simple_spinner_item, HourBellsManager.getSoundTypes() );
		spinnerSoundType.setAdapter( dataAdapter );
		spinnerSoundType.setSelection( savedSoundType );
		spinnerSoundType.setOnItemSelectedListener( this );

		cbShort.setOnCheckedChangeListener( this );
		cbHalfHour.setOnCheckedChangeListener( this );
		cbUTC.setOnCheckedChangeListener( this );

		//hourBellsPlayer = new HourBellsPlayer( this );
		hourBellsPlayer = new HourBellsPlayer( this, savedSoundType );
	}

	@Override
	public void onClick(View view) {
		switch( view.getId() ){
			case R.id.btnStartAlarms: {
				btnStartAlarms.setEnabled( false );
				btnStopAlarms.setEnabled( true );
				HourBellsManager.createAlarm( this );
				Toasty.success( getApplicationContext(), "The alarm was created", Toast.LENGTH_SHORT, true ).show();
				break;
			}
			case R.id.btnStopAlarms: {
				btnStartAlarms.setEnabled( true );
				btnStopAlarms.setEnabled( false );
				HourBellsManager.cancelAlarm( this );
				break;
			}
			case R.id.btnCheckBell0: {
				hourBellsPlayer.play( new int[]{ 0 } );
				//hourBellsPlayer.release();
				break;
			}
			case R.id.btnCheckBell1: {
				hourBellsPlayer.play( new int[]{ 1 } );
				//hourBellsPlayer.release();
				break;
			}
		}
	}

	@Override
	public void onCheckedChanged( CompoundButton compoundButton, boolean isChecked ) {
		switch( compoundButton.getId() ){
			case R.id.cbShort: {
				HourBellsManager.setShortMode( that, isChecked );
				break;
			}
			case R.id.cbHalfHour: {
				HourBellsManager.setHalfHoursMode( that, isChecked );
				break;
			}
			case R.id.cbUTC: {
				HourBellsManager.setUtcMode( that, isChecked );
				break;
			}
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
		HourBellsManager.setSoundType( this, position );
		hourBellsPlayer = new HourBellsPlayer( this, position );
	}

	@Override
	public void onNothingSelected(AdapterView<?> adapterView) {

	}
}
