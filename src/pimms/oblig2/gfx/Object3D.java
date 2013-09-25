package pimms.oblig2.gfx;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public abstract class Object3D {
	protected FloatBuffer mVertexBuffer;
	protected float[] mPosition;
	
	private int mVertCount;
	
	public Object3D(float[] position) {
		assert(position.length == 3);
		mPosition = position;
		
		loadVertexData();
	}
	
	public final void loadVertexData() {
		float[] vertices = getVertices();
		mVertCount = vertices.length;
		
		ByteBuffer byteBuffer =	ByteBuffer.allocateDirect(mVertCount * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		
    	mVertexBuffer = byteBuffer.asFloatBuffer();
    	mVertexBuffer.put(vertices);
    	mVertexBuffer.position(0);
	}
	
	public final void draw(GL10 gl) {
		gl.glPushMatrix();
		gl.glTranslatef(mPosition[0], mPosition[1], mPosition[2]);
		
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
		gl.glDrawArrays(GL10.GL_TRIANGLES, 0, mVertCount / 3);
		mVertexBuffer.position(0);
		
		gl.glPopMatrix();
	}
	
	abstract float[] getVertices();
}
