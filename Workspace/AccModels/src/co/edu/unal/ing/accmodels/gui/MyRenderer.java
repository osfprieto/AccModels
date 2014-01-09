/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package co.edu.unal.ing.accmodels.gui;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

public class MyRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "MyGLRenderer";
    private static final float DISTANCE_FACTOR = -5.0f;
    
    
    private Cube   cube;
    private CubeLines cubeLines;
    private float viewPoint[] = {1, 2, 3};
    
    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    //private final float[] mRotationMatrix = new float[16];

    //private float mAngle;

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        // Set the background frame color
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        
        cube   = new Cube();
        cubeLines = new CubeLines();
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        //Log.v("OSFPRIETO", Arrays.toString(viewPoint));
        
        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0,
        		viewPoint[0], viewPoint[1], viewPoint[2],//Eye center
        		0f, 0f, 0f,//Where to look
        		0f, 1.0f, 0.0f);//Up vector

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        // Draw square
        cube.draw(mMVPMatrix);
        cubeLines.draw(mMVPMatrix);

       //Log.v("OSFPRIETO", "Redraw");
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        // Adjust the viewport based on geometry changes,
        // such as screen rotation
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);

    }

    public void setViewPoint(float viewPoint[]){
    	
    	this.viewPoint[0] = viewPoint[0];
    	this.viewPoint[1] = viewPoint[1];
    	this.viewPoint[2] = viewPoint[2];
    	normalizeVector();
    	applyFactor();
    	//Log.v("OSFPRIETO", Arrays.toString(this.viewPoint));
    }
    
    private void normalizeVector(){
    	float length = norma();
    	viewPoint[0] /= length;
    	viewPoint[1] /= length;
    	viewPoint[2] /= length;
    }
    
    private float norma(){
    	float ret = 0;
    	
    	ret += viewPoint[0]*viewPoint[0];
    	ret += viewPoint[1]*viewPoint[1];
    	ret += viewPoint[2]*viewPoint[2];
    	
    	return (float) Math.sqrt(ret);
    }
    
    private void applyFactor(){
    	viewPoint[0] *= DISTANCE_FACTOR;
    	viewPoint[1] *= -DISTANCE_FACTOR;
    	viewPoint[2] *= DISTANCE_FACTOR;
    }
    
    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }
    
    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }
    
}