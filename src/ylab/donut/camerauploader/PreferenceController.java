package ylab.donut.camerauploader;

import android.content.*;
import android.preference.*;

public class PreferenceController{
	private SharedPreferences share;
	private Context context;
	public PreferenceController(Context c){
		context=c;
		updateSharedPreferences();
	}
	public boolean getBoolean(String key){
		//share = PreferenceManager.getDefaultSharedPreferences(context);
		return share.getBoolean(key,false);
	}
	public String getString(String key){
		//share = PreferenceManager.getDefaultSharedPreferences(context);
		return share.getString(key, "");
	}
	public int getInt(String key){
		return share.getInt(key, 0);
	}
	public String getSaveFilePath(){
		//share = PreferenceManeger.getDefaultS
		String filename = share.getString("filename", "");
		return "/sdcard/" + filename;
	}
	
	private void updateSharedPreferences(){
		share = PreferenceManager.getDefaultSharedPreferences(context);
	}
}
