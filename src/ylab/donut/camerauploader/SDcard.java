package ylab.donut.camerauploader;

import java.io.*;

import android.preference.PreferenceManager;
import android.content.*;
import android.util.*;

public class SDcard{
	private final static String LOGTAG="SDcard";
	private final static String sdroot="/sdcard/";
	public SDcard(){}
	public void write(String name, String content){
		try{
			FileOutputStream fos = new FileOutputStream(sdroot+name);
			fos.write(content.getBytes());
			fos.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	/*
	 * String Writer
	 * */
	public static void writeString(String filename, String contents) throws IOException{
		String path="/sdcard/"+filename;
		File file=new File(path);
		FileOutputStream fos = new FileOutputStream(file,true);
		OutputStreamWriter osw = new OutputStreamWriter(fos,"UTF-8");
		osw.write(contents);
		osw.close();
		fos.close();
	}
	
	/*
	 * String Reader
	 * */
	public static String readString(String filename) throws IOException{
		String path="/sdcard/"+filename;
		StringBuffer buf=new StringBuffer();
		String str;
		FileReader is = new FileReader(path);
		BufferedReader br = new BufferedReader(is);
		while((str=br.readLine())!=null){
			buf.append(str);
		}
		br.close();
		is.close();
		return buf.toString();
	}
	
	public static void savePreferenceImageFile(Context context, byte[] data){
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		String filename=sp.getString("filename", "");
		saveByteFile(filename,data);
	}
	
	public static void saveByteFile(String filename, byte[] data){
		
		try{
			FileOutputStream fos= new FileOutputStream(new File(filename));
		}catch(IOException e){
			Log.d(LOGTAG,"save byte file ioexception");
		}
	}
}
