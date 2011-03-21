package ylab.donut.camerauploader;
/*
 * Camera Activity
 * 
 * Description:
 *  Camera activity. Shot the photo
 *  
 * Last up date:
 *  10/12/15
 *  
 * What should i do next:
 *  resolusion setting.
 *  layout image.
 *  gps lat lon.
 * */
import java.io.*;
import java.util.*;

import org.apache.http.client.ClientProtocolException;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.*;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.content.*;
import android.util.Log;

// Camera 
public class CameraActivity extends Activity{
	private final static String LOGTAG="Camera";
	private CameraView cameraView;
	private CameraOverlay cameraOverlay;
	private SensorController sensorController;
	private GPSController gpsController;
	// Application initialize
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Log.d(LOGTAG,"onCreate");
		cameraView=new CameraView(this);
		setContentView(cameraView);
		cameraOverlay = new CameraOverlay(this,cameraView);
		addContentView(cameraOverlay, new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		
		//
		sensorController = SensorController.getInstance();
		sensorController.init(this);
		gpsController = GPSController.getInstance();
		gpsController.init(this);
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
		sensorController.stop();
		gpsController.stop();
	}
	public void onResume(){
		super.onResume();
		Log.d(LOGTAG,"onResume");
		//cameraView=new CameraView(this);
		//setContentView(cameraView);
		sensorController.start();
		gpsController.start();
	}
	public void onStart(){
		super.onStart();
		Log.d(LOGTAG,"onStart");
	}
	public void onStop(){
		super.onStop();
		Log.d(LOGTAG,"onStop");
	}
	
	// Intent 
	//----------------------------------------------
	public void doAction(){
		PreferenceController control = new PreferenceController(this);
		if(!control.getBoolean("withshot"))return;
		
		String type = control.getString("postormail");
		
		if( type.equals("post")){
			Log.d(LOGTAG,"post action");
			postAction(control);
		}else if( type.equals("mail") ){
			mailAction(control);
		}
	}
	
	private void postAction(PreferenceController control){
		new BackgroundPost(this).execute("");
		/*String url=control.getString("posturl");
		String name=control.getString("postname");
		String fpath=control.getSaveFilePath();
		try{
			Toast.makeText(this, "now posting", Toast.LENGTH_SHORT).show();
			NetClient.doPostFile(url, name, new File(fpath));
			Toast.makeText(this, "post succeed", Toast.LENGTH_SHORT).show();
		}catch(IOException e){
			Toast.makeText(this, "error:"+e.getMessage(), Toast.LENGTH_SHORT).show();
		}*/
	}
	
	private void mailAction(PreferenceController control){
		String[] uriString = {control.getString("mailaddress")};
		String mailbody = control.getString("mailbody");
		String mailtitle = control.getString("mailtitle");
		String fpath = control.getSaveFilePath();
		String fullpath = "file://" + fpath;
		
		Intent intent=new Intent(Intent.ACTION_SEND);
		intent.putExtra(Intent.EXTRA_EMAIL, uriString);
		intent.putExtra(Intent.EXTRA_SUBJECT, mailtitle);
		intent.putExtra(Intent.EXTRA_TEXT, mailbody);
		intent.setType("image/jpeg");
		intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(fullpath));
		startActivity(intent);
	}
	
}
