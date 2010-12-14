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
import android.content.*;
import android.util.Log;

// Camera 
public class CameraActivity extends Activity{
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
	
	// Intent 
	//----------------------------------------------
	public void doAction(){
		PreferenceController control = new PreferenceController(this);
		if(!control.getBoolean("withshot"))return;
		
		String type = control.getString("postormail");
		String fpath = control.getSaveFilePath();
		
		if( type.equals("post")){
			Log.d(LOGTAG,"post action");
			String posturl = control.getString("posturl");
			try{
				Toast.makeText(this, "now posting", Toast.LENGTH_SHORT).show();
				NetClient.doPostFile(posturl, new File(fpath));
				Toast.makeText(this, "post succeed", Toast.LENGTH_SHORT).show();
			}catch(IOException e){
				Toast.makeText(this, "error:"+e.getMessage(), Toast.LENGTH_SHORT).show();
			}
		}else if( type.equals("mail") ){
			Log.d(LOGTAG,"mail action");
			String[] uriString = {control.getString("mailaddress")};
			String mailbody = control.getString("mailbody");
			String mailtitle = control.getString("mailtitle");
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
}
