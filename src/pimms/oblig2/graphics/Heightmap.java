package pimms.oblig2.graphics;

import javax.microedition.khronos.opengles.GL10;

import pimms.oblig2.R;
import android.content.Context;

public class Heightmap extends Object3D {

	private static final int xQuads = 32;
	private static final int zQuads = 32;
	private static final int quadSize = 4;
	
	public Heightmap(float[] position, Context context) {
		super(position, context);
	}
	
	@Override
	float[] getVertices() {
		// Randomize the heights
		float[][] height = new float[xQuads+1][zQuads+1];
		for (int i=0; i<xQuads+1; i++) {
			for (int j=0; j<zQuads+1; j++) {
				height[i][j] = (float)(Math.random() * 2f) - 1f;
			}
		}
		
		// 3 floats * 6 vertices = 18 floats
		float[] verts = new float[xQuads * zQuads * 18];
		
		for (int i = 0; i < xQuads; i++) {	
			for (int j = 0; j < zQuads; j++) {
				float xpos = (i-xQuads/2)*quadSize;
				float zpos = (j-zQuads/2)*quadSize;
				
				int idx = (zQuads * i + j) * 18;
				
				verts[idx + 0] = xpos;
				verts[idx + 1] = height[i][j+1];
				verts[idx + 2] = zpos + quadSize;
				
				verts[idx + 3] = xpos + quadSize;
				verts[idx + 4] = height[i+1][j];
				verts[idx + 5] = zpos;
				
				verts[idx + 6] = xpos;
				verts[idx + 7] = height[i][j];
				verts[idx + 8] = zpos;
				
				
				verts[idx + 9] = xpos + quadSize;
				verts[idx +10] = height[i+1][j+1];
				verts[idx +11] = zpos + quadSize;
				
				verts[idx +12] = xpos + quadSize;
				verts[idx +13] = height[i+1][j];
				verts[idx +14] = zpos;
				
				verts[idx +15] = xpos;
				verts[idx +16] = height[i][j+1];
				verts[idx +17] = zpos + quadSize;
			}
		}	
		
		return verts;
	}
	
	@Override
	float[] getTexCoord(GL10 gl) {
		loadTexture(gl, R.drawable.elwynngrassbase);
		
		float[] texCoord = new float[xQuads * zQuads * 12];
		
		for (int i=0; i<xQuads; i++) {
			for (int j=0; j<zQuads; j++) {
				int idx = (i*xQuads+j) * 12;
				
				texCoord[idx + 0] = 0f;
				texCoord[idx + 1] = 1f;
				
				texCoord[idx + 2] = 1f;
				texCoord[idx + 3] = 0f;
				
				texCoord[idx + 4] = 0f;
				texCoord[idx + 5] = 0f;
				
				texCoord[idx + 6] = 1f;
				texCoord[idx + 7] = 1f;
				
				texCoord[idx + 8] = 1f;
				texCoord[idx + 9] = 0f;
				
				texCoord[idx +10] = 0f;
				texCoord[idx +11] = 1f;
			}
		}
		
		return texCoord;
	}
}






























