package pimms.oblig2;

import java.util.Timer;
import java.util.TimerTask;

import pimms.oblig2.graphics.GLSceneRenderer;

import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;


public class AugmentationActivity extends Activity 
			implements 	OnClickListener,
						OrientationSensorListener,
						JoystickMovedListener {
	
	private GLSurfaceView mGlView;
	private GLSceneRenderer mRenderer;
	private OrientationSensorReader mSensors;
	
	private float[] mFusedOrientation;
	private float[] mDevicePosition;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE );
        
        requestWindowFeature( Window.FEATURE_NO_TITLE );
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
                                WindowManager.LayoutParams.FLAG_FULLSCREEN );
        
        setContentView(R.layout.activity_augmentation);
        
        // Create an openGL view and set it as the main layout
        mGlView = (GLSurfaceView)findViewById(R.id.glView);
        mGlView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        mGlView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        mRenderer = new GLSceneRenderer(this);
        mGlView.setRenderer(mRenderer);
        
        mDevicePosition = new float[] { -10f, 1.5f, 0f };
        mRenderer.setDeviceLocation(mDevicePosition);
     
        initButtonListeners();
        
        mSensors = new OrientationSensorReader(this, this);
        mSensors.beginReading();
    }
    
    @Override
    public void onStop() {
    	super.onStop();
    	mGlView.onPause();
    	mSensors.stopReading();
    }
	
    @Override
    protected void onPause() {
        super.onPause();
        mGlView.onPause();
        mSensors.stopReading();
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	mGlView.onResume();
    	
    	mSensors.beginReading();
    }
    
    @Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_down:
			translateDevicePos(0f, -1f, 0f);
			break;
			
		case R.id.button_up:
			translateDevicePos(0f, 1f, 0f);
			break;
		}
		
		mRenderer.setDeviceLocation(mDevicePosition);
	}
    
    @Override
	public void OnMoved(int pan, int tilt) {
    	// pan and tilt refer to X and Y respectively, and their values
    	// exists in the domain {-10, 10}.
		float diffX = (float)(pan) / 30f;
		float diffZ = (float)(tilt) / 30f;
		
		// Normalize the values
		//float magnitude = (float)Math.sqrt(diffX*diffX + diffZ*diffZ);
		//diffX /= magnitude;
		//diffZ /= magnitude;
		
		translateDevicePos(diffX, 0f, diffZ);
	}

	@Override
	public void OnReleased() {
		
	}
    
    public void onOrientationDataReady(float[] fusedOrientation) {
    	mFusedOrientation = fusedOrientation;
    	
    	mRenderer.setRotationEuler(
	    	(float)Math.toDegrees(fusedOrientation[0]),
	    	(float)Math.toDegrees(fusedOrientation[1]),
	    	(float)Math.toDegrees(fusedOrientation[2])
	    );
    	
    	mRenderer.setDeviceLocation(mDevicePosition);
    }
    
    
    private void translateDevicePos(float x, float y, float z) {
    	if (mFusedOrientation == null) {
    		return;
    	}
    	
    	// Update the position in the XZ plane based on the Y-rotation
    	float yRot = mFusedOrientation[0];
    	
    	float dx = (float)(x*Math.cos(yRot) - z*Math.sin(yRot));
    	float dz = (float)(z*Math.cos(yRot) + x*Math.sin(yRot));
    	
    	mDevicePosition[0] += dx;
    	mDevicePosition[2] += dz;
    	
    	// Update the position in the Y axis directly
    	mDevicePosition[1] += y;
    }
    
    private void initButtonListeners() {
    	((Button)findViewById(R.id.button_up)).setOnClickListener(this);
    	((Button)findViewById(R.id.button_down)).setOnClickListener(this);
    	
    	JoystickView joystick = (JoystickView)findViewById(R.id.joystick);
    	joystick.setOnJoystickMovedListener(this);
    }
}