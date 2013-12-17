package co.edu.unal.ing.accmodels.controller;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class MySensorEventListener implements SensorEventListener{

	private MainActivity caller;
	
	public MySensorEventListener(MainActivity caller){
		this.caller = caller;
	}
	
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
			float ax=event.values[0];
	        float ay=event.values[1];
	        float az=event.values[2];
	        float values[] = {ax, ay, az};
	        caller.update(values);
	   }
	}

}
