package com.znt.speaker.music;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.znt.speaker.util.CommonLog;
import com.znt.speaker.util.LogFactory;

public class NetUtils {

private static final CommonLog log = LogFactory.createLog();
	
	public static Drawable requestDrawableByUri(String uri){
		if (uri == null || uri.length() == 0){
			return null;
		}
		
		Drawable drawable = null; 
		int index = 0;
		while(true){
			if (index >= 3){
				break;
			}
			drawable = getDrawableFromUri(uri);
			if (drawable != null){
				break;
			}
			index++;
		}
			
		return drawable;
	}
	
	public static Drawable getDrawableFromUri(String uri){
		if (uri == null || uri.length() < 1){
			return null;
		}
		Drawable drawable = null;
		try {
			URL url = new URL(uri);
			HttpURLConnection  conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("GET");
			InputStream input = conn.getInputStream();
			if (conn.getResponseCode() != 200){
			    log.e("getDrawableFromUri.getResponseCode() = " + conn.getResponseCode() + "\n" +
			    		"uri :" + uri + "is invalid!!!");
			    input.close();
				return null;
			}
			drawable = Drawable.createFromStream(input, "src"); 
			input.close();
		} catch (Exception e) {
			e.printStackTrace();
		//	log.e("getDrawableFromUri catch exception!!!e = " + e.getMessage());
		}
		
		return drawable;
	}
	
	public static boolean isWifiConnected(Context context) 
    {  
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);  
        if (cm != null)
        {  
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();  
            if (networkInfo != null 
                    && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) 
            {  
                return true;  
            }  
        }  
        return false;  
    }  
}
