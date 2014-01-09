package co.edu.unal.ing.accmodels.data_processing;

import co.edu.unal.ing.accmodels.controller.GUIUpdateService;


public class Kalman {
	
	public static float STARTING_ANGLE[] = {0, 0, 90};
	
	private Filtro filtros[];
	private float deltaTiempo;
	
	public Kalman(){
		
		filtros = new Filtro[3];
		
		for(int i=0;i<filtros.length;i++){
			filtros[i] = new Filtro();
			filtros[i].setAngle(STARTING_ANGLE[i]);
		}
		
		deltaTiempo = (float) ((float)GUIUpdateService.SLEEP_TIME)/1000f;
		
	}
	
	public float[] filtrar(float[] input){
		
		//Process the input
		
		
		
		return input;
	}
	
	public class Filtro{
		/* Kalman filter variables */
	    private float qAngle; // Process noise variance for the accelerometer
	    private float qBias; // Process noise variance for the gyro bias
	    private float rMeasure; // Measurement noise variance - this is actually the variance of the measurement noise

	    private float angle; // The angle calculated by the Kalman filter - part of the 2x1 state vector
	    private float bias; // The gyro bias calculated by the Kalman filter - part of the 2x1 state vector
	    private float rate; // Unbiased rate calculated from the rate and the calculated bias - you have to call getAngle to update the rate

	    private float p[][]; // Error covariance matrix - This is a 2x2 matrix
	    private float k[]; // Kalman gain - This is a 2x1 vector
	    private float y; // Angle difference
	    private float s; // Estimate error
		
	    public Filtro() {
	        /* We will set the variables like so, these can also be tuned by the user */
	    	
	        qAngle = 0.001f;
	        qBias = 0.003f;
	        rMeasure = 0.03f;

	        angle = 0; // Reset the angle
	        bias = 0; // Reset bias
	        
	        //Since we assume that the bias is 0 and we know the starting angle
	        //(use setAngle), the error covariance matrix is set like so - see:
	        //http://en.wikipedia.org/wiki/Kalman_filter#Example_application.2C_technical
	        p[0][0] = 0; 
	        p[0][1] = 0;
	        p[1][0] = 0;
	        p[1][1] = 0;
	    }
	    
	    // The angle should be in degrees and the rate should be in degrees
	    //per second and the delta time in seconds
	    
	    public float getAngle(float newAngle, float newRate, float dt) {
	    	
	        // Discrete Kalman filter time update equations - Time Update ("Predict")
	        // Update xhat - Project the state ahead
	        /* Step 1 */
	    	
	        rate = newRate - bias;
	        angle += dt * rate;

	        // Update estimation error covariance - Project the error covariance ahead
	        /* Step 2 */
	        
	        p[0][0] += dt * (dt*p[1][1] - p[0][1] - p[1][0] + qAngle);
	        p[0][1] -= dt * p[1][1];
	        p[1][0] -= dt * p[1][1];
	        p[1][1] += qBias * dt;

	        // Discrete Kalman filter measurement update equations - Measurement Update ("Correct")
	        // Calculate Kalman gain - Compute the Kalman gain
	        /* Step 4 */
	        
	        s = p[0][0] + rMeasure;
	        
	        /* Step 5 */
	        
	        k[0] = p[0][0] / s;
	        k[1] = p[1][0] / s;

	        // Calculate angle and bias - Update estimate with measurement zk (newAngle)
	        /* Step 3 */
	        
	        y = newAngle - angle;
	        
	        /* Step 6 */
	        
	        angle += k[0] * y;
	        bias += k[1] * y;

	        // Calculate estimation error covariance - Update the error covariance
	        /* Step 7 */
	        p[0][0] -= k[0] * p[0][0];
	        p[0][1] -= k[0] * p[0][1];
	        p[1][0] -= k[1] * p[0][0];
	        p[1][1] -= k[1] * p[0][1];

	        return angle;
	    }
	    
	    // Used to set angle, this should be set as the starting angle
	    public void setAngle(float newAngle){
	    	angle = newAngle;
	    }
	    
	    public float getRate(){
	    	return rate;
	    } // Return the unbiased rate

	    /* These are used to tune the Kalman filter */
	    public void setQangle(float newQ_angle){
	    	qAngle = newQ_angle;
	    }
	    public void setQbias(float newQ_bias){
	    	qBias = newQ_bias;
	    }
	    public void setRmeasure(float newR_measure){
	    	rMeasure = newR_measure;
	    }

	    public double getQangle(){
	    	return qAngle;
	    }
	    public double getQbias(){
	    	return qBias;
	    }
	    public double getRmeasure(){
	    	return rMeasure;
	    }
		
	}
}
