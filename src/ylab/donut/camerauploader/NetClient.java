package ylab.donut.camerauploader;

import java.io.*;
import java.util.*;
// org apache
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.HttpEntity;
import org.apache.http.entity.*;
import org.apache.http.entity.mime.*;
import org.apache.http.entity.mime.content.*;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
// android
import android.util.Log;

public class NetClient{
	public NetClient(){;}
	
	public static String doGet(String uri, HashMap<String,String> map) throws ClientProtocolException, IOException{
		String fulluri;
		if(null == map){
			fulluri = uri;
		}else{
			fulluri = uri+"?"+parameterToString(map);
		}
		HttpGet method = new HttpGet(fulluri);
		return EntityUtils.toString(DoRequest(method));
	}
	
	public static String doPost(String uri, HashMap<String,String> map)throws ClientProtocolException, IOException{
		HttpPost method=new HttpPost(uri);
		if(null != map){
			StringEntity paramEntity = new StringEntity(parameterToString(map));
			paramEntity.setChunked(false);
			paramEntity.setContentType("application/x-www-form-urlencoded");
			method.setEntity(paramEntity);
		}
		return EntityUtils.toString(DoRequest(method));
	}
	
	public static String doPostFile(String uri, File file)
		throws ClientProtocolException,IOException{
		HttpPost post = new HttpPost(uri);
		MultipartEntity entity= new MultipartEntity();
		post.setEntity(entity);
		FileBody fileBody = new FileBody(file);
		entity.addPart("file", fileBody);
		return EntityUtils.toString(DoRequest(post));
	}
	
	/*
	 * Http Request Executer
	 *  method type: GET/POST
	 *  client type: default (non proxy)
	 * */
	private static HttpEntity DoRequest(HttpUriRequest method) throws ClientProtocolException, IOException{
		HttpClient client = new DefaultHttpClient();
		HttpResponse response = null;
		try{
			Log.d("NetClient","Dorequest Execute");
			response = client.execute(method);
			Log.d("NetClient","Executed");
			int statuscode = response.getStatusLine().getStatusCode();
			// request Pass or failure
			if(statuscode==HttpStatus.SC_OK | statuscode == HttpStatus.SC_CREATED){
				return response.getEntity();
			}else{
				throw new HttpResponseException(statuscode,"Response code is "+Integer.toString(statuscode));
			}
		}catch(RuntimeException e){
			method.abort();
			Log.d("NetClient", e.getMessage());
			throw new RuntimeException(e);
		}
	}
	
	/*
	 * it returns key1=val1&key2=val2&...&keyN=valN
	 * */
	private static String parameterToString(HashMap<String, String> map){
		Iterator<String> it = map.keySet().iterator();
		String keys="";
		Object o;
		if( it.hasNext() ){
			o = it.next();
			keys = keys + o + "=" + map.get(o);
			while( it.hasNext() ){
				o = it.next();
				keys = keys + "&" + o + "=" + map.get(o);
			}
		}
		return keys;
	}
}
