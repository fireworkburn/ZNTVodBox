package com.znt.speaker.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class DevStatusService extends Service
{

	private boolean isStop = false;
	private OnDevCheckListener listener = null;
	
	public static final String ACTION = "com.znt.speaker.DEV_STATUS";
	
	private final int CHECK_TIME_MAX = 8;
	
	private int checkTime = 0;
	private int checkTimeMax = CHECK_TIME_MAX;
	
	private void startCheckDevStatus()
	{
		isStop = false;
		new Thread(new CheckDevStatusTask()).start();
	}
	private void stopCheckDevStatus()
	{
		isStop = true;
	}
	
	public void setOnDevCheckListener(OnDevCheckListener listener)
	{
		this.listener = listener;
	}
	public interface OnDevCheckListener
	{
		public void onCheckStart();
		public void onCheckLocalStart();
	}
	
	private class CheckDevStatusTask implements Runnable
	{
		@Override
		public void run() 
		{
			// TODO Auto-generated method stub
			while(true)
			{
				if(isStop)
					break;
				if(listener != null)
				{
					if(checkTime >= checkTimeMax)
					{
						checkTime = 0;
						listener.onCheckStart();
					}
					else
						listener.onCheckLocalStart();
				}
					
				try 
				{
					Thread.sleep(1000);
				} 
				catch (InterruptedException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				checkTime ++;
			}
		}
	}
	
	public class DevStatusBinder extends Binder
	{  
        /** 
         * 鑾峰彇褰撳墠Service鐨勫疄渚� 
         * @return 
         */  
        public DevStatusService getService()
        {  
            return DevStatusService.this;  
        }  
    }  
	
	@Override
	public void onCreate()
	{
		// TODO Auto-generated method stub
		super.onCreate();
	}
	@Override
	public void onStart(Intent intent, int startId) 
	{
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}
	@Override
	public IBinder onBind(Intent arg0) 
	{
		// TODO Auto-generated method stub
		startCheckDevStatus();
		return new DevStatusBinder(); 
	}
	@Override
	public void onDestroy() 
	{
		stopCheckDevStatus();
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
