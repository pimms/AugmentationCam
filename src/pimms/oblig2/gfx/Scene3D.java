package pimms.oblig2.gfx;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;


public class Scene3D {
	ArrayList<Object3D>	mObjects;
	
	public Scene3D(GL10 gl) {
		mObjects = new ArrayList<Object3D>();
			
		mObjects.add(new Box(new float[]{0f, 0f, 0f}));
		
		//initLights(gl);
	}
	
	public void drawScene(GL10 gl) {
		for (Object3D obj : mObjects) {
			obj.draw(gl);
		}
	}
	
	
	
	protected void initLights(GL10 gl) {
		gl.glEnable(GL10.GL_LIGHTING);
		gl.glEnable(GL10.GL_LIGHT0);
		
		
	}
}
