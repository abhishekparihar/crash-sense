package weboapps.crashsense;

import java.io.ObjectInputStream.GetField;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
	TextView textView;
	private final static String TAG=GetField.class.getName();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
//		MyApplication myApplication= new MyApplication();
//		myApplication.setContext(this);
		
		Log.v(TAG,"main");
		
		textView = (EditText)findViewById(R.id.textView);
		textView.setText(R.string.app_name);
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
