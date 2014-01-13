package co.edu.unal.ing.accmodels.controller;

public class VectorController {

	public static final float DISTANCE_FACTOR = 5.0f;
	
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

    //Multiplies matrixces returns A x B
    public static float[][] multiplyMatrixes(float[][] A, float[][] B) {

        int aRows = A.length;
        int aColumns = A[0].length;
        int bRows = B.length;
        int bColumns = B[0].length;

        if (aColumns != bRows) {
            return null;
        }

        float[][] C = new float[aRows][bColumns];
        for (int i = 0; i < aRows; i++) {
            for (int j = 0; j < bColumns; j++) {
                C[i][j] = 0f;
            }
        }

        for (int i = 0; i < aRows; i++) { // aRow
            for (int j = 0; j < bColumns; j++) { // bColumn
                for (int k = 0; k < aColumns; k++) { // aColumn
                    C[i][j] += A[i][k] * B[k][j];
                }
            }
        }

        return C;
    }
 
    public static float[][] transponse(float[][] input){
    	float output[][] = new float[input[0].length][input.length];
    	for(int i=0;i<output.length;i++)
    		for(int j=0;j<output[0].length;j++)
    			output[i][j] = input[j][i];
    	return output;
    }
    
    public static float[][] addMatrixes(float[][] A, float[][] B){
    	if(A.length!=B.length || A[0].length!=B[0].length)
    		return null;
    	
    	float sum[][] = new float[A.length][A[0].length];
    	
    	for(int i=0;i<sum.length;i++)
    		for(int j=0;j<sum[0].length;j++)
    			sum[i][j] = A[i][j] + B[i][j];
    	
    	return sum;
    }
    
    public static float[][] scalar(float k, float[][] A){
    	float ret[][] = new float[A.length][A[0].length];
    	for(int i=0;i<A.length;i++)
    		for(int j=0;j<A[0].length;j++)
    			ret[i][j] = k*A[i][j];
    	return ret;
    }
    
    //http://www.codeproject.com/Articles/405128/Matrix-operations-in-Java
    
    public static float[][] createSubMatrix(float input[][], int excluding_row, int excluding_col){
        
        float ret[][] = new float[input.length-1][input[0].length-1];
        int r = -1;
        
        for (int i=0;i<input.length;i++) {
            if (i==excluding_row)
                continue;
                r++;
                int c = -1;
            for (int j=0;j<input[0].length;j++) {
                if (j==excluding_col)
                    continue;
                ret[r][++c] = input[i][j];
            }
        }
        return ret;
    } 
    
    public static float determinant(float[][] input){
        if (input.length != input.length)
        	return 0;//Not square
        
        if (input.length == 1) {
        	return input[0][0];
        }
        
        if (input.length==2) {
            return input[0][0] * input[1][1] - input[0][1] * input[1][0];
        }
        
        float sum = 0f;
        for (int i=0; i<input[0].length; i++) {
            sum += (i%2==0?1:-1) * input[0][i] * determinant(createSubMatrix(input, 0, i));
        }
        return sum;
    }
    
    public static float[][] cofactor(float[][] input){
    	
        float mat[][] = new float[input.length][input[0].length];
        
        for (int i=0;i<input.length;i++) {
            for (int j=0; j<input[0].length;j++) {
                mat[i][j] = (i%2==0?1:-1) * (j%2==0?1:-1) * determinant(createSubMatrix(input, i, j));
            }
        }
        
        return mat;
    }
    
    public static float[][] inverse(float[][] input){
    	if(input.length==1){
    		float ret[][] = new float[1][1];
    		ret[0][0] = 1/input[0][0];
    		return ret;
    	}
        return transponse(scalar(1/determinant(input), cofactor(input)));
    }
    
    //nxn identity matrix
    public static float[][] identity(int n){
    	float ret[][] = new float[n][n];
    	for(int i=0;i<n;i++)
    		for(int j=0;j<n;j++)
    			if(i==j)
    				ret[i][j] = 1f;
    			else
    				ret[i][j] = 0f;
    	return ret;
    }
    
}
