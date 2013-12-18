package co.edu.unal.ing.accmodels.data_processing;

import jkalman.JKalman;


public class Kalman {

	private JKalman jkalman;
	
	public Kalman() throws Exception{
		jkalman = new JKalman(3, 2);
	}
	
	public float[] filter(float[] input){
		
		//Process the input
		
		
		
		return input;
	}
	
}
