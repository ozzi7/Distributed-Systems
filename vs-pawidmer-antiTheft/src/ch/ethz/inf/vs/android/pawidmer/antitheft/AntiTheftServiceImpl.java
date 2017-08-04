package ch.ethz.inf.vs.android.pawidmer.antitheft;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;

public class AntiTheftServiceImpl extends Service  implements AntiTheftService{
	
	public AbstractMovementDetector movementDetector;
	public SensorManager sensorManager = null;
	public MediaPlayer mediaplayer = null;;
	
	public float triggerCount;
	
	public boolean triggered;
	
	public AntiTheftServiceImpl() {

	}

	@Override
	public void startAlarm() {
		
		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pintent = PendingIntent.getActivity(this, 0, intent, 0);
		
		Notification.Builder n  = new Notification.Builder(this)
	        .setContentTitle("Alarm triggered")
	        .setSmallIcon(R.drawable.ic_launcher)
	        .setContentIntent(pintent)
	        .setAutoCancel(true);

		
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		notificationManager.notify(0, n.build()); 
		
		Runnable alarmThread = new Runnable() {
			@Override
			public void run() {
				SharedPreferences sp =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				int alarmTimeout = sp.getInt("alarm_timeout", 5);
				try {
					Thread.sleep(alarmTimeout*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				// play sound
				MediaPlayer mediaplayer = MediaPlayer.create(getBaseContext(), R.raw.alarm);
				
				sp =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				boolean active = sp.getBoolean("alarm_active", false);
				while (active)
				{
					Vibrator vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
					
					mediaplayer.start();
					try {
						vib.vibrate(300);
						Thread.sleep(300);
						vib.vibrate(300);
						Thread.sleep(300);
						vib.vibrate(300);
						Thread.sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					sp =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
					active = sp.getBoolean("alarm_active", false);
				}
			}
		};
		
		Thread thread = new Thread(alarmThread);
		thread.start();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		triggerCount = 0.0f;
		triggered = false;
		Log.d("debug", "Service created");
		
		if (sensorManager == null) // Only once...
		{
			movementDetector = new MovementDetector(this);
			sensorManager = (SensorManager)getSystemService(Service.SENSOR_SERVICE);
			sensorManager.registerListener(movementDetector, sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_NORMAL);
		}
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {

		if (movementDetector != null)
		{
			sensorManager.unregisterListener(movementDetector);
			movementDetector = null;
		}
		
		if (mediaplayer != null)
		{
			mediaplayer.stop();
		}
		
		Log.d("debug", "Service destroyed");
		super.onDestroy();
	}
}
