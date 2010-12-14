package ylab.donut.camerauploader;
/*
 * Camera Activity
 * 
 * Description:
 *  Camera activity. Shot the photo
 *  
 * Last up date:
 *  10/12/9
 * */
import java.io.*;
import java.util.*;

import org.apache.http.client.ClientProtocolException;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.*;
import android.view.*;
import android.view.View.OnClickListener;
import android.content.*;
import android.util.Log;

// Camera 
public class Camera extends Activity{
	private final static String LOGTAG="Camera";
	private CameraView cameraView;
	// Application initialize
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Log.d(LOGTAG,"onCreate");
		cameraView=new CameraView(this);
		setContentView(cameraView);
		/*setContentView(R.layout.main);
		
		Button bt=(Button)this.findViewById(R.id.execbutton);
		bt.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				EditText text=(EditText)findViewById(R.id.edittext_uri);
				SharedPreferences shared=PreferenceManager.getDefaultSharedPreferences(text.getContext());
				String uri=shared.getString("posturl", "");
				text.setText(uri);
			}});*/
	}
		
	// menu
	//--------------------------------------------
	public boolean onCreateOptionsMenu(Menu menu){
		menu.add(Menu.NONE, Menu.FIRST, Menu.NONE, "Setting");
		return super.onCreateOptionsMenu(menu);
	}
	
	public boolean onOptionsItemSelected(MenuItem item){
		// camera release
		cameraView.destroyDrawingCache();
		
		Intent intent = new Intent(this, SettingActivity.class);
		this.startActivity(intent);
		return super.onOptionsItemSelected(item);
	}
	
	
	// onPause onResume is important for camera
	//----------------------------------------------
	public void onPause(){
		super.onPause();
		Log.d(LOGTAG,"onPause");
	}
	public void onResume(){
		super.onResume();
		Log.d(LOGTAG,"onResume");
		cameraView=new CameraView(this);
		setContentView(cameraView);
	}
	public void onStart(){
		super.onStart();
		Log.d(LOGTAG,"onStart");
	}
	public void onStop(){
		super.onStop();
		Log.d(LOGTAG,"onStop");
	}
}
