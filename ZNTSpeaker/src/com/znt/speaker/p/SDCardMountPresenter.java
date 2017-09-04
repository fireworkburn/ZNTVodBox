package com.znt.speaker.p;

import com.znt.speaker.receiver.CustomMountingReceiver;
import com.znt.speaker.receiver.CustomMountingReceiver.IMediaChangeListener;
import com.znt.speaker.v.ISDCardMountView;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class SDCardMountPresenter 
{
	private Activity activity = null;
	private ISDCardMountView iSDCardMountView = null;
	
	private CustomMountingReceiver customMountingReceiver = null;
	
	private boolean isSDSRegistered = false;
	public SDCardMountPresenter(Activity activity, ISDCardMountView iSDCardMountView)
	{
		this.activity = activity;
		this.iSDCardMountView = iSDCardMountView;
	}
	
	public void registerStorageMount()
	{
		if(!isSDSRegistered)
		{
			customMountingReceiver = new CustomMountingReceiver();
			IntentFilter filter = new IntentFilter();
	        filter.addAction(Intent.ACTION_MEDIA_EJECT);
	        filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
	        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
	        filter.addDataScheme("file");
	        activity.registerReceiver(customMountingReceiver, filter);
	        customMountingReceiver.setReceiverListener(new IMediaChangeListener() 
	        {
				@Override
				public void onMediaChange(boolean isAdd, String path) 
				{
					// TODO Auto-generated method stub
					iSDCardMountView.onMediaChange(isAdd, path);
				}
			});
	        isSDSRegistered = true;
		}
	}
	
	public void unregisterStorageReceiver()
	{
		try 
		{
			if(customMountingReceiver != null && isSDSRegistered)
				activity.unregisterReceiver(customMountingReceiver);
		} 
		catch (Exception e) 
		{
			// TODO: handle exception
			Log.e("", "unregisterStorageReceiver error-->" + e.getMessage());
		}
		
	}
}
