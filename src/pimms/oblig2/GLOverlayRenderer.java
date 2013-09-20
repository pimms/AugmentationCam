package pimms.oblig2;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.util.Log;

public class GLOverlayRenderer implements Renderer {
	private FloatBuffer mVertexBuffer;
	float[] mRotEuler = new float[3];
	
	public GLOverlayRenderer() {
		ByteBuffer byteBuffer =	ByteBuffer.allocateDirect(cubeVertices.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		
    	mVertexBuffer = byteBuffer.asFloatBuffer();
    	mVertexBuffer.put(cubeVertices);
    	mVertexBuffer.position(0);
	}
	
	public void onDrawFrame( GL10 gl ) {
        gl.glClear( GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT );
        
        gl.glPushMatrix();
        gl.glLoadIdentity();
        
        gl.glTranslatef(0f, 0f, -10f);
        gl.glRotatef(mRotEuler[0], 1.0f, 0.0f, 0.0f);
        gl.glRotatef(mRotEuler[1], 0.0f, 1.0f, 0.0f);
        gl.glRotatef(mRotEuler[2], 0.0f, 0.0f, 1.0f);
        
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
        gl.glDrawArrays(GL10.GL_TRIANGLES, 0, cubeVertices.length/3);
        mVertexBuffer.position(0);
        
        gl.glPopMatrix();
    }
 
    public void onSurfaceChanged( GL10 gl, int width, int height ) {
        gl.glViewport( 0, 0, width, height );
        
        float aspect = (float)width / (float)height;
        
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        
        GLU.gluPerspective(gl, 45, aspect, 0.1f, 1000f);
        
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
    }
 
    public void onSurfaceCreated( GL10 gl, EGLConfig config ) {
    	gl.glClearColor(0f, 0f, 0f, 0f);
    	gl.glClearDepthf(1f);
    	gl.glEnable(GL10.GL_DEPTH_TEST);
    	gl.glDepthFunc(GL10.GL_LEQUAL);
    	gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
    	gl.glShadeModel(GL10.GL_SMOOTH);
    	gl.glDisable(GL10.GL_DITHER);
    	
    	gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
    	gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
    	gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
    }  

    
    /* private float[] mRotMatrix = new float[16];
    public void setGyroMatrix(float[] rotMatrix) {
    	// Transpose the matrix
    	for (int r=0; r<4; r++) { 
    		for (int c=0; c<4; c++) {
    			mRotMatrix[c*4+r] = rotMatrix[r*4+c];
    		}
    	}
    	
    	
    	double r32 = mRotMatrix[9];
    	double r33 = mRotMatrix[10];
    	double r31 = mRotMatrix[8];
    	double r21 = mRotMatrix[4];
    	double r11 = mRotMatrix[0];
    	
    	double rotX = Math.toDegrees(Math.atan2(r32, r33));
    	double rotY = Math.toDegrees(Math.atan2(-r31, Math.sqrt(r32*r32+r33*r33)));
    	double rotZ = Math.toDegrees(Math.atan2(r21, r11));
    	
    	Log.i("ROT", rotX + ", " + rotY + ", " + rotZ);
    } */
    
    public void setRotationEuler(float x, float y, float z) {
    	mRotEuler[0] = x;
    	mRotEuler[1] = y;
    	mRotEuler[2] = z;
    }
	
	// The prettiest cube in ALL of the lands!
	private static float[] cubeVertices = {
		-1.0f,-1.0f,-1.0f, // triangle 1 : begin
	    -1.0f,-1.0f, 1.0f,
	    -1.0f, 1.0f, 1.0f, // triangle 1 : end
	    1.0f, 1.0f,-1.0f, // triangle 2 : begin
	    -1.0f,-1.0f,-1.0f,
	    -1.0f, 1.0f,-1.0f, // triangle 2 : end
	    1.0f,-1.0f, 1.0f,
	    -1.0f,-1.0f,-1.0f,
	    1.0f,-1.0f,-1.0f,
	    1.0f, 1.0f,-1.0f,
	    1.0f,-1.0f,-1.0f,
	    -1.0f,-1.0f,-1.0f,
	    -1.0f,-1.0f,-1.0f,
	    -1.0f, 1.0f, 1.0f,
	    -1.0f, 1.0f,-1.0f,
	    1.0f,-1.0f, 1.0f,
	    -1.0f,-1.0f, 1.0f,
	    -1.0f,-1.0f,-1.0f,
	    -1.0f, 1.0f, 1.0f,
	    -1.0f,-1.0f, 1.0f,
	    1.0f,-1.0f, 1.0f,
	    1.0f, 1.0f, 1.0f,
	    1.0f,-1.0f,-1.0f,
	    1.0f, 1.0f,-1.0f,
	    1.0f,-1.0f,-1.0f,
	    1.0f, 1.0f, 1.0f,
	    1.0f,-1.0f, 1.0f,
	    1.0f, 1.0f, 1.0f,
	    1.0f, 1.0f,-1.0f,
	    -1.0f, 1.0f,-1.0f,
	    1.0f, 1.0f, 1.0f,
	    -1.0f, 1.0f,-1.0f,
	    -1.0f, 1.0f, 1.0f,
	    1.0f, 1.0f, 1.0f,
	    -1.0f, 1.0f, 1.0f,
	    1.0f,-1.0f, 1.0f	
};
}
