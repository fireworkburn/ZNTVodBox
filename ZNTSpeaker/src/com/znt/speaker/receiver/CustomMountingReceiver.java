package com.znt.speaker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.znt.speaker.entity.Constant;

public class CustomMountingReceiver extends BroadcastReceiver 
{
	
	private IMediaChangeListener receiver = null;
	
	@Override
	public void onReceive(Context arg0, Intent arg1) 
	{
		String action = arg1.getAction();
		if(action.equals(Constant.MEDIA_ADD))//插入
		{
			String path = arg1.getDataString();
			if(receiver != null)
			{
				receiver.onMediaChange(true, path);
			}
			/*if(getAndroidOSVersion() >= 19)
				Toast.makeText(arg0, "当前系统版本可能无法访问U盘内容", 0).show();
			else
				Toast.makeText(arg0, "存储设备插入", 0).show();*/
		}
		if(action.equals(Constant.MEDIA_REMOVE))//移除
		{
			if(receiver != null)
			{
				receiver.onMediaChange(false, "");
			}
			//Toast.makeText(arg0, "存储设备移除", 0).show();
		}
	}
	
	public static int getAndroidOSVersion()  
    {  
         int osVersion;  
         try  
         {  
            osVersion = Integer.valueOf(android.os.Build.VERSION.SDK);  
         }  
         catch (NumberFormatException e)  
         {  
            osVersion = 0;  
         }  
           
         return osVersion;  
   }  
	
	public void setReceiverListener(IMediaChangeListener receiver)
	{
		this.receiver = receiver;
	}
	
	public interface IMediaChangeListener
	{
		public void onMediaChange(boolean isAdd, String path);
	}
}
