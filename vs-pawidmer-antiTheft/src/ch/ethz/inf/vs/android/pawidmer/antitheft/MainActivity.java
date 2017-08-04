package ch.ethz.inf.vs.android.pawidmer.antitheft;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.util.Log;
import android.view.Menu;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MainActivity extends Activity {

	SharedPreferences preferences;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		
		preferences.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {
			@Override
			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
				loadPref();
			}
		});
		
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean("alarm_active", false);
		editor.commit();
		
		
		CheckBox alarmActive = (CheckBox) findViewById(R.id.checkBox1);
		alarmActive.setChecked(preferences.getBoolean("alarm_active", false));
		alarmActive.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				updatePref();
			}
		});
		
		SeekBar alarmSensitivity = (SeekBar) findViewById(R.id.sensitivity);
		alarmSensitivity.setProgress(preferences.getInt("alarm_sensitivity", 50));
		alarmSensitivity.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				updatePref();
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
		});
		
		SeekBar alarmTimeout = (SeekBar) findViewById(R.id.timeout);
		alarmTimeout.setProgress(preferences.getInt("alarm_timeout", 5));
		alarmTimeout.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				updatePref();
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
		});
		
		loadPref();
	}
	
	public void updatePref()
	{
		SeekBar alarmSensitivity = (SeekBar) findViewById(R.id.sensitivity);
		SeekBar alarmTimeout = (SeekBar) findViewById(R.id.timeout);
		CheckBox alarmActive = (CheckBox) findViewById(R.id.checkBox1);
		
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean("alarm_active", alarmActive.isChecked());
		editor.putInt("alarm_sensitivity", alarmSensitivity.getProgress());
		editor.putInt("alarm_timeout", alarmTimeout.getProgress());
		editor.commit();
		
		Log.d("debug", "preferences updated");
	}
	
	public void loadPref()
	{
		CheckBox alarmActive = (CheckBox) findViewById(R.id.checkBox1);
		alarmActive.setChecked(preferences.getBoolean("alarm_active", false));
		
		SeekBar alarmSensitivity = (SeekBar) findViewById(R.id.sensitivity);
		alarmSensitivity.setProgress(preferences.getInt("alarm_sensitivity", 50));
		
		SeekBar alarmTimeout = (SeekBar) findViewById(R.id.timeout);
		alarmTimeout.setProgress(preferences.getInt("alarm_timeout", 5));
		
		TextView sensitivityText = (TextView) findViewById(R.id.sensitivityText);
		sensitivityText.setText("Alarm sensitivity: "+alarmSensitivity.getProgress());
		
		TextView timeoutText = (TextView) findViewById(R.id.timeoutText);
		timeoutText.setText("Timeout in seconds: "+alarmTimeout.getProgress());
		
		if (alarmActive.isChecked())
		{
			Intent intent = new Intent(getApplicationContext(), AntiTheftServiceImpl.class);
			startService(intent);
		}
		else
		{
			Intent intent = new Intent(getApplicationContext(), AntiTheftServiceImpl.class);
			stopService(intent);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
