package pimms.oblig2.graphics;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import pimms.oblig2.R;
import android.content.Context;


public class Scene3D {
	private static final float[] sLightAmbient = new float[] {0.7f, 0.7f, 0.7f, 1f };
	private static final float[] sLightDiffuse = new float[] {1f, 1f, 1f, 1f};
	private static final float[] sLightSpecular = new float[] {1f, 1f, 1f, 1f};
	private static final float[] sLightPosition = new float[] {0f, 50f, 10f, 1f};
	private static final float sLightAttConstant = 0.1f;
	private static final float sLightAttLinear = 0f;
	private static final float sLightAttQuadratic = 0f;
	
	private ArrayList<Object3D> mObjects;
	private Context mContext;
	
	public Scene3D(GL10 gl, Context context) {
		mContext = context;
		mObjects = new ArrayList<Object3D>();
		
		Object3D obj = new Object3D(mContext, "USSEnterprise.modobj");
		obj.setScale(50f);
		obj.setPosition(0f, 100f, 250f);
		obj.setTextureId(R.drawable.enterprise_tex);
		obj.init(gl);
		mObjects.add(obj);
		
		obj = new Object3D(mContext, "Terrain.modobj");
		obj.setTextureId(R.drawable.tertex);
		obj.init(gl);
		mObjects.add(obj);
		
		initLights(gl);
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
