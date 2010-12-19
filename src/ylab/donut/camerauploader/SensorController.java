package ylab.donut.camerauploader;

import java.util.List;

import android.content.*;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class SensorController implements SensorEventListener{
	private static SensorController instance = new SensorController();
	private SensorManager sensorManager;
	private Sensor accelerometer;
	private Sensor orientation;
	
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
					accelerometer,SensorManager.SENSOR_DELAY_FASTEST);
		}
		if(orientation!=null){
			sensorManager.registerListener(this,
					orientation, SensorManager.SENSOR_DELAY_FASTEST);
		}
	}
	
	public void stop(){
		sensorManager.unregisterListener(this);
	}
	
	public void onSensorChanged(SensorEvent event){
		for(int i=0; i<3; i++){
			int w=(int)(10*event.values[i]);
			event.values[i]=(float)(w/10.0f);
		}
		
		if(event.sensor==accelerometer){
			
		}
	}
	
	public void onAccuracyChanged(Sensor sensor, int accuracy){}
}
