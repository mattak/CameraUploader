package ylab.donut.camerauploader;

import android.location.*;
import android.util.Log;
import android.os.Bundle;

public class GPSController implements LocationListener{
	private final static String LOGTAG="GPSController";
	private double lat;
	private double lon;
	private double alt;
	private double speed;
	private float accuracy;
	private long timestamp;
	
	public void onLocationChanged(Location location){
		Log.d(LOGTAG,"location changed");
		alt = location.getAltitude();
		lat = location.getLatitude();
		lon = location.getLongitude();
		timestamp = location.getTime();
		speed = location.getSpeed();
		accuracy = location.getAccuracy();
	}
	
	public void onProviderDisabled(String provider){
		Log.d(LOGTAG,"provider disabled");
	}
	public void onProviderEnabled(String provider){
		
	}
	public void onStatusChanged(String provider,int status,Bundle extras){
		
	}
	
	// public Get Method
	//-----------------------------------
	public double getLatitude(){
		return lat;
	}
	public double getLongitude(){
		return lon;
	}
	public double getAltitude(){
		return alt;
	}
	public double getSpeed(){
		return speed;
	}
	public float getAccuracy(){
		return accuracy;
	}
	public long getTimestamp(){
		return timestamp;
	}
}
