package pimms.oblig2.gfx;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.lang.System;

import javax.microedition.khronos.opengles.GL10;

public abstract class Object3D {
	private FloatBuffer mVertexBuffer;
	private int mVertCount;
	
	private FloatBuffer mColorBuffer;
	private int mColorCount;
	
	private FloatBuffer mNormalBuffer;
	
	protected float[] mPosition;
	
	public Object3D(float[] position) {
		assert(position.length == 3);
		mPosition = position;
		
		loadVertexData();
		loadColorData();
	}
	
	public final void loadVertexData() {
		float[] vertices = getVertices();
		mVertCount = vertices.length / 3;
		
		ByteBuffer byteBuffer =	ByteBuffer.allocateDirect(vertices.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		
    	mVertexBuffer = byteBuffer.asFloatBuffer();
    	mVertexBuffer.put(vertices);
    	mVertexBuffer.position(0);
    	
    	calculateNormals(vertices);
	}
	
	public final void loadColorData() {
		float[] colors = getColors();
		if (colors == null) {
			return;
		}
		mColorCount = colors.length / 3;
		assert(mColorCount == mVertCount);
		
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(colors.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		
		mColorBuffer = byteBuffer.asFloatBuffer();
		mColorBuffer.put(colors);
		mColorBuffer.position(0);
	}
	
	public final void draw(GL10 gl) {
		gl.glPushMatrix();
		gl.glTranslatef(mPosition[0], mPosition[1], mPosition[2]);
		
		// Pass the colors if applicable
		if (mColorBuffer != null) {
			gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
			gl.glColorPointer(3, GL10.GL_FLOAT, 0, mColorBuffer);
		} else {
			gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		}
		
		// Pass the normals if applicable
		if (mNormalBuffer != null) {
			gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
			gl.glNormalPointer(GL10.GL_FLOAT, 0, mNormalBuffer);
		} else {
			gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
		}
		
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
		gl.glDrawArrays(GL10.GL_TRIANGLES, 0, mVertCount);
		mVertexBuffer.position(0);
		
		gl.glPopMatrix();
	}
	
	
	abstract float[] getVertices();
	abstract float[] getColors();
	
	private void calculateNormals(float[] vertices) {
		float[] normals = new float[vertices.length];
		
		for (int i=0; i<vertices.length; i += 9) {
			float[] norm = calculateNormalsForTriangle(vertices, i);
			
			System.arraycopy(norm, 0, normals, i+0, 3);
			System.arraycopy(norm, 0, normals, i+3, 3);
			System.arraycopy(norm, 0, normals, i+6, 3);
		}
		
		ByteBuffer byteBuffer =	ByteBuffer.allocateDirect(normals.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		
    	mNormalBuffer = byteBuffer.asFloatBuffer();
    	mNormalBuffer.put(normals);
    	mNormalBuffer.position(0);
	}
	
	private float[] calculateNormalsForTriangle(float[] vertices, int start) {
		assert(start + 9 < vertices.length);
		
		float[] norm = new float[3];
		float[] v1 = new float[3];
		float[] v2 = new float[3];
		float[] v3 = new float[3];
		
		// Copy the raw vertices
		System.arraycopy(vertices, start+0, v1, 0, 3);
		System.arraycopy(vertices, start+3, v2, 0, 3);
		System.arraycopy(vertices, start+6, v3, 0, 3);
		
		// Adjust v1 and v2 to be relative to v3
		for (int i=0; i<3; i++) {
			v1[i] = v3[i] - v1[i];
			v2[i] = v3[i] - v2[i];
		}
		
		// Calculate the cross product
		// norm.x = (u.y * v.z) - (u.z * v.y)
		norm[0] = (v1[1] * v2[2]) - (v1[2] * v2[1]);
		
		// norm.y = (u.z * v.x) - (u.x * v.z)
		norm[1] = (v1[2] * v2[0]) - (v1[0] * v2[2]);
		
		// norm.z = (u.x * v.y) - (u.y * v.x)
		norm[2] = (v1[0] * v2[1]) - (v1[1] * v2[0]);
		
		return norm;
	}
}




























