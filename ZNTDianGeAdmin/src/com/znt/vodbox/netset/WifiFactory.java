
package com.znt.vodbox.netset; 

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.znt.vodbox.utils.MyLog;

/** 
 * @ClassName: WifiManager 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-1-26 下午1:36:41  
 */
public class WifiFactory
{
	private Context context = null;
	private WifiAdmin mWifiAdmin = null;
	
	private String wifiName = "";
	private String wifiPwd = "";
	
	private static WifiFactory instance = null;
	
	private OnConnectResultListener onConnectResult = null;
	private Handler mHandler = new Handler();
	
	private int retryTimes = 0;
	
	public WifiFactory(Context context)
	{
		this.context = context;
		
		mWifiAdmin = new WifiAdmin(context)
		{
			@Override
			public void onNotifyWifiConnected()
			{
				if(onConnectResult != null)
					onConnectResult.onConnectResult(true);
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onNotifyWifiConnectFailed()
			{
				// TODO Auto-generated method stub
				if(onConnectResult != null)
					onConnectResult.onConnectResult(false);
			}
			
			@Override
			public void myUnregisterReceiver(BroadcastReceiver receiver)
			{
				// TODO Auto-generated method stub
				//mContext.unregisterReceiver(receiver);
				MyLog.e("myUnregisterReceiver");
			}
			
			@Override
			public Intent myRegisterReceiver(BroadcastReceiver receiver,
					IntentFilter filter)
			{
				// TODO Auto-generated method stub
				MyLog.e("myRegisterReceiver");
				return null;
			}
		};
	}
	
	public static WifiFactory newInstance(Context context)
	{
		if(instance == null)
			instance = new WifiFactory(context);
		return instance;
	}
	
	public void setOnConnectResultListener(OnConnectResultListener onConnectResult)
	{
		this.onConnectResult = onConnectResult;
	}
	
	public WifiAdmin getWifiAdmin()
	{
		return mWifiAdmin;
	}
	
	public void connectWifi(String name, String pwd)
	{
		if(TextUtils.isEmpty(pwd))
			pwd = "";
		pwd = pwd.replace(" ", "");
		
		wifiName = name;
		wifiPwd = pwd;
		
		Log.e("", "wifiName-->"+wifiName);
		Log.e("", "wifiPwd-->"+wifiPwd);
		
		if(!TextUtils.isEmpty(wifiName))
			mHandler.postDelayed(updateThread, 100);//开始连接WIFI 
	}
	
	private Runnable updateThread = new Runnable() 
	{
		@Override
		public void run() 
		{
			mWifiAdmin.openWifi();
			while (true)
			{
				retryTimes ++;
				if(retryTimes >= 20)//尝试12次还未成功就认为是连接失败了
				{
					if(onConnectResult != null)
						onConnectResult.onConnectResult(false);
					break;
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
				Log.e("", "DMR 正在打开WIFI");
				if(mWifiAdmin.checkState() == WifiManager.WIFI_STATE_ENABLED)
				{
					mWifiAdmin.addNetwork(mWifiAdmin.createWifiInfo(wifiName, wifiPwd));
					Log.e("", "DMR WIFI打开成功-->"+ wifiName);
					break;
				}
			}
		}
	};
	
	public interface OnConnectResultListener
	{
		public void onConnectResult(boolean result);
	}
}
 
