package pimms.oblig2;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.util.Log;

import pimms.oblig2.gfx.*;

public class GLOverlayRenderer implements Renderer {
	public static final double METERS_PER_LON = 55799.979000000014;
	public static final double METERS_PER_LAT = 111412.24020000001;
	
	private float[] mRotEuler = new float[3];
	private float[] mRotMatrix = new float[16];
	private float[] mDevicePosition = new float[3];

	private Scene3D mScene;
	
	public GLOverlayRenderer() {
		
	}
	
	public void onDrawFrame(GL10 gl) {
        gl.glClear( GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT );
        
        gl.glPushMatrix();
        gl.glLoadIdentity();
        
        gl.glRotatef(-mRotEuler[1], 0f, 0f, 1f);
        gl.glRotatef( mRotEuler[2]+90, 1f, 0f, 0f);
        gl.glRotatef( mRotEuler[0], 0f, 1f, 0f);
        
        gl.glTranslatef(-mDevicePosition[0], -mDevicePosition[1], -mDevicePosition[2]);
        
        mScene.drawScene(gl);
        
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
    	
    	// Vertex Arrays are required for all objects,
    	// so it's always enabled. Other client states
    	// must be handled by Object3D objects manually.
    	gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
    	
    	mScene = new Scene3D(gl);
    }  

    public void setGyroMatrix(float[] rotMatrix) {
    	for (int r=0; r<3; r++) {
    		for (int c=0; c<3; c++) {
    			mRotMatrix[r*4+c] = rotMatrix[r*3+c];
    		}
    	}
    	
    	mRotMatrix[3] = mRotMatrix[7] = 0f;
    	mRotMatrix[15] = 1f;
    	
    	for (int i=11; i<=14; i++) {
    		mRotMatrix[i] = 0f;
    	}
    } 
    
    public void setRotationEuler(float x, float y, float z) {
    	mRotEuler[0] = x;
    	mRotEuler[1] = y;
    	mRotEuler[2] = z;
    }
    
    public void setDeviceLocation(float[] position) {
    	assert(position.length == 3);
    	mDevicePosition = position.clone();
    }
}
