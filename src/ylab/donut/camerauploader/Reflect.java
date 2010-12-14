package ylab.donut.camerauploader;
/*
 * Reflect
 * 
 * Description:
 *  Basic Compatibility for api level 5(2.0) upper device.
 *  The Method of camera.getParameters() is fail when api level is greater than 4.
 *  so, we need to prepaire the getSupportedPreviewSizes() function.
 *  this method is supported in api level 5.
 *  then reflection for getSupportedPreviewSizes() is need for api level 4(1.6).
 * */
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.*;
import java.util.*;


public class Reflect{
	private static Method Parameters_getSupportedPreviewSizes;
	private final static String LOGTAG="Reflect";
	static{
		initCompatibility();
	};
	private static void initCompatibility(){
		try{
			Parameters_getSupportedPreviewSizes = Camera.Parameters.class.
				getMethod("getSupportedPreviewSizes",new Class[]{});
		}catch(NoSuchMethodException name){}
	}
	
	@SuppressWarnings("unchecked")
	public static List<Size> getSupportedPreviewSizes(Camera.Parameters p){
		try{
			if( Parameters_getSupportedPreviewSizes != null){
				return (List<Size>)Parameters_getSupportedPreviewSizes.invoke(p);
			}else{
				Log.e(LOGTAG, "getSupportedPreviewSizes is null");
				return null;
			}
		}catch(InvocationTargetException e){
			Log.e(LOGTAG,"InvocationTargetException");
			Throwable cause=e.getCause();
			if(cause instanceof RuntimeException){
				throw(RuntimeException)cause;
			}else if(cause instanceof Error){
				throw (Error)cause;	
			}else{
				throw new RuntimeException(e);
			}
		}catch(IllegalAccessException e){
			Log.e(LOGTAG,"IllegalAccessException");
			return null;
		}
	}
	
}