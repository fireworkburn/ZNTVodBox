package com.znt.diange.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.znt.diange.entity.Constant;
import com.znt.diange.utils.SystemUtils;

public class CustomMountingReceiver extends BroadcastReceiver 
{
	
	private IMediaChangeListener receiver = null;
	
	@Override
	public void onReceive(Context arg0, Intent arg1) 
	{
		String action = arg1.getAction();
		if(action.equals(Constant.MEDIA_ADD))//插入
		{
			if(receiver != null)
			{
				receiver.onMediaChange(true);
			}
			
			if(SystemUtils.getAndroidOSVersion() >= 19)
				Toast.makeText(arg0, "当前系统版本可能无法访问U盘内容", 0).show();
			else
				Toast.makeText(arg0, "存储设备插入", 0).show();
		}
		if(action.equals(Constant.MEDIA_REMOVE))//移除
		{
			if(receiver != null)
			{
				receiver.onMediaChange(false);
			}
			Toast.makeText(arg0, "存储设备移除", 0).show();
		}
	}
	
	public void setReceiverListener(IMediaChangeListener receiver)
	{
		this.receiver = receiver;
	}
	
	public interface IMediaChangeListener
	{
		public void onMediaChange(boolean isAdd);
	}
}
