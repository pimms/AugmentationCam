/************************************************************************************
 * Copyright (c) 2012 Paul Lawitzki
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 * 
 * 
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 ************************************************************************************/
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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;


public class AugmentationActivity extends Activity 
			implements 	OnClickListener,
						OrientationSensorListener{
	
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
        mGlView.setEGLConfigChooser( 8, 8, 8, 8, 16, 0 );
        mGlView.getHolder().setFormat( PixelFormat.TRANSLUCENT );
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
		case R.id.button_backward:
			translateDevicePos(0f, 0f, 1f);
			break;
			
		case R.id.button_forward:
			translateDevicePos(0f, 0f, -1f);
			break;
			
		case R.id.button_left:
			translateDevicePos(-1f, 0f, 0f);
			break;
			
		case R.id.button_right:
			translateDevicePos(1f, 0f, 0f);
			break;
			
		case R.id.button_down:
			translateDevicePos(0f, -1f, 0f);
			break;
			
		case R.id.button_up:
			translateDevicePos(0f, 1f, 0f);
			break;
		}
		
		mRenderer.setDeviceLocation(mDevicePosition);
	}
    
    public void onOrientationDataReady(float[] fusedOrientation) {
    	mFusedOrientation = fusedOrientation;
    	
    	mRenderer.setRotationEuler(
	    	(float)Math.toDegrees(fusedOrientation[0]),
	    	(float)Math.toDegrees(fusedOrientation[1]),
	    	(float)Math.toDegrees(fusedOrientation[2])
	    );
    }
    
    
    private void translateDevicePos(float x, float y, float z) {
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
    	((Button)findViewById(R.id.button_left)).setOnClickListener(this);
    	
    	((Button)findViewById(R.id.button_right)).setOnClickListener(this);
    	
    	((Button)findViewById(R.id.button_backward)).setOnClickListener(this);
    	
    	((Button)findViewById(R.id.button_forward)).setOnClickListener(this);
    	
    	((Button)findViewById(R.id.button_up)).setOnClickListener(this);
    	
    	((Button)findViewById(R.id.button_down)).setOnClickListener(this);
    }
}