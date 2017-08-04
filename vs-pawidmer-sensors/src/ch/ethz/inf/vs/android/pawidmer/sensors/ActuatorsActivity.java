package ch.ethz.inf.vs.android.pawidmer.sensors;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class ActuatorsActivity extends Activity {

	int vibrMillisecs = 1000;
	
	Button soundButton;
	
	Button vibrButton;
	TextView vibrDurText;
	SeekBar vibrTimeSeekBar;
	
	Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_actuators);
		
		context = getApplicationContext();
		
		vibrButton = (Button)findViewById(R.id.vibrateButton); 
		vibrButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Vibrator vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
				vib.vibrate(vibrMillisecs);
			}
		});
		
		vibrDurText = (TextView)findViewById(R.id.vibrationTimeText);
		vibrDurText.setText(vibrMillisecs+"ms");
		
		vibrTimeSeekBar = (SeekBar)findViewById(R.id.vibrationTimeSeekBar);
		vibrTimeSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				vibrMillisecs = progress;
				vibrDurText.setText(vibrMillisecs+"ms");
			}
		});
		
		soundButton = (Button)findViewById(R.id.playSoundButton);
		soundButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MediaPlayer mp = MediaPlayer.create(context, R.raw.pig);
				mp.start();
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.actuators, menu);
		return true;
	}

}
