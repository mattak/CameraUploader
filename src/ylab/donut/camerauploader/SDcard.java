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
	
	public static void saveByteFile(String filepath, byte[] w)throws IOException{
		FileOutputStream fos=null;
		try{
			fos=new FileOutputStream(filepath);
			fos.write(w);
			fos.close();
		}catch(IOException e){
			if (fos!=null)fos.close();
			throw e;
		}
	}
	
	
	public static void savePreferencedFile(Context context,byte[] data)throws IOException{
		PreferenceController control = new PreferenceController(context);
		String filename=control.getSaveFilePath();
		saveByteFile(filename,data);
	}
	
}
