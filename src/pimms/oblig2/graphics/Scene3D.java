package pimms.oblig2.graphics;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import pimms.oblig2.R;
import android.content.Context;


public class Scene3D {
	private static final float[] sLightAmbient = new float[] {0.7f, 0.7f, 0.7f, 1f };
	private static final float[] sLightDiffuse = new float[] {1f, 1f, 1f, 1f};
	private static final float[] sLightSpecular = new float[] {1f, 1f, 1f, 1f};
	private static final float[] sLightPosition = new float[] {0f, -1f, 0f, 0f};
	private static final float sLightAttConstant = 0.1f;
	private static final float sLightAttLinear = 0f;
	private static final float sLightAttQuadratic = 0f;
	
	private ArrayList<Object3D> mObjects;
	private Context mContext;
	private Scene3DCallback mCallback;
	
	/*
	 * The load progress is calculated uniformly
	 * based on the loaded models. A stupidly large
	 * model accounts for the same amount of progress as a
	 * small model. This is done for simplicity's sake.
	 */
	private int mObjectsToLoad = 2;
	
	public Scene3D(Context context, Scene3DCallback callback) {
		mContext = context;
		mObjects = new ArrayList<Object3D>();
		
		mCallback = callback;
	}
	
	public void loadScene(GL10 gl) {
		mCallback.onScene3DLoadBegin();
		
		Object3D obj = new Object3D(this, mContext, "USSEnterprise.modobj");
		obj.setScale(50f);
		obj.setPosition(0f, 200f, 500f);
		obj.setTextureId(R.drawable.enterprise_tex);
		obj.init(gl);
		mObjects.add(obj);

		obj = new Object3D(this, mContext, "Terrain.modobj");
		obj.setTextureId(R.drawable.tertex);
		obj.init(gl);
		mObjects.add(obj);
		
		initLights(gl);
		
		mCallback.onScene3DLoadCompleted();
	}
	
	public void setModelProgress(int progress) {
		float completedPercent = ((float)mObjects.size() / (float)mObjectsToLoad) * 100f;
		completedPercent += (float)progress / (float)mObjectsToLoad;
		
		mCallback.onScene3DLoadInProgress((int)completedPercent);
	}
	
	
	public void drawScene(GL10 gl) {
		for (Object3D obj : mObjects) {
			obj.draw(gl);
		}
		
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, sLightPosition, 0);
	}
	
	protected void initLights(GL10 gl) {
		gl.glEnable(GL10.GL_LIGHTING);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glEnable(GL10.GL_LIGHT0);
		
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, sLightAmbient, 0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, sLightDiffuse, 0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, sLightSpecular, 0);
		
		gl.glLightf(GL10.GL_LIGHT0, GL10.GL_CONSTANT_ATTENUATION, sLightAttConstant);
		gl.glLightf(GL10.GL_LIGHT0, GL10.GL_QUADRATIC_ATTENUATION, sLightAttLinear);
		gl.glLightf(GL10.GL_LIGHT0, GL10.GL_LINEAR_ATTENUATION, sLightAttQuadratic);
	}
}
