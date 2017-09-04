/*  
* @Project: ZNTDianGe 
* @User: Administrator 
* @Description: 家庭音乐
* @Author： yan.yu
* @Company：http://www.zhunit.com/
* @Date 2016-5-2 下午10:29:16 
* @Version V1.1   
*/ 

package com.znt.diange.service; 

import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;

import com.znt.diange.entity.Constant;
import com.znt.diange.entity.LocalDataEntity;
import com.znt.diange.factory.HttpFactory;
import com.znt.diange.http.HttpMsg;
import com.znt.diange.mina.client.MinaClient;
import com.znt.diange.mina.cmd.DeviceInfor;
import com.znt.diange.timer.SearchDeviceTimer;
import com.znt.diange.utils.SystemUtils;

/** 
 * @ClassName: DeviceCheckService 
 * @Description: TODO
 * @author yan.yu 
 * @date 2016-5-2 下午10:29:16  
 */
public class DeviceCheckService extends Service 
{

	private HttpFactory httpFactory = null;
	private SearchDeviceTimer searchDeviceTimer = null;
	private boolean isRunning = false;
	
	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == 0)
			{
				if(searchDeviceTimer.isOver())
				{
					searchDeviceTimer.stopTimer();
				}
				else
				{
					startGetNearDevice();
				}
			}
			if(msg.what == HttpMsg.GET_NEAR_BY_SPEAKER_START)
			{
				isRunning = true;
			}
			else if(msg.what == HttpMsg.GET_NEAR_BY_SPEAKER_SUCCESS)
			{
				List<DeviceInfor> tempList = (List<DeviceInfor>)msg.obj;
				
				if(tempList.size() == 0)
				{
					
				}
				else
				{
					checkDevice(tempList);
				}
				isRunning = false;
			}
			else if(msg.what == HttpMsg.GET_NEAR_BY_SPEAKER_FAIL)
			{
				isRunning = false;
				
			}
			/*else if(msg.what == OnConnectHandler.ON_NETWORK_RECONNECTED_SUCCESS)
			{
				
			}
			else if(msg.what == OnConnectHandler.ON_NETWORK_RECONNETC_FAIL)
			{
				//boolean isDeviceRemove = (Boolean)msg.obj;
				
			}*/
		};
	};
	
	/**
	*callbacks
	*/
	@Override
	public IBinder onBind(Intent arg0) 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() 
	{
		httpFactory = new HttpFactory(this, handler);
		searchDeviceTimer = new SearchDeviceTimer(this);
		searchDeviceTimer.setHandler(handler, 0);
		searchDeviceTimer.setTimeInterval(10 * 1000);
		searchDeviceTimer.setMaxTime(3);
		super.onCreate();		
	}
	
	private void checkDevice(List<DeviceInfor> devices)
	{
		int size = devices.size();
		String localId = LocalDataEntity.newInstance(this).getDeviceId();
		String curWifiMac = SystemUtils.getWifiBSsid(this);
		if(TextUtils.isEmpty(curWifiMac))
		{
			stopCheckDevice();
			return;
		}
		for(int i=0;i<size;i++)
		{
			DeviceInfor infor = devices.get(i);
			if(curWifiMac.equals(infor.getWifiMac()))
			{
				if(TextUtils.isEmpty(localId))
				{
					connectDevice(infor);
					stopCheckDevice();
				}
				else if(infor.getId().equals(localId))
				{
					connectDevice(infor);
					stopCheckDevice();
				}
			}
		}
	}
	
	private void connectDevice(DeviceInfor infor)
	{
		LocalDataEntity.newInstance(this).setDeviceInfor(infor);
		if(!MinaClient.getInstance().isConnected())
			MinaClient.getInstance().startClient();
	}
	
	private void startGetNearDevice()
	{
		if(isRunning)
			return;
		stopGetSpeakers();
		httpFactory.getNearBySpeakers();
	}
	private void stopGetSpeakers()
	{
		if(httpFactory != null)
			httpFactory.stopHttp();
	}
	
	private void startCheckDevice()
	{
		if(!isWifiApConnect())
		{
			stopCheckDevice();
			searchDeviceTimer.startTimer();
		}
	}
	private void stopCheckDevice()
	{
		searchDeviceTimer.stopTimer();
	}
	
	@Override
	public void onDestroy() 
	{
		stopGetSpeakers();
		stopCheckDevice();
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		startCheckDevice();
		return super.onStartCommand(intent, flags, startId);
	}
	
	public boolean isWifiApConnect()
	{
		String curSsid = SystemUtils.getConnectWifiSsid(this);
		if(TextUtils.isEmpty(curSsid))
			return false;
		return curSsid.endsWith(Constant.UUID_TAG);
	}
}
 
