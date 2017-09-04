package com.znt.vodbox.utils;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class ImageUtil {
	public static List<String> getImageURL(String json) {
		if (json == null || "".equals(json)) {
			return null;
		}

		return null;
	}

	// 获取文件夹的字节大小
	public static long getFileSize(File f) throws Exception {
		long size = 0;
		File flist[] = f.listFiles();
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				size = size + getFileSize(flist[i]);
			} else {
				size = size + flist[i].length();
			}
		}
		return size;
	}

	public static String sendGETRequest(String path) throws Exception {
		HttpURLConnection conn = (HttpURLConnection) new URL(path)
				.openConnection();
		conn.setConnectTimeout(5000);
		conn.setRequestMethod("GET");
		// if (conn.getResponseCode() == 200) {
		InputStream inStream = conn.getInputStream();
		byte[] data = StreamTool.read(inStream);
		String result = new String(data);
		return result;
		// }else{
		// return null;
		// }

	}

	public static List<String> resolveImageData(String json) throws Exception 
	{
		List<String> list = new ArrayList<String>();
		JSONObject object = new JSONObject(json);
		JSONArray data = object.getJSONArray("data");
		int temp = data.length();
		for(int i=0;i<3;i++)
		{
			if(i < temp)
			{
				String objURL = data.getJSONObject(i).getString("objURL");
				list.add(objURL);
			}
		}
		
		return list;
	}

	public static void deleteFile(File f) {
		if(f.exists()&&f.isDirectory()){
			File[] files = f.listFiles();
			for(File file:files){
				file.delete();
			}
		}
		
	}

	public static String getSystemTime(){
		long time = System.currentTimeMillis();
		Date date = new Date(time);
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMddhhmmss");
		return sdf.format(date)+time%1000;
	}
}