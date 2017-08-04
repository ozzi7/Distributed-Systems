package ch.ethz.inf.vs.android.pawidmer.sensors;

import java.util.List;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.os.Build;

public class SensorActivity extends Activity {

	SensorManager sensorManager;
	Sensor sensor = null;
	String[] values;
	SensorActivity thisActivity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sensor);
		setupActionBar();
		
		thisActivity = this;

		sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
		
		
		
		int sensorId = getIntent().getIntExtra("sensorId", -1);
		if (sensorId != -1)
		{
			sensor = sensors.get(sensorId);
			TextView tv = (TextView) findViewById(R.id.text);
			tv.setText(sensor.getName());
			
			SensorEventListener sensorEventListener = new SensorEventListener() {
				
				@Override
				public void onSensorChanged(SensorEvent arg0) {
					ListView lv = (ListView) findViewById(R.id.listView2);
					
					
					values = new String[arg0.values.length];
					for (int i = 0; i < arg0.values.length; i++)
					{
						values[i] = Float.toString(arg0.values[i]);
					}
					
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(thisActivity, android.R.layout.simple_list_item_1, values);
					lv.setAdapter(adapter);
					
					
				}
				
				@Override
				public void onAccuracyChanged(Sensor arg0, int arg1) {
					// TODO Auto-generated method stub
					
				}
			};
			
			sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
			
//			Log.d("debug", parent.sensors.get(sensorId).getName());
		}
		else
		{
			Log.e("error", "invalid sensorId");
		}
		
		
		
		
		
		
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sensor, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
