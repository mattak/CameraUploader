package ylab.donut.camerauploader;

import java.util.List;
import java.util.Observer;
import java.util.Observable;

import android.content.*;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class SensorController extends Observable implements SensorEventListener{
	private final static String LOGTAG = "Sensor";
	private static SensorController instance = new SensorController();
	private static SensorManager sensorManager;
	private static Sensor accelerometer;
	private static Sensor orientation;
	private static float[] aVector=new float[4];
	private static float[] oVector=new float[4];
	
	private SensorController(){}
	public static SensorController getInstance(){
		return instance;
	}
	
	public void init(Context context){
		sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
		List<Sensor> list;
		list = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
		if(list.size()>0)accelerometer=list.get(0);
		list = sensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
		if(list.size()>0)orientation=list.get(0);
	}
	
	public void start(){
		if(accelerometer!=null){
			sensorManager.registerListener(this,
					accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
		}
		if(orientation!=null){
			sensorManager.registerListener(this,
					orientation, SensorManager.SENSOR_DELAY_NORMAL);
		}
	}
	
	public void stop(){
		sensorManager.unregisterListener(this);
	}
	
	public void onSensorChanged(SensorEvent event){
		//
		//for(int i=0; i<3; i++){
		//	int w=(int)(10*event.values[i]);
		//	event.values[i]=(float)(w/10.0f);
		//}
		
		if(event.sensor==accelerometer){
			aVector[0] = event.values[0];
			aVector[1] = event.values[1];
			aVector[2] = event.values[2];
		}else if(event.sensor==orientation){
			oVector[0] = event.values[0];
			oVector[1] = event.values[1];
			oVector[2] = event.values[2];
		}
		this.notifyObservers();
		Log.d(LOGTAG,"--sensorchanged--");
	}
	
	public void onAccuracyChanged(Sensor sensor, int accuracy){
		if( sensor == accelerometer ){
			aVector[3] = accuracy;
		}else if( sensor == orientation ){
			oVector[3] = accuracy;
		}
	}
	
	public static float[] getAccelerometor(){
		return aVector;
	}
	
	public static float[] getOrientation(){
		return oVector;
	}	
	
	// Observer
	//--------------------------------
	public void addObserver(Observer observer){
		super.addObserver(observer);
	}
	public void removeObserver(Observer observer){
		super.deleteObserver(observer);
	}
	public void notifyObservers(){
		setChanged();
		super.notifyObservers();
		clearChanged();
	}
}
