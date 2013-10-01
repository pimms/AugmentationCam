package pimms.oblig2;

public interface OrientationSensorListener {
	/** 
	 * @param deviceOrientation
	 * 		  An array of length 3 containing the device's orientation
	 * 		  in Euler-angles.
	 */
	void onOrientationDataReady(float[] deviceOrientation);
}
