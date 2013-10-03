package pimms.oblig2.graphics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.util.Log;

public abstract class Object3D {
	private static final String TAG = "Obj3D";
	
	private FloatBuffer mVertexBuffer;
	private FloatBuffer mNormalBuffer;
	private FloatBuffer mTexcoordBuffer;
	private int mVertCount;
	private int mTexture = -1;
	
	private FloatBuffer mDbgNormalBuffer;
	
	protected float[] mPosition;
	protected float mScale = 1f;
	protected Context mContext;
	
	public Object3D(Context context) {
		setPosition(0f, 0f, 0f);
		mContext = context;
	}
	
	
	public void init(GL10 gl) {
		loadVertexData();
		loadTexcoordData(gl);
		createDebugNormals();
	}
	
	/*
	 * For simplicity, models can only be scaled uniformly
	 * in all directions. 
	 */
	public void setScale(float scale) {
		mScale = scale;
	}
	
	public void setPosition(float x, float y, float z) {
		mPosition = new float[] {x, y, z};
	}
	
	public final void loadVertexData() {
		float[] vertices = getVertices();
		mVertCount = vertices.length / 3;
		mVertexBuffer = createFloatBuffer(vertices);
    	
    	calculateNormals(vertices);
	}
	
	public final void loadTexcoordData(GL10 gl) {
		float[] texCoord = getTexCoord(gl);
		if (texCoord == null || mTexture == -1) {
			return;
		}
		
		if (texCoord.length / 2 != mVertCount) {
			Log.e(TAG, "Invalid number of texcoord elements");
			return;
		}
		
		mTexcoordBuffer = createFloatBuffer(texCoord);
	}
	
	public final void draw(GL10 gl) {
		gl.glPushMatrix();
		gl.glTranslatef(mPosition[0], mPosition[1], mPosition[2]);
		gl.glScalef(mScale, mScale, mScale);
		
		// Pass the normals if applicable
		if (mNormalBuffer != null) {
			gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
			gl.glNormalPointer(GL10.GL_FLOAT, 0, mNormalBuffer);
		} else {
			gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
		}
		
		// Pass the texture coordinates if applicable
		if (mTexcoordBuffer != null) {
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			gl.glEnable(GL10.GL_TEXTURE_2D);
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTexcoordBuffer);
		} else {
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			gl.glDisable(GL10.GL_TEXTURE_2D);
		}
		
		
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
		gl.glDrawArrays(GL10.GL_TRIANGLES, 0, mVertCount);
		
		gl.glPopMatrix();
		
		int error = gl.glGetError();
		if (error != GL10.GL_NO_ERROR) {
			Log.e(TAG, "OpenGL Error: " + GLU.gluErrorString(error));
		}
		
		/*
		 * Debug draw the normals
		 */
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
		
		gl.glColor4f(0f, 1f, 0f, 1f);
		
		gl.glDisable(GL10.GL_LIGHTING);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mDbgNormalBuffer);
		gl.glDrawArrays(GL10.GL_LINES, 0, mVertCount*2);
		gl.glEnable(GL10.GL_LIGHTING);
	}
	
	
	/*
	 * 
	 */
	abstract float[] getVertices();
	
	/*
	 * Overriders of getTexCoord(GL10) are REQUIRED
	 * to load it's required texture within the method if the
	 * texture coordinates are to be used.
	 */
	abstract float[] getTexCoord(GL10 gl);

	protected void loadTexture(GL10 gl, int id) {
		Resources res = mContext.getResources();
		Drawable drawable = res.getDrawable(id);
		
		if (drawable == null || !(drawable instanceof BitmapDrawable)) {
			Log.e(TAG, "Failed to load texture");
			return;
		}
		
		Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
		
		int tmp[] = new int[1];
		gl.glGenTextures(1, tmp, 0);
		mTexture = tmp[0];
		
		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexture);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
		
		bitmap.recycle();
	}


	private void createDebugNormals() {
		float[] normVert = new float[mVertCount * 6];
		
		mVertexBuffer.position(0);
		mNormalBuffer.position(0);
		
		int i = 0;
		boolean normal = false;
		while (mNormalBuffer.hasRemaining()) {
			if (normal) {
				for (int j=0; j<3; j++) {
					normVert[i] = normVert[i-3] + mNormalBuffer.get();
					i++;
				}
			} else {
				for (int j=0; j<3; j++) {
					normVert[i] = mVertexBuffer.get() * mScale;
					normVert[i] += mPosition[j];
					i++;
				}
			}
			
			normal = !normal;
		}
		
		mDbgNormalBuffer = createFloatBuffer(normVert);
		mVertexBuffer.position(0);
		mNormalBuffer.position(0);
	}
	
	private void calculateNormals(float[] vertices) {
		float[] normals = new float[vertices.length];
		
		int i=0;
		for (; i<vertices.length; i += 9) {
			float[] norm = calculateNormalsForTriangle(vertices, i);
			
			System.arraycopy(norm, 0, normals, i+0, 3);
			System.arraycopy(norm, 0, normals, i+3, 3);
			System.arraycopy(norm, 0, normals, i+6, 3);
		}
		
		mNormalBuffer = createFloatBuffer(normals);
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
		
		float magnintude = (float)Math.sqrt(norm[0]*norm[0] + norm[1]*norm[1] + norm[2]*norm[2]);
		norm[0] /= magnintude;
		norm[1] /= magnintude;
		norm[2] /= magnintude;
		
		return norm;
	}

	private FloatBuffer createFloatBuffer(float[] arr) {
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(arr.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		
		FloatBuffer buffer = byteBuffer.asFloatBuffer();
		buffer.put(arr);
		buffer.position(0);
		
		return buffer;
	}
}




























