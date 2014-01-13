package co.edu.unal.ing.accmodels.controller;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import co.edu.unal.ing.accmodels.R;
import co.edu.unal.ing.accmodels.data_processing.Kalman;
import co.edu.unal.ing.accmodels.gui.MyRenderer;
import co.edu.unal.ing.accmodels.gui.PlotUpdater;

public class MainActivity extends Activity {
	
	private GLSurfaceView openGLView;
	private MyRenderer renderer;

	private SensorManager sensorManager;
	private float rawData[];
	private float filteredData[];
	private boolean useFilteredData;
	private Kalman kalman;
	
	private Intent updatingService;
	private PlotUpdater plotUpdater;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
			     WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		useFilteredData = true;
		
		try {
			kalman = new Kalman();
		} catch (Exception e) {
			useFilteredData = false;
		}
		
		putPlotView();
		
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensorManager.registerListener(new MySensorEventListener(this), 
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	public void update(float readData[]){
		rawData = readData;
		VectorController.normalizeVector(rawData);
		VectorController.applyFactor(rawData);
		
		if(useFilteredData)
			filteredData = kalman.filtrar(rawData);
		else
			filteredData = rawData;
		
		VectorController.normalizeVector(filteredData);
		VectorController.applyFactor(filteredData);
		
		plotUpdater.updatePlots(rawData, filteredData);
		renderer.setViewPoint(filteredData);
		//updateGUI();
	}
	
	public void updateGUI(){
		openGLView.requestRender();
	}
	
	private void putPlotView(){
		renderer = new MyRenderer();
		
		openGLView = new GLSurfaceView(this);
		openGLView.setEGLContextClientVersion(2);
		openGLView.setEGLConfigChooser(8 , 8, 8, 8, 16, 0);
		openGLView.setRenderer(renderer);
		openGLView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		openGLView.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				putPlotView();
			}
		});
		
		setContentView(R.layout.plotlayout);
		
		Button button = (Button) findViewById(R.id.buttonViewCube);
		
		button.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				putOpenGLView();
			}
		});
		
		plotUpdater = new PlotUpdater(this);
	}
	
	private void putOpenGLView(){
		setContentView(openGLView);
		
		CharSequence text = getString(R.string.tapToClose);
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(this, text, duration);
		toast.show();
		
		startUpdateService();
	}
	
	private void startUpdateService(){
		if(updatingService!=null)
			stopService(updatingService);
		
		GUIUpdateService.setMainActivity(this);
		updatingService = new Intent(this, GUIUpdateService.class);
		startService(updatingService);
	}
	
	public GLSurfaceView getOpenGLView(){
		return openGLView;
	}
	public MyRenderer getMyRenderer(){
		return renderer;
	}
}
