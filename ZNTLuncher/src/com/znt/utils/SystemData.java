package com.znt.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Environment;
import android.text.TextUtils;

public class SystemData 
{
	private final static String filePath = Environment.getExternalStorageDirectory() + Constant.WORK_DIR + "/data/system.text";
	
	public static int getSpeakerPid(Activity activity)
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
				JSONObject json = new JSONObject(content);
				
				String pidStr = json.getString("SPEAKER_PID");
				if(!TextUtils.isEmpty(pidStr))
				{
					return Integer.parseInt(pidStr);
				}
			} 
			catch (Exception e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try 
			{
				if(fis != null)	
					fis.close();
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		return -1;
	}
}
