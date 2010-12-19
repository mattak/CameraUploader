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
	private boolean apihigh=true;
	private boolean touchhold=true;
	private final static String LOGTAG ="CameraView";
	
	public CameraView(CameraActivity context){
		super(context);
		cameraActivity = context;
		
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
		
		//
		PreferenceController control=new PreferenceController(cameraActivity);
		if(control.getBoolean("camera_flash")){
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
			if( touchhold ){
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
			}
		}
		return true;
	}
	
	// Take Picture 
	//-------------------------------------------
	public void takePicture(){
		// Camera Screen Shot
		PreferenceController control=new PreferenceController(cameraActivity);
		if(!control.getBoolean("camera_autofocus")){
			camera.takePicture(shutterListener,null, pictureCallback);
		}else{
			camera.autoFocus(autoFocusCallback);
		}
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
		
		params.remove("gps-latitude");
		params.remove("gps-longitude");
		params.remove("gps-altitude");
		
		params.set("gps-latitude", "35.0");
		params.set("gps-longitude", "137.0");
		params.set("gps-altitude", "50");
		params.set("jpeg-quality", "100");
		params.set("gps-timestamp", "2010/12/15");
	}
}
