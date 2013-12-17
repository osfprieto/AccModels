package co.edu.unal.ing.accmodels.controller;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import co.edu.unal.ing.accmodels.data_processing.Kalman;
import co.edu.unal.ing.accmodels.gui.MyRenderer;

public class MainActivity extends Activity {
	
	private GLSurfaceView openGLView;
	private MyRenderer renderer;

	private SensorManager sensorManager;
	private float rawData[];
	private float filteredData[];
	private boolean useFilteredData;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
			     WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		useFilteredData = true;
		
		putOpenGLView();
		
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensorManager.registerListener(new MySensorEventListener(this), 
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	public void update(float readData[]){
		rawData = readData;
		filteredData = Kalman.filter(rawData);
		if(useFilteredData)
			updateGUI(filteredData);
		else
			updateGUI(rawData);
	}
	
	private void updateGUI(float dataToUse[]){
		renderer.setViewPoint(dataToUse);
	}
	
	private void putOpenGLView(){
		openGLView = new GLSurfaceView(this);
		renderer = new MyRenderer();
		openGLView.setRenderer(renderer);
		
		setContentView(openGLView);
	}
	
	public GLSurfaceView getOpenGLView(){
		return openGLView;
	}
	public MyRenderer getMyRenderer(){
		return renderer;
	}
}
