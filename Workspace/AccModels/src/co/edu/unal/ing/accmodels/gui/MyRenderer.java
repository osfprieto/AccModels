package co.edu.unal.ing.accmodels.gui;

import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.util.Log;

public class MyRenderer implements Renderer{
	
	///////////////////////////////////////////////////
	
	// Se tienen que modificar los métodos de esta clase
	// para que utilice el punto de viewPoint
	// en la posición de la cámara
	
	///////////////////////////////////////////////////
	
	
	private float viewPoint[] = {1.0f, 1.0f, 1.0f};
	 
	private Cube mCube = new Cube();
    private float mCubeRotation;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f); 
            
        gl.glClearDepthf(1.0f);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glDepthFunc(GL10.GL_LEQUAL);

        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,
                  GL10.GL_NICEST);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
    	gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);        
        gl.glLoadIdentity();
        
        gl.glTranslatef(0.0f, 0.0f, -10.0f);
        gl.glRotatef(mCubeRotation, 1.0f, 1.0f, 1.0f);
            
        mCube.draw(gl);
           
        gl.glLoadIdentity();                                    
            
        mCubeRotation -= 1.0f; 
        
        /*GLU.gluLookAt(gl, factor*viewPoint[0], factor*viewPoint[1], factor*viewPoint[2],
        		0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);*/
         
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU.gluPerspective(gl, 45.0f, (float)width / (float)height, 0.1f, 100.0f);
        gl.glViewport(0, 0, width, height);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        //GLU.gluLookAt(gl, 100, 100, 100, 0, 0, 0, 0, 0, 1);
    }
	
    public void setViewPoint(float viewPoint[]){
    	Log.v("OSFPRIETO","View point changed: "+Arrays.toString(viewPoint));
    	this.viewPoint = viewPoint;
    }
    
}
