package pimms.oblig2.graphics;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;


public class Scene3D {
	private static final float[] sLightAmbient = new float[] {0.7f, 0.7f, 0.7f, 1f };
	private static final float[] sLightDiffuse = new float[] {1f, 1f, 1f, 1f};
	private static final float[] sLightSpecular = new float[] {1f, 1f, 1f, 1f};
	private static final float[] sLightPosition = new float[] {0f, 50f, 10f, 1f};
	private static final float sLightAttConstant = 0.25f;
	private static final float sLightAttLinear = 0f;
	private static final float sLightAttQuadratic = 0f;
	
	private ArrayList<Object3D> mObjects;
	private Context mContext;
	
	public Scene3D(GL10 gl, Context context) {
		mContext = context;
		mObjects = new ArrayList<Object3D>();
			
		int cubes = 32;
		double rad = Math.PI * 2;
		while (rad >= 0.0) {
			float x = (float)Math.cos(rad) * 20f;
			float z = (float)Math.sin(rad) * 20f;
			
			Object3D obj = new Box(mContext);
			obj.setPosition(x, 1f, z);
			obj.init(gl);
			mObjects.add(obj);
			
			
			rad -= (Math.PI*2) / cubes;
		}
		
		Object3D obj = new Heightmap(mContext);
		obj.init(gl);
		mObjects.add(obj);
		
		obj = new Model(mContext, "USSEnterprise.obj");
		obj.setScale(50f);
		obj.setPosition(0f, 100f, 250f);
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
