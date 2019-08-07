package com.spl.hourbells;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.spl.hourbells.classes.HourBellsManager;
import com.spl.hourbells.classes.HourBellsReceiver;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

	public final MainActivity that = this;

	Button btnStartAlarms, btnStopAlarms;
	CheckBox cbShort, cbHalfHour, cbUTC;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		btnStartAlarms = findViewById( R.id.btnStartAlarms );
		btnStopAlarms = findViewById( R.id.btnStopAlarms );
		cbShort = findViewById( R.id.cbShort );
		cbHalfHour = findViewById( R.id.cbHalfHour );
		cbUTC = findViewById( R.id.cbUTC );

		boolean isActive = HourBellsManager.getActive( this );
		btnStartAlarms.setEnabled( ! isActive );
		btnStopAlarms.setEnabled( isActive );

		btnStartAlarms.setOnClickListener( this );
		btnStopAlarms.setOnClickListener( this );

		cbShort.setChecked( HourBellsManager.getShortMode( this ) );
		cbHalfHour.setChecked( HourBellsManager.getHalfHoursMode( this ) );
		cbUTC.setChecked( HourBellsManager.getUtcMode( this ) );

		cbShort.setOnCheckedChangeListener( this );
		cbHalfHour.setOnCheckedChangeListener( this );
		cbUTC.setOnCheckedChangeListener( this );
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
}
