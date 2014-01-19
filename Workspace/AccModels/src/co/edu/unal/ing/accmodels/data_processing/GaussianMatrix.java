package co.edu.unal.ing.accmodels.data_processing;

import java.util.Random;

public class GaussianMatrix {

	private Random random[][];
	private float Q[][];
	
	public GaussianMatrix(float[][] Q){
		this.Q = Q;
		Random seeds = new Random(System.currentTimeMillis());
		random = new Random[Q.length][Q[0].length];
		for(int i=0;i<Q.length;i++){
			for(int j=0;j<Q[i].length;j++){
				random[i][j] = new Random(seeds.nextLong());
			}
		}
	}
	
	public float[][] nextRandomMatrix(){
		float ret[][] = new float[random.length][random[0].length];
		
		for(int i=0;i<ret.length;i++){
			for(int j=0;j<ret[i].length;j++){
				ret[i][j] = (float) (random[i][j].nextGaussian()*Q[i][j]);
			}
		}
		
		return ret;
	}
	
}
