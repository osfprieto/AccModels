package co.edu.unal.ing.accmodels.gui;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;

public class Line {

	private FloatBuffer VertexBuffer;
	
	private final String VertexShaderCode =
	        // This matrix member variable provides a hook to manipulate
	        // the coordinates of the objects that use this vertex shader
	        "uniform mat4 uMVPMatrix;" +
	
	        "attribute vec4 vPosition;" +
	        "void main() {" +
	        // the matrix must be included as a modifier of gl_Position
	        "  gl_Position = uMVPMatrix * vPosition;" +
	        "}";
	
	private final String FragmentShaderCode =
	        "precision mediump float;" +
	        "uniform vec4 vColor;" +
	        "void main() {" +
	        "  gl_FragColor = vColor;" +
	        "}";
	
	protected int GlProgram;
	protected int PositionHandle;
	protected int ColorHandle;
	protected int MVPMatrixHandle;
	
	// number of coordinates per vertex in this array
	static final int COORDS_PER_VERTEX = 3;
	static float lineCoords[] = {
	    0.0f, 0.0f, 0.0f,
	    1.0f, 0.0f, 0.0f
	};
	
	private final int VertexCount = lineCoords.length / COORDS_PER_VERTEX;
	private final int VertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex
	
	// Set color with red, green, blue and alpha (opacity) values
	float color[] = { 0.0f, 0.0f, 0.0f, 1.0f };
	
	public Line(){
	    // initialize vertex byte buffer for shape coordinates
	    ByteBuffer bb = ByteBuffer.allocateDirect(
	            // (number of coordinate values * 4 bytes per float)
	            lineCoords.length * 4);
	    // use the device hardware's native byte order
	    bb.order(ByteOrder.nativeOrder());
	
	    // create a floating point buffer from the ByteBuffer
	    VertexBuffer = bb.asFloatBuffer();
	    // add the coordinates to the FloatBuffer
	    VertexBuffer.put(lineCoords);
	    // set the buffer to read the first coordinate
	    VertexBuffer.position(0);
	
	
	    int vertexShader = MyRenderer.loadShader(GLES20.GL_VERTEX_SHADER, VertexShaderCode);
	    int fragmentShader = MyRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, FragmentShaderCode);
	
	    GlProgram = GLES20.glCreateProgram();             // create empty OpenGL ES Program
	    GLES20.glAttachShader(GlProgram, vertexShader);   // add the vertex shader to program
	    GLES20.glAttachShader(GlProgram, fragmentShader); // add the fragment shader to program
	    GLES20.glLinkProgram(GlProgram);                  // creates OpenGL ES program executables
	
	}
	
	public void setVerts(float v[])
	{
	    lineCoords[0] = v[0];
	    lineCoords[1] = v[1];
	    lineCoords[2] = v[2];
	    lineCoords[3] = v[3];
	    lineCoords[4] = v[4];
	    lineCoords[5] = v[5];
	
	    VertexBuffer.put(lineCoords);
	    // set the buffer to read the first coordinate
	    VertexBuffer.position(0);
	
	}
	
	public void SetColor(float red, float green, float blue, float alpha)
	{
	    color[0] = red;
	    color[1] = green;
	    color[2] = blue;
	    color[3] = alpha;
	}
	
	public void draw(float[] mvpMatrix) {
	    // Add program to OpenGL ES environment
	    GLES20.glUseProgram(GlProgram);
	
	    // get handle to vertex shader's vPosition member
	    PositionHandle = GLES20.glGetAttribLocation(GlProgram, "vPosition");
	
	    // Enable a handle to the triangle vertices
	    GLES20.glEnableVertexAttribArray(PositionHandle);
	
	    // Prepare the triangle coordinate data
	    GLES20.glVertexAttribPointer(PositionHandle, COORDS_PER_VERTEX,
	                                 GLES20.GL_FLOAT, false,
	                                 VertexStride, VertexBuffer);
	
	    // get handle to fragment shader's vColor member
	    ColorHandle = GLES20.glGetUniformLocation(GlProgram, "vColor");
	
	    // Set color for drawing the triangle
	    GLES20.glUniform4fv(ColorHandle, 1, color, 0);
	
	    // get handle to shape's transformation matrix
	    MVPMatrixHandle = GLES20.glGetUniformLocation(GlProgram, "uMVPMatrix");
	    MyRenderer.checkGlError("glGetUniformLocation");
	
	    // Apply the projection and view transformation
	    GLES20.glUniformMatrix4fv(MVPMatrixHandle, 1, false, mvpMatrix, 0);
	    MyRenderer.checkGlError("glUniformMatrix4fv");
	
	
	    // Draw the triangle
	    GLES20.glDrawArrays(GLES20.GL_LINES, 0, VertexCount);
	
	    // Disable vertex array
	    GLES20.glDisableVertexAttribArray(PositionHandle);
	}
}