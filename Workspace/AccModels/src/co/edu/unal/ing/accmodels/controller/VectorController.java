package co.edu.unal.ing.accmodels.controller;

public class VectorController {

	public static final float DISTANCE_FACTOR = -5.0f;
	
	public static void normalizeVector(float input[]){
    	float length = norm(input);
    	input[0] /= length;
    	input[1] /= length;
    	input[2] /= length;
    }
    
    public static float norm(float input[]){
    	float ret = 0;
    	
    	ret += input[0]*input[0];
    	ret += input[1]*input[1];
    	ret += input[2]*input[2];
    	
    	return (float) Math.sqrt(ret);
    }
    
    public static void applyFactor(float input[]){
    	input[0] *= DISTANCE_FACTOR;
    	input[1] *= DISTANCE_FACTOR;
    	input[2] *= DISTANCE_FACTOR;
    }
	
	
}
