package ylab.donut.camerauploader;
/*
 * Camera View
 * 
 * Description:
 * 	Camera Surface View
 * Last modified:
 * 	10/12/15
 * */

import android.app.*;
import android.content.*;
import android.view.*;
import android.widget.Toast;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.hardware.Camera.Parameters;

import android.preference.PreferenceManager;
import android.util.*;
import java.io.FileOutputStream;
import java.util.List;

// Camera Control 
public class CameraView extends SurfaceView
	implements SurfaceHolder.Callback{
	private SurfaceHolder holder;
	private Camera camera;
	private CameraActivity cameraActivity;
	private PreferenceController preferenceController;
	private boolean apihigh=true;
	private boolean touchhold=false;
	private boolean tackenpicture=false;
	private final static String LOGTAG ="CameraView";
	
	public CameraView(CameraActivity context){
		super(context);
		cameraActivity = context;
		preferenceController= new PreferenceController(cameraActivity);
		
		// Surface Holder Creation
		holder=getHolder();
		holder.addCallback(this);
		
		// Recent Pushing Buffer
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}
	
	// Surface Life Cycle
	//------------------------------------------------
	public void surfaceCreated(SurfaceHolder holder){
		Log.d(LOGTAG,"surfaceCreated");
		camera=Camera.open();
		try{
			camera.setPreviewDisplay(holder);
		}catch(Exception e){
			camera.release();
			camera=null;
			Log.d(LOGTAG, "restart false");
		}
	}
	
	public void surfaceChanged(SurfaceHolder holder, int foramt ,int w, int h){
		// Camera Preview Start
		Log.d(LOGTAG,"surfaceChanged");
		Camera.Parameters params=camera.getParameters();
		List<Size> sizes = Reflect.getSupportedPreviewSizes(params);
		
		if(null != sizes){
			Size optimalSize = getOptimalPreviewSize(sizes,w,h);
			params.setPreviewSize(optimalSize.width,optimalSize.height);
		}else{
			params.setPreviewSize(w,h);
			apihigh=false;
		}
		
		
		setPictureSize(params,w,h);
		setJpegQuality(params);
		PreferenceController control=new PreferenceController(cameraActivity);
		if(control.getBoolean("camera_latlon")){
			setGeoParams(params);
		}
		
		camera.setParameters(params);
		camera.startPreview();
	}
	
	public void surfaceDestroyed(SurfaceHolder holder){
		// Camera Preview Stop
		Log.d(LOGTAG,"surfaceDestroyed");
		camera.stopPreview();
		camera.release();
		camera=null;
	}
	
	// Event Dispatch
	//----------------------------------------------
	public boolean onTouchEvent(MotionEvent event){
		if(event.getAction()==MotionEvent.ACTION_UP){
			// api level is low or first touch 
			/*if( touchhold ){
				takePicture();
			}
			
			if(!touchhold){// || !apihigh){
				Log.d(LOGTAG,"!touchhold || !apihigh");
				touchhold=true;
				Camera.Parameters params=camera.getParameters();
				camera.setParameters(params);
				camera.startPreview();
			}else{
				touchhold=false;
			}*/
		}
		return true;
	}
	
	// Restart Preview
	//-------------------------------------------
	public void restartPreview(){
		Log.d(LOGTAG,"cameraview touch");
		if(!touchhold && tackenpicture){
			tackenpicture = false;
			Camera.Parameters params=camera.getParameters();
			camera.setParameters(params);
			camera.startPreview();
		}
	}
	
	// Take Picture 
	//-------------------------------------------
	public void takePicture(){
		// Camera Screen Shot
		if( !tackenpicture ){
			Log.d(LOGTAG,"takepicture");
			touchhold = true;
			PreferenceController control=new PreferenceController(cameraActivity);
			if(!control.getBoolean("camera_autofocus")){
				camera.takePicture(shutterListener,null, pictureCallback);
			}else{
				camera.autoFocus(autoFocusCallback);
			}
			touchhold = false;
			tackenpicture = true;
		}
	}
	
	// Do Focus
	//-------------------------------------------
	public void doOnlyFocus(){
		camera.autoFocus(autoFocusNullCallback);
	}
	
	// Camera callbacks
	//-------------------------------------------
	private Camera.PictureCallback pictureCallback =new Camera.PictureCallback(){
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			Log.d(LOGTAG,"onPictureTaken called");
			try{
				SDcard.savePreferencedFile(cameraActivity, data);
				cameraActivity.doAction();
				Log.d(LOGTAG,"data2sd called");
			}catch(Exception e){
				Log.e(LOGTAG,"data2sd false:"+e.toString());
			}
		}
	};
	
	private Camera.AutoFocusCallback autoFocusCallback=new Camera.AutoFocusCallback() {
		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			// TODO Auto-generated method stub
			camera.autoFocus(null);
			camera.takePicture(shutterListener, null, pictureCallback);
		}
	};
	
	private Camera.AutoFocusCallback autoFocusNullCallback=new Camera.AutoFocusCallback() {
		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			// TODO Auto-generated method stub
			camera.autoFocus(null);
			//camera.takePicture(shutterListener, null, pictureCallback);
		}
	};
	
	private Camera.ShutterCallback shutterListener=new Camera.ShutterCallback(){
		public void onShutter(){
			Log.d(LOGTAG,"shutted");
		}
	};
	
	// Utility
	//----------------------------------------------
	private Size getOptimalPreviewSize(List<Size> sizes, int w, int h){
		final double ASPECT_TOLERANGE = 0.05;
		double targetRatio = (double)w/h;
		if(sizes==null)return null;
		Size optimalSize=null;
		double minDiff= Double.MAX_VALUE;
		int targetHeight=h;
		// try to find an size match aspect ratio and size
		for(Size size : sizes){
			double ratio = (double) size.width/size.height;
			if( Math.abs(ratio-targetRatio) > ASPECT_TOLERANGE )continue;
			if( Math.abs(size.height - targetHeight) < minDiff){
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}
		// Cannot find the one match the aspect ratio, ignore the requirement
		if(optimalSize == null){
			minDiff = Double.MAX_VALUE;
			for(Size size : sizes){
				if( Math.abs(size.height - targetHeight) < minDiff){
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}
		return optimalSize;
	}
	
	private void setGeoParams(Camera.Parameters params){
		Log.d(LOGTAG, "GeoParam set");
		params.remove("gps-latitude");
		params.remove("gps-longitude");
		params.remove("gps-altitude");
		params.remove("gps-timestamp");
		params.remove("gps-processing-method");
		
		String lat = String.valueOf(GPSController.getLatitude());
		String lon = String.valueOf(GPSController.getLongitude());
		String alt = String.valueOf(GPSController.getAltitude());
		String time = String.valueOf(GPSController.getTimestamp());
		String provider = String.valueOf(GPSController.getProvider());
		
		Log.d(LOGTAG,"Geo lat:"+lat);
		Log.d(LOGTAG,"Geo lon:"+lon);
		Log.d(LOGTAG,"Geo alt:"+alt);
		Log.d(LOGTAG,"Geo time:"+time);
		Log.d(LOGTAG,"Geo provider:"+provider);
		
		params.set("gps-latitude", lat);
		params.set("gps-longitude", lon);
		params.set("gps-altitude", alt);
		params.set("gps-timestamp", time);
		params.set("gps-processing-method", provider);
	}
	
	private void setJpegQuality(Camera.Parameters params){
		PreferenceController control = new PreferenceController(cameraActivity);
		int q = control.getInt("camera_quality");
		if( q == 0 ){return ;}
		String quality = String.valueOf(q);
		Log.d(LOGTAG,"camera quality"+quality);
		params.remove("jpeg-quality");
		params.set("jpeg-quality", quality);
		
	}
	
	private void setPictureSize(Camera.Parameters params, int w, int h){
		List<Size> sizes = Reflect.getSupportedPictureSizes(params);
		String[] picturesize = preferenceController.getString("camera_picturesize").split(" ");
		Log.d(LOGTAG,"picture size");
		if(sizes == null || picturesize.length != 2){
			Log.d(LOGTAG,"picture size is null");
			return;
		}
		
		int pwidth = Integer.valueOf(picturesize[0]);
		int pheight = Integer.valueOf(picturesize[1]);
		Log.d(LOGTAG, "picture size : " + pwidth + "," + pheight );
		for( int i=0; i<sizes.size(); i++ ){
			Size s=sizes.get(i);
			if( ( s.width == pwidth ) &&
			    ( s.height == pheight ) ){
				params.setPictureSize(s.width, s.height);
				break;
			}
			Log.d(LOGTAG,"["+i+"]:"+s.width+","+s.height);
		}
	}
	
	
}
