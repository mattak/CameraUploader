package ylab.donut.camerauploader;
/*
 * SettingActivity
 * 	
 * Description:
 * 	The activity for the Application Setting.
 *  For example, this activity set the following status
 *  - POST url
 *  - Camera Shut Condition
 *  - File attached GPS
 *  - File attached Location
 *  these data is stored on Application Shared Prereference
 * 
 * Log:
 *  10/12/9 last modified
 * */

import android.app.*;
import android.os.Bundle;
import android.preference.*;
import android.widget.*;
import android.view.*;
import android.view.View.OnClickListener;
import android.content.*;
import android.content.SharedPreferences.*;
import android.util.Log;
import java.io.*;
import org.apache.http.client.*;

public class SettingActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener{
	private final static String LOGTAG="SettingActivity";
	
	// Activity Life Cycles
	//-------------------------------------------
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref);
		
		this.initSummaries(this.getPreferenceScreen());
		// register the listener
		this.getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		//setPostListener();
	}
	
	// Preference Change Listener
	//-------------------------------------------
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key){
		Preference pref = findPreference(key);
		this.setSummary(pref);
	}
	
	// Menu
	//-------------------------------------------
	public boolean onCreateOptionsMenu(Menu menu){
		menu.add(Menu.NONE, Menu.FIRST, Menu.NONE, "Camera");
		return super.onCreateOptionsMenu(menu);
	}
	
	public boolean onOptionsItemSelected(MenuItem item){
		// change the activity to
		setResult(RESULT_OK);
		finish();
		return super.onOptionsItemSelected(item);
	}
	
	// Privates
	//-------------------------------------------
	/*private void setPostListener(){
        Button execButton = (Button) findViewById(R.id.button_post);
        execButton.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		TextView responseView = (TextView)findViewById(R.id.textview_response);
        		EditText edit_uri = (EditText)findViewById(R.id.edittext_uri);
        		EditText edit_file = (EditText)findViewById(R.id.edittext_filename);
        		String uri = edit_uri.getText().toString();
        		String filename = edit_file.getText().toString();
        		String response;
        		try{
        			SDcard.writeString(filename,"post check from android mobile");
        			response = NetClient.doGet(uri, null);
        			//response = NetClient.doPostFile(uri, new File("/sdcard/"+filename));
    				responseView.setText(response);
        		}catch(ClientProtocolException e){
        			e.printStackTrace();
        			Log.d(LOGTAG,"protocol:failure");
        			responseView.setText("protocol fail");
        		}catch(IOException e){
        			e.printStackTrace();
        			Log.d(LOGTAG,"get failure");
        			responseView.setText("ioexception fail");
        		}
        	}
        });
	}*/
	
	// Preference Summary Functions
	//-------------------------------------------
	private void initSummaries(PreferenceGroup pg){
		for(int i=0; i<pg.getPreferenceCount(); i++){
			Preference p = pg.getPreference(i);
			if( p instanceof PreferenceGroup){
				this.initSummaries((PreferenceGroup)p);
			}else{
				this.setSummary(p);
			}
		}
	}
	private void setSummary(Preference pref){
		if( pref instanceof ListPreference ){
			ListPreference lp=(ListPreference)pref;
			pref.setSummary(lp.getEntry());
		}else if( pref instanceof EditTextPreference){
			EditTextPreference ep=(EditTextPreference)pref;
			pref.setSummary(ep.getText());
		}
	}
	
}
