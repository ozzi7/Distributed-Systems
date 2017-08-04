package ch.ethz.inf.vs.android.pawidmer.antitheft;

import android.content.SharedPreferences;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;
import android.util.Log;

public class MovementDetector extends AbstractMovementDetector{

	public float movementCount = 0;
	public boolean triggered = false;
	public SensorManager sensorManager;
	public AntiTheftServiceImpl service;
	
	public MovementDetector(AntiTheftServiceImpl service)
	{
		this.service = service;
	}
	
	
	@Override
	public void onSensorChanged(SensorEvent event) {	
		float len = (float) Math.sqrt((event.values[0]*event.values[0])+(event.values[1]*event.values[1])+(event.values[2]*event.values[2]));
		SharedPreferences sp =  PreferenceManager.getDefaultSharedPreferences(service);
		float sensitivity = ((float)sp.getInt("alarm_sensitivity", 50))*0.05f;
//		int alarmTimeout = sp.getInt("alarm_timeout", 5);
		
		movementCount = movementCount+(len*sensitivity)-1;
		if (movementCount < 0)
		{
			movementCount = 0;
		}
		
		if (movementCount > 40 && triggered == false)
		{
			triggered = true;
			Log.d("debug", "sensor alarm triggerd");
			
			service.startAlarm();
		}
	}

}
