package pimms.oblig2.graphics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.util.Log;

public class Object3D {
	private static final String TAG = "Obj3D";
	
	private FloatBuffer mVertexBuffer;
	private FloatBuffer mNormalBuffer;
	private FloatBuffer mTexcoordBuffer;
	private ShortBuffer mIndexBuffer;
	private int mVertCount;
	private int mTexture = -1;
	
	private int mTextureRId = -1;
	private String mFileName;
	private ObjLoader mObjLoader;
	
	protected float[] mPosition;
	protected float mScale = 1f;
	protected Context mContext;
	protected Scene3D mScene;
	
	public Object3D(Scene3D scene, Context context, String fileName) {
		setPosition(0f, 0f, 0f);
		mContext = context;
		mFileName = fileName;
		mObjLoader = new ObjLoader();
		mScene = scene;
	}
	
	/** 
	 * setTexture() 
	 * 	@param textureId  	The R-id of the texture resource.
	 * This method must be called before init(GL10) if a texture
	 * should be used.
	 */
	public void setTextureId(int textureRId) {
		mTextureRId = textureRId;
	}
	
	public void init(GL10 gl) {
		if (!mObjLoader.parseFile(mFileName, mContext)) {
			Log.e(TAG, "Failed to load model " + mFileName);
			return;
		}
		
		mScene.setModelProgress(80);
		
		loadVertexData();
		mScene.setModelProgress(90);
		
		loadTexcoordData(gl);
		mScene.setModelProgress(100);
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
		
		short[] indices = getIndices();
		mIndexBuffer = createShortBuffer(indices);
    	
		float[] normals = getNormals();
		mNormalBuffer = createFloatBuffer(normals);
		
    	//calculateNormals(vertices, indices);
    	
    	Log.d(TAG, indices.length + " indices and " + mVertCount + " vertices.");
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
		if (mScale != 1.0f) {
			gl.glEnable(GL10.GL_NORMALIZE);
		} else {
			gl.glDisable(GL10.GL_NORMALIZE);
		}
		
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
			gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexture);
			Log.i(TAG, "using texture with id " + mTexture);
		} else {
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			gl.glDisable(GL10.GL_TEXTURE_2D);
			gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);
			Log.e(TAG, "NOT USING TEXTURE :'(");
		}
		
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
		gl.glDrawElements(GL10.GL_TRIANGLES, mIndexBuffer.limit(), GL10.GL_UNSIGNED_SHORT, mIndexBuffer);
		
		gl.glPopMatrix();
		
		int error = gl.glGetError();
		if (error != GL10.GL_NO_ERROR) {
			Log.e(TAG, "OpenGL Error: " + GLU.gluErrorString(error));
		}
	}
	
	/* Textures MUST be loaded from this method */
	protected float[] getTexCoord(GL10 gl) {
		if (mTextureRId == -1) {
			Log.d(TAG, "Not using texture for this model");
			return null;
		}
		
		loadTexture(gl, mTextureRId);
		if (mTexture == -1) {
			Log.e(TAG, "Failed to load texture");
			return null;
		}
		
		return mObjLoader.getTexCoords();
	}
	
	protected float[] getVertices() {
		return mObjLoader.getVertices();
	}
	
	protected float[] getNormals() {
		return mObjLoader.getNormals();
	}
	
	protected short[] getIndices() {
		return mObjLoader.getIndices();
	}
	
	
	private void loadTexture(GL10 gl, int id) {
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
	}

	private FloatBuffer createFloatBuffer(float[] arr) {
		ByteBuffer byteBuffer = createByteBuffer(arr.length * 4);
		FloatBuffer buffer = byteBuffer.asFloatBuffer();
		
		buffer.put(arr);
		buffer.position(0);
		
		return buffer;
	}
	
	private ShortBuffer createShortBuffer(short[] arr) {
		ByteBuffer byteBuffer = createByteBuffer(arr.length * 2);
		ShortBuffer buffer = byteBuffer.asShortBuffer();
		
		buffer.put(arr);
		buffer.position(0);
		
		return buffer;
	}
	
	private ByteBuffer createByteBuffer(int length) {
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(length);
		byteBuffer.order(ByteOrder.nativeOrder());
		
		return byteBuffer;
	}
}




























