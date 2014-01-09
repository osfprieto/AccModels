package co.edu.unal.ing.accmodels.controller;

import android.app.IntentService;
import android.content.Intent;

public class GUIUpdateService extends IntentService{

	//Millis
	public static final long SLEEP_TIME = 2;
	
	private static MainActivity caller;
	
	public GUIUpdateService(String name) {
		super(name);
	}
	
	public GUIUpdateService(){
		super("GUIUpdateService");
	}

	public static void setMainActivity(MainActivity caller){
		GUIUpdateService.caller = caller;
	}
	
	protected void onHandleIntent(Intent intent) {
		while(true){
			//Log.v("OSFPRIETO", "Service call");
			caller.updateGUI();
			try{
				Thread.sleep(SLEEP_TIME);
			}catch(Exception e){
				
			}
		}
	}
	
}
