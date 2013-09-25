package pimms.oblig2.gfx;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;


public class Scene3D {
	private static final float[] sLightAmbient = new float[] {0.7f, 0.7f, 0.7f, 1f };
	private static final float[] sLightDiffuse = new float[] {0.55f, 0.5f, 0.5f, 1f};
	private static final float[] sLightSpecular = new float[] {1f, 1f, 1f, 1f};
	private static final float[] sLightPosition = new float[] {-5f, 10f, -5f, 1f};
	
	ArrayList<Object3D>	mObjects;
	
	public Scene3D(GL10 gl) {
		mObjects = new ArrayList<Object3D>();
			
		int cubes = 32;
		double rad = Math.PI * 2;
		while (rad >= 0.0) {
			float x = (float)Math.cos(rad) * 20f;
			float z = (float)Math.sin(rad) * 20f;
			mObjects.add(new Box(new float[] {x, 0f, z}));
			
			rad -= (Math.PI*2) / cubes;
		}
		
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
		gl.glEnable(GL10.GL_LIGHT0);
		
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, sLightAmbient, 0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, sLightDiffuse, 0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, sLightSpecular, 0);
	}
}
