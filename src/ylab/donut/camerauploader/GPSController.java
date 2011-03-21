package ylab.donut.camerauploader;

import android.content.Context;
import android.location.*;
import android.util.Log;
import android.os.Bundle;
import java.util.*;

public class GPSController extends Observable implements LocationListener{
	private static GPSController instance = new GPSController();
	private final static String LOGTAG="GPSController";
	private static LocationManager locationManager;
	private static double lat;
	private static double lon;
	private static double alt;
	private static double speed;
	private static float accuracy;
	private static long timestamp;
	private static int status;
	private static String provider;
	
	private GPSController(){}
	public static GPSController getInstance(){
		return instance;
	}
	
	public void init(Context context){
		locationManager = (LocationManager)
			context.getSystemService(Context.LOCATION_SERVICE);
	}
	
	public void start(){
		if(locationManager != null){
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 
					0, 0, this);
		}
	}
	
	public void stop(){
		if(locationManager != null){
			locationManager.removeUpdates(this);
		}
	}
	
	public void onLocationChanged(Location location){
		Log.d(LOGTAG,"location changed");
		alt = location.getAltitude();
		lat = location.getLatitude();
		lon = location.getLongitude();
		timestamp = location.getTime();
		speed = location.getSpeed();
		accuracy = location.getAccuracy();
		provider = location.getProvider().toUpperCase();
		this.notifyObservers();
		Log.d(LOGTAG,"--LocationChanged--");
	}
	
	public void onProviderDisabled(String provider){
		Log.d(LOGTAG,"provider disabled");
	}
	
	public void onProviderEnabled(String provider){
	}
	
	public void onStatusChanged(String provider,int changedstatus,Bundle extras){
		switch(changedstatus){
		case LocationProvider.AVAILABLE:
			status = changedstatus;
			break;
		case LocationProvider.OUT_OF_SERVICE:
			status = changedstatus;
			break;
		case LocationProvider.TEMPORARILY_UNAVAILABLE:
			status = changedstatus;
			break;
		}
	}
	
	// public Get Method
	//-----------------------------------
	public static double getLatitude(){
		return lat;
	}
	public static double getLongitude(){
		return lon;
	}
	public static double getAltitude(){
		return alt;
	}
	public static double getSpeed(){
		return speed;
	}
	public static float getAccuracy(){
		return accuracy;
	}
	public static long getTimestamp(){
		return timestamp;
	}
	public static int getStatus(){
		return status;
	}
	public static String getProvider(){
		return provider;
	}
	
	// Observer
	//------------------------------------
	public void addObserver(Observer observer){
		super.addObserver(observer);
	}
	public void removeObserver(Observer observer){
		super.addObserver(observer);
	}
	public void notifyObservers(){
		setChanged();
		super.notifyObservers();
		clearChanged();
	}
}
