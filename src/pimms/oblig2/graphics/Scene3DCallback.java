package pimms.oblig2.graphics;

public interface Scene3DCallback {
	void onScene3DLoadBegin();
	void onScene3DLoadInProgress(int progress);
	void onScene3DLoadCompleted();
}
