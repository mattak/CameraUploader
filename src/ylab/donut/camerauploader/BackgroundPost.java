package ylab.donut.camerauploader;

import org.apache.http.client.ClientProtocolException;

import android.app.ProgressDialog;
import android.app.Activity;
import android.util.Log;
import android.widget.Toast;
import android.os.AsyncTask;

import java.io.File;
import java.io.IOException;

public class BackgroundPost extends AsyncTask<String, Integer, Long>{
	private static final String LOGTAG = "BackgroundPost";
	private ProgressDialog progressDialog = null;
	private Activity mainActivity;
	
	public BackgroundPost(Activity activity){
		mainActivity = activity;
	}
	
	protected void onPreExecute(){
		Log.d(LOGTAG,"onpreExecute");
		progressDialog = new ProgressDialog(mainActivity);
		progressDialog.setTitle("now uploading...");
		progressDialog.setMessage("please wait... ");
		progressDialog.setIndeterminate(false);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.show();
	}
	
	protected Long doInBackground(String... params){
		PreferenceController preferenceControl = new PreferenceController(mainActivity);
		String url = preferenceControl.getString("posturl");
		String postname = preferenceControl.getString("postname");
		String filepath = preferenceControl.getSaveFilePath();
		File file = new File(preferenceControl.getSaveFilePath());
		progressDialog.setMessage(
				   "To:\n  "+url
				+"\npostname:\n  "+filepath
				+"\nresolution:\n  "+preferenceControl.getString("camera_picturesize")
				+"\nsize:\n  "+file.length());
		try{
			NetClient.doPostFile(url, postname, file);
		}catch(ClientProtocolException e){
			Log.d(LOGTAG,"ClientProtocolException");
			Toast.makeText(this.mainActivity, "ClientProtocolException", Toast.LENGTH_SHORT).show();
		}catch(IOException e){
			Log.d(LOGTAG,"IOException");
			Toast.makeText(this.mainActivity, "IOException", Toast.LENGTH_SHORT).show();
		}
		return 0L;
	}
	
	protected void onProgressUpdate(Integer... progress){
		this.progressDialog.setProgress(progress[0]);
	}
	
	/*
	 * When BackgroundAction is End
	 * */
	protected void onPostExecute(Long result){
		this.progressDialog.dismiss();
		Toast.makeText(this.mainActivity, "upload succeed!", Toast.LENGTH_SHORT).show();
	}
}
