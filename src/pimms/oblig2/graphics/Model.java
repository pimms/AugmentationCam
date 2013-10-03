package pimms.oblig2.graphics;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.content.Context;
import android.util.Log;

/*
 * Model is an extension of Object3D which loads it's data
 * from a Wavefront Object-file using an ObjLoader object.
 */
public class Model extends Object3D {
	private String mFileName;
	private ObjLoader mObjLoader;
	
	
	public Model(Context context, String file) {
		super(context);
		
		mFileName = file;
		mObjLoader = new ObjLoader();
	}
	
	@Override
	public void init(GL10 gl) {
		if (mObjLoader.parseFile(mFileName, mContext)) {
			super.init(gl);	
		} else {
			Log.e("Model", "Failed to load model " + mFileName);
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
