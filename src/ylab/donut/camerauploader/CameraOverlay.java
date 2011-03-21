package ylab.donut.camerauploader;

import android.util.Log;
import android.view.*;
import android.content.*;
import android.content.res.*;
import android.graphics.*;
import java.util.*;

public class CameraOverlay extends View implements Observer{
	private final static String LOGTAG ="CameraOverlay";
	private Paint paint=new Paint();
	private Paint textPaint;
	private Bitmap shotIcon,focusIcon,menuIcon;
	private Rect srcRect;
	private Rect dstRect1,dstRect2,dstRect3;
	private CameraView cameraView;
	public CameraOverlay(Context context,CameraView cameraView){
		super(context);
		setFocusable(true);
		this.cameraView = cameraView;
		
		textPaint=new Paint();
		textPaint.setColor(Color.GRAY);
		textPaint.setTextSize(22);
		
		SensorController sensorController = SensorController.getInstance();
		sensorController.addObserver(this);
		GPSController gpsController = GPSController.getInstance();
		gpsController.addObserver(this);
		
		Resources res = this.getContext().getResources();
		shotIcon = BitmapFactory.decodeResource(res,R.drawable.shot1);
		focusIcon = BitmapFactory.decodeResource(res,R.drawable.focus1);
		srcRect = new Rect(0,0,shotIcon.getWidth(),shotIcon.getHeight());
	}
	/*protected void onSizeChanged(int w, int h, int oldw, int oldh){
		
	}*/
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);
		int viewWidth = canvas.getWidth();
		int viewHeight = canvas.getHeight();
		if(dstRect1==null){
			Log.d(LOGTAG,"w:"+viewWidth+" h:"+viewHeight);
			dstRect1 = new Rect(viewWidth-shotIcon.getWidth(),0,viewWidth,shotIcon.getHeight());
			dstRect2 = new Rect(viewWidth-shotIcon.getWidth(),150,viewWidth,shotIcon.getHeight()+150);
		}
		
		// icons
		canvas.drawBitmap(shotIcon, srcRect, dstRect1, paint);
		canvas.drawBitmap(focusIcon, srcRect, dstRect2, paint);
		//canvas.drawBitmap(menuIcon, srcRect, dstRect3, paint);
		
		// sensors
		/*float []acc = SensorController.getAccelerometor();
		float []ori = SensorController.getOrientation();
		String text=new String("acc[0]:"+acc[0]+"[1]"+acc[1]+"[2]"+acc[2]+"[3]"+acc[3]+","
				+"ori[0]:"+ori[0]+"[1]"+ori[1]+"[2]"+ori[2]);
		canvas.drawText( text, this.getWidth()-500, this.getHeight()-10, textPaint);
		*/
		
		// latlons
		int textsize=22, padding=2;
		double lat = GPSController.getLatitude();
		double lon = GPSController.getLatitude();
		int accuracy = (int)GPSController.getAccuracy();
		
		canvas.drawText("lat: "+lat, 0, viewHeight-textsize*2-padding, textPaint);
		canvas.drawText("lon: "+lon, 0, viewHeight-textsize-padding, textPaint);
		canvas.drawText("R: "+accuracy+"[m]", 0, viewHeight-padding, textPaint);
	}
	
	public void update(Observable o, Object arg){
		Log.d(LOGTAG,"notified");
		this.invalidate();
	}
	
	public boolean onTouchEvent(MotionEvent e){
		int touchX=(int)e.getX();
		int touchY=(int)e.getY();
		int touchAction = e.getAction();
		if( touchAction != MotionEvent.ACTION_UP )return true;
		if( dstRect1.left < touchX && touchX < dstRect1.right ){
			// shot area
			if( dstRect1.top < touchY && touchY < dstRect1.bottom ){
				Log.d(LOGTAG,"cameraoverlay tack picture");
				cameraView.takePicture();
			}else
			// focus area
			if( dstRect2.top < touchY && touchY < dstRect2.bottom ){
				Log.d(LOGTAG,"cameraoverlay focus");
				cameraView.doOnlyFocus();
			}else
			//  restartpreview
			{
				cameraView.restartPreview();
			}
		}else{
			cameraView.restartPreview();
		}
		return true;
	}
}
