package pimms.oblig2.gfx;

import javax.microedition.khronos.opengles.GL10;

import pimms.oblig2.R;
import android.content.Context;

public class Heightmap extends Object3D {

	public Heightmap(float[] position, Context context) {
		super(position, context);
	}
	
	@Override
	float[] getVertices() {
		return new float[] {
			-30f, 0f, -30f,
			-30f, 0f,  30f,
			 30f, 0f, -30f,
			 
			 30f, 0f,  30f,
			 30f, 0f, -30f,
			-30f, 0f,  30f,
		};
	}
	
	@Override
	float[] getTexCoord(GL10 gl) {
		loadTexture(gl, R.drawable.square);
		
		return new float[] {
			0f, 0f,
			0f, 1f,
			1f, 0f,
			
			1f, 1f,
			1f, 0f,
			0f, 1f,
		};
	}
}
