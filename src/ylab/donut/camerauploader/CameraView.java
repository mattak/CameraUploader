package ylab.donut.camerauploader;
/*
 * Camera View
 * 
 * Description:
 * 	Camera Surface View
 * Last modified:
 * 	10/12/10
 * */

import android.app.*;
import android.content.*;
import android.view.*;
import android.widget.Toast;
import android.hardware.Camera;
import android.hardware.Camera.Size;

import android.preference.PreferenceManager;
import android.util.*;
import java.io.FileOutputStream;
import java.util.List;

// Camera Control 
public class CameraView extends SurfaceView
	implements SurfaceHolder.Callback{
	private SurfaceHolder holder;
	private Camera camera;
	private final static String LOGTAG ="CameraView";
	
	public CameraView(Context context){
		super(context);
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
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this.getContext());
			Boolean b=pref.getBoolean("withshot",false);
			String withshot=null;
			if( b ){
				withshot="true";
			}else{
				withshot="false";
			}
			Toast.makeText(this.getContext(), "takepicture:"+withshot, Toast.LENGTH_LONG).show();
			//camera.stopPreview();
			Log.d(LOGTAG,"stopPreview before takepicture");
			
			
			takePicture();
			Log.d(LOGTAG,"takedpicture"+camera);
			
			Camera.Parameters params=camera.getParameters();
			camera.setParameters(params);
			
			//waitMillis(1000);
			
			camera.startPreview();
			Log.d(LOGTAG,"restart startPreview after takepicture");
		}
		return true;
	}
	
	
	// Take Picture 
	//-------------------------------------------
	public void takePicture(){
		// Camera Screen Shot
		
		camera.takePicture(ShutterListener,null,new Camera.PictureCallback() {
			@Override
			public void onPictureTaken(byte[] data, Camera camera) {
				// TODO Auto-generated method stub
				Log.d(LOGTAG,"onPictureTakencalled");
				try{
					data2sd(getContext(),data,"test.jpg");
					Log.d(LOGTAG,"data2sd called");
				}catch(Exception e){
					Log.e(LOGTAG,"data2sd false"+e.toString());
				}
			}
		});
	}
	
	// Byte data -> SD card
	private static void data2sd(Context context, 
			byte[] w,String fileName) throws Exception{
		// data is stored on SD card
		FileOutputStream fos=null;
		try{
			fos=new FileOutputStream("/sdcard/"+fileName);
			fos.write(w);
			fos.close();
		}catch(Exception e){
			if (fos!=null)fos.close();
			throw e;
		}
	}
	
	private Camera.ShutterCallback ShutterListener=new Camera.ShutterCallback(){
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
	private void waitMillis(long mili){
		long start=java.lang.System.currentTimeMillis();
		long end=start;
		while((end-start)>mili){
			end=java.lang.System.currentTimeMillis();
		}
	}
}
