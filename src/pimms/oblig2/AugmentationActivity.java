package pimms.oblig2;

import pimms.oblig2.graphics.GLSceneRenderer;
import pimms.oblig2.graphics.Scene3DCallback;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
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
						OrientationSensorListener,
						JoystickMovedListener,
						Scene3DCallback {
	
	/* Variables used in the implementation of Scene3DCallback */
	private Handler mHandler;
	private ProgressDialog mProgress;
	
	private GLSurfaceView mGlView;
	private GLSceneRenderer mRenderer;
	private OrientationSensorReader mSensors;
	
	private float[] mFusedOrientation;
	private float[] mDevicePosition;
	
	private int lastX, lastZ;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        
        setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE );
        
        requestWindowFeature( Window.FEATURE_NO_TITLE );
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
                                WindowManager.LayoutParams.FLAG_FULLSCREEN );
        
        setContentView(R.layout.activity_augmentation);
        
        // Create an openGL view and set it as the main layout
        mGlView = (GLSurfaceView)findViewById(R.id.glView);
        mGlView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        mGlView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        mRenderer = new GLSceneRenderer(this, this);
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
    	((Button)findViewById(R.id.button_up)).setOnTouchListener(new RepeatListener(0, 0, new OnClickListener() {
    		  @Override
    		  public void onClick(View view) {
    		    translateDevicePos(0f, 0.001f, 0f);
    		  }
    		}));
    	((Button)findViewById(R.id.button_down)).setOnTouchListener(new RepeatListener(0, 0, new OnClickListener() {
    		  @Override
    		  public void onClick(View view) {
    		    translateDevicePos(0f, -0.001f, 0f);
    		  }
    		}));
    	
    	JoystickView joystick = (JoystickView)findViewById(R.id.joystick);
    	joystick.setOnJoystickMovedListener(this);
    	joystick.setOnTouchListener(new RepeatListener(0, 0, new OnClickListener() {
    		@Override
    		public void onClick(View view) {
    			OnMoved(lastX, lastZ);
    			}
    		}));
    	}

    
    /*
	 * OrientationSensorListener implementation
	 */
    public void onOrientationDataReady(float[] fusedOrientation) {
    	mFusedOrientation = fusedOrientation;
    	
    	mRenderer.setRotationEuler(
	    	(float)Math.toDegrees(fusedOrientation[0]),
	    	(float)Math.toDegrees(fusedOrientation[1]),
	    	(float)Math.toDegrees(fusedOrientation[2])
	    );
    	
    	mRenderer.setDeviceLocation(mDevicePosition);
    }
    
    /*
     * JoystickMovedListener implementation
     */
    @Override
	public void OnMoved(int pan, int tilt) {
    	// pan and tilt refer to X and Y respectively, and their values
    	// exists in the domain {-10, 10}.
		float diffX = (float)(pan) / 3000f;
		float diffZ = (float)(tilt) / 3000f;
		
		lastX = pan;
		lastZ = tilt;
		
		translateDevicePos(diffX, 0f, diffZ);
	}

	@Override
	public void OnReleased() {
		
	}
	
	/*
	 * OnClickListener implementation
	 */
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
	
	/*
	 * Scene3DCallback implementation
	 */
	@Override
	public void onScene3DLoadBegin() {
		final Context context = this;
		
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				mProgress = new ProgressDialog(context);
				mProgress.setTitle("Loading scene");
				mProgress.setMessage("0%");
				mProgress.setCancelable(false);
				mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				mProgress.show();
			}
		};
		
		mHandler.post(runnable);
	}
	
	@Override
	public void onScene3DLoadInProgress(final int progress) {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				mProgress.setMessage(progress + "%");
			}
		};
		
		mHandler.post(runnable);
	}
	
	@Override
	public void onScene3DLoadCompleted() {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				mProgress.dismiss();
			}
		};
		
		mHandler.post(runnable);
	}
}































