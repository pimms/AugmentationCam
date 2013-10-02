package pimms.oblig2.graphics;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

/*
 * Model is an extension of Object3D which loads it's data
 * from a Wavefront Object-file using an ObjLoader object.
 */
public class Model extends Object3D {
	private String mFileName;
	private ObjLoader mObjLoader;
	
	
	public Model(float[] position, Context context, String file) {
		super(position, context);
		
		mFileName = file;
		mObjLoader = new ObjLoader();
	}
	
	@Override
	public void init(GL10 gl) {
		if (mObjLoader.parseFile(mFileName, mContext)) {
			super.init(gl);	
		}
	}

	@Override
	float[] getVertices() {
		return mObjLoader.getVertices();
	}

	@Override
	float[] getTexCoord(GL10 gl) {
		return null;
	}

}