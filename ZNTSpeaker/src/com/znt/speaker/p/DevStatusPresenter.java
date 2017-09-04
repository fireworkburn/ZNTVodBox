package com.znt.speaker.p;

import java.util.List;

import com.znt.diange.mina.entity.SongInfor;
import com.znt.speaker.db.DBManager;
import com.znt.speaker.entity.LocalDataEntity;
import com.znt.speaker.factory.UIManager;
import com.znt.speaker.service.DevStatusService;
import com.znt.speaker.service.DevStatusService.OnDevCheckListener;
import com.znt.speaker.util.SystemUtils;
import com.znt.speaker.v.IDevStatusView;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

public class DevStatusPresenter 
{
	private Activity activity = null;
	private IDevStatusView iDevStatusView = null;
	private DevStatusService devStatusService = null;
	private UIManager mUIManager = null;
	
	private volatile boolean isCheckDelayRunning = false;
	private volatile boolean isDevSRegistered = false;
	private int checkDelayCount = 0;
	private int checkDevStatusFailCount = 0;
	private final int MAX_FAIL_COUNT = 3;
	public DevStatusPresenter(Activity activity, IDevStatusView iDevStatusView, UIManager mUIManager)
	{
		this.activity = activity;
		this.iDevStatusView = iDevStatusView;
		this.mUIManager = mUIManager;
		devStatusService = new DevStatusService();
	}
	
	public void startDevStatusService()
	{
        Intent intent = new Intent(DevStatusService.ACTION);  
        activity.bindService(intent, conn, Context.BIND_AUTO_CREATE);  
        isDevSRegistered = true;
	}
	public void stopDevStatusService()
	{
		if(isDevSRegistered)
			activity.unbindService(conn);  
	}
	
	public void setFailCount()
	{
		checkDevStatusFailCount ++;//每次获取状态失败，都更新计算器
		if(checkDevStatusFailCount >= MAX_FAIL_COUNT)
			checkDevStatusFailCount = MAX_FAIL_COUNT;
	}
	public void resetFailCount()
	{
		if(checkDevStatusFailCount > 0)
			checkDevStatusFailCount = 0;
	}
	public boolean isOffLine()
	{
		return checkDevStatusFailCount >= MAX_FAIL_COUNT;
	}
	
	ServiceConnection conn = new ServiceConnection() 
	{  
        @Override  
        public void onServiceDisconnected(ComponentName name) 
        {  
        	//iDevStatusView.onServiceDisconnected(name);
        }  
          
        @Override  
        public void onServiceConnected(ComponentName name, IBinder service) 
        {  
        	devStatusService = ((DevStatusService.DevStatusBinder)service).getService();  
              
        	devStatusService.setOnDevCheckListener(new OnDevCheckListener() 
        	{
				@Override
				public void onCheckStart() 
				{
					// TODO Auto-generated method stub
					
					if(isCheckDelayRunning)
						return;
					isCheckDelayRunning = true;
					checkDevStatusProcess();
					
					if(isOffLine())
					{
						getLocalPlayListAndPlay();
					}
					
					isCheckDelayRunning = false;
				}
				@Override
				public void onCheckLocalStart() 
				{
					
				}
			});
        }  
    }; 
    
	private void checkDevStatusProcess()
	{
		if(SystemUtils.isNetConnected(activity))
		{
			iDevStatusView.onGetDevStatus();
		}
		else
		{
			//检查网络连接状态
		}
		
		//检查歌曲是否卡主了
		checkDelayCount ++;
		if(checkDelayCount >= 3)
		{
			checkDelayCount = 0;
			iDevStatusView.onCheckDelay();
		}
	}
	
	private boolean isGetLocalMusicRunning = false;
	private void getLocalPlayListAndPlay()
	{
		if(isGetLocalMusicRunning)
			return;
		isGetLocalMusicRunning = true;
		if(mUIManager.getCurTime() <= 0 )
		{
			long localTime = DBManager.newInstance(activity).getFirstPlanTime();
			if(localTime > 0)
			{
				LocalDataEntity.newInstance(activity).setPlanTime("");
				mUIManager.setCurTime((localTime + 10 * 60) + "");
			}
		}
		
		List<SongInfor> tempSongList = DBManager.newInstance(activity).getCurPlanMusics(mUIManager.getCurTime());
		iDevStatusView.getLocalPlayListAndPlay(tempSongList);
		isGetLocalMusicRunning = false;
		
	}
}
