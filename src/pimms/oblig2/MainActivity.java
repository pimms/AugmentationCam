package pimms.oblig2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_main);
	    
	    Button button = (Button)findViewById(R.id.main_btn_go);
	    if (button != null) {
	    	final Context context = this;
	    	
	    	button.setOnClickListener(new OnClickListener() {
	    		@Override
	    		public void onClick(View v) {
	    			Intent intent = new Intent(context, AugmentationActivity.class);
	    			startActivity(intent);
	    		}
	    	});
	    }
	}
}
