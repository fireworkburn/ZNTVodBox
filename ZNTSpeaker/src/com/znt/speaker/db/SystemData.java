package com.znt.speaker.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.znt.speaker.entity.Constant;
import com.znt.speaker.util.FileUtils;

import android.app.Activity;
import android.os.Environment;
import android.text.TextUtils;

public class SystemData 
{

	private final static String filePath = Environment.getExternalStorageDirectory() + Constant.WORK_DIR + "/data/system.text";
	
	private static void setSystemData(Activity activity, String appId, String dbVersion)
	{
		JSONObject json = new JSONObject();
		try 
		{
			json.put("SPEAKER_PID", appId);
			json.put("DB_VERSION", dbVersion);
			String content = json.toString();
			
			File file = new File(filePath);
			if(file.exists())
				file.delete();
			FileUtils.writeDataToFile(file.getAbsolutePath(), content);
		} 
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/*public static void checkDbversion(Activity activity)
	{
		File file = new File(filePath);
		if(file.exists())
		{
			FileInputStream fis = FileUtils.readDataFromFile(file.getAbsolutePath());
			byte[] buffer = new byte[(int) file.length()];
			try
			{
				fis.read(buffer);
				String content = new String(buffer);
				if(fis != null)
					fis.close();
				JSONObject json = new JSONObject(content);
				
				String dbVersion = json.getString("DB_VERSION");
				if(!TextUtils.isEmpty(dbVersion))
				{
					int dbV = Integer.parseInt(dbVersion);
					if(dbV > 0 && dbV < Constant.DB_VERSION)
					{
						//本地保存的数据库版本比当前的低，删除本地数据库
						DBManager.newInstance(activity).deleteDbFile();
						//DBManager.newInstance(activity).openDatabase();
					}
				}
			} 
			catch (Exception e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		setSystemData(activity, android.os.Process.myPid() + "", Constant.DB_VERSION + "");
	}*/
	
}
