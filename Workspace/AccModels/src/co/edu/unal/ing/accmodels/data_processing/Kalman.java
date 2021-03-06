package co.edu.unal.ing.accmodels.data_processing;

import co.edu.unal.ing.accmodels.controller.VectorController;

public class Kalman {
	
	private static final float q = 0.01f;
	private static final float r = 0.1f;
	
	public static final int TYPE_1D = 0;
	public static final int TYPE_3D = 1;
	
	private Filter filters[][];
	
	public Kalman(){
		filters = new Filter[3][2];
		for(int i=0;i<filters.length;i++){
			filters[i][TYPE_1D] = initialize1DFilter();
			filters[i][TYPE_3D] = initialize3DFilter();
		}
	}
	
	private static Filter initialize3DFilter(){
		
		float dt = 1f;
		
		float A[][] = {	{1, dt, dt*dt/2},
						{0,  1,    dt},
						{0,  0,     1}};

		float B[][] = VectorController.identity(3);
		
		float H[][] = VectorController.identity(3);
		
		float currentStateEstimate[][] = {	{0},
											{0},
											{5}};
		
		float currentProbEstimate[][] = VectorController.identity(3);
		
		float Q[][] = {	{(float) (Math.pow(dt,5)/20f),(float) (Math.pow(dt,4)/8f),(float) (Math.pow(dt,3)/6f)},
						{(float) (Math.pow(dt,4)/ 8f),(float) (Math.pow(dt,3)/3f),(float) (Math.pow(dt,2)/2f)},
						{(float) (Math.pow(dt,3)/ 6f),(float) (Math.pow(dt,2)/2f),						  dt}};
		Q = VectorController.scalar(q, Q);
		
		float R[][] = VectorController.scalar(r, VectorController.identity(3));

		return new Filter(A, B, H, currentStateEstimate, currentProbEstimate, Q, R);
	}
	
	private static Filter initialize1DFilter(){
		
    	float A[][] = new float[1][1];
    	A[0][0] = 1f;
    	
    	float B[][] = new float[1][1];
    	B[0][0] = 0f;
    	
    	float H[][] = new float[1][1];
    	H[0][0] = 1f;
    	
    	float currentStateEstimate[][] = new float[1][1];
    	currentStateEstimate[0][0] = 0f;
    	
    	float currentProbEstimate[][] = new float[1][1];
    	currentProbEstimate[0][0] = 1f;
    	
    	float Q[][] = new float[1][1];
    	Q[0][0] = q;
    	
    	float R[][] = new float[1][1];
    	R[0][0] = r;
    	
    	return new Filter(A, B, H, currentStateEstimate, currentProbEstimate, Q, R);
	}
    
    public float[] filter(float[] input, int filterToUse){
		
		//Process the input
		
    	float ret[] = new float[3];
    	
    	if(filterToUse == TYPE_3D){//Use 3D filter
			
			for(int i=0;i<3;i++){
    			ret[i] = filters[i][TYPE_3D].getCurrentState()[2][0];
    			
    			float controlVector[][] = {	{0},
											{0},
											{0}};

    			float measurementVector[][] = {	{0},
												{0},
												{input[i]}};
    			
    			filters[i][TYPE_3D].step(controlVector, measurementVector);
    		}
    	
    	}else if(filterToUse == TYPE_1D){//Use 1D filters
    		
    		for(int i=0;i<3;i++){
    			ret[i] = filters[i][TYPE_1D].getCurrentState()[0][0];
    			
    			float controlVector[][] = new float[1][1];
    			controlVector[0][0] = 0;
    			
    			float measurementVector[][] = new float[1][1];
    			measurementVector[0][0] = input[i];
    			
    			filters[i][TYPE_1D].step(controlVector, measurementVector);
    		}
    		
    	}
		
		return ret;
	}
    
    public static class Filter{
		
		private float A[][];						//State transition matrix.
	    private float B[][];						//Control matrix.
	    private float H[][];						//Observation matrix.
	    private float currentStateEstimate[][];		//Initial state estimate.
	    private float currentProbEstimate[][]; 		//Initial covariance estimate.
	    private float Q[][];						//Estimated error in process.
	    private float R[][];						//Estimated error in measurements.
		
	    
	    
	    public Filter(float A[][], float B[][], float H[][],
	    		float currentStateEstimate[][], float currentProbEstimate[][],
	    		float Q[][], float R[][]){
	    	this.A = A;
	    	this.B = B;
	    	this.H = H;
	    	this.currentStateEstimate = currentStateEstimate;
	    	this.currentProbEstimate = currentProbEstimate;
	    	this.Q = Q;
	    	this.R = R;
	    }
	    
	    public float[][] getCurrentState(){
	        return currentStateEstimate;
	    }
	    
	    public void  step(float controlVector[][], float measurementVector[][]){
	        //---------------------------Prediction step-----------------------------
	    	//predicted_state_estimate = A * current_state_estimate + B * control_vector
	    	float predictedStateEstimate[][] = VectorController.addMatrixes(
	        		VectorController.multiplyMatrixes(A, currentStateEstimate),
	        		VectorController.multiplyMatrixes(B, controlVector));
	        
	        //predicted_prob_estimate = (A * current_prob_estimate) * transpose(A) + Q
	        float predictedProbEstimate[][] = VectorController.addMatrixes(
	        		VectorController.multiplyMatrixes(
	        				VectorController.multiplyMatrixes(A, currentProbEstimate),
	        				VectorController.transponse(A)), Q);
	        		
	        //--------------------------Observation step-----------------------------
	        //innovation = measurement_vector - H*predicted_state_estimate
	        float innovation[][] = VectorController.addMatrixes(measurementVector, VectorController.scalar(-1, 
	        		VectorController.multiplyMatrixes(H, predictedStateEstimate)));
	        
	        //innovation_covariance = (H*predicted_prob_estimate)*transpose(H) + R
	        float innovationCovariance[][] = VectorController.addMatrixes(
	        		VectorController.multiplyMatrixes(
	        				VectorController.multiplyMatrixes(H, predictedProbEstimate),
	        				VectorController.transponse(H)), R);
	        
	        //-----------------------------Update step-------------------------------
	        //kalman_gain = predicted_prob_estimate * transpose(H) * inv(innovation_covariance)
	        float kalmanGain[][] = VectorController.multiplyMatrixes(
	        		VectorController.multiplyMatrixes(predictedProbEstimate,
	        				VectorController.transponse(H)),
	        				VectorController.inverse(innovationCovariance));
	        
	        //current_state_estimate = predicted_state_estimate + kalman_gain * innovation
	        currentStateEstimate = VectorController.addMatrixes(predictedStateEstimate,
	        		VectorController.multiplyMatrixes(kalmanGain, innovation));
	        
	        // We need the size of the matrix so we can make an identity matrix.
	        int size = currentProbEstimate.length;
	        
	        //current_prob_estimate = (identity(size)-kalman_gain*self)*predicted_prob_estimate
	        currentProbEstimate = VectorController.multiplyMatrixes(
	        		VectorController.addMatrixes(
	        				VectorController.identity(size), 
	        				VectorController.scalar(-1,	VectorController.multiplyMatrixes(kalmanGain, H))),
	        		predictedProbEstimate);
	    }
	}
    
}