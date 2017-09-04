package com.znt.speaker.m;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.znt.diange.mina.cmd.DeviceInfor;
import com.znt.speaker.entity.Constant;
import com.znt.speaker.entity.LocalDataEntity;
import com.znt.speaker.mina.server.MinaServer;
import com.znt.speaker.netset.CreateWifiApLisnter;
import com.znt.speaker.netset.WifiAdmin;
import com.znt.speaker.netset.WifiApAdmin;
import com.znt.speaker.player.CheckSsidTimer;
import com.znt.speaker.util.LogFactory;
import com.znt.speaker.util.SystemUtils;
import com.znt.speaker.util.XmlUtils;
import com.znt.speaker.v.INetWorkView;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

public class NetWorkModel 
{
	private Activity context = null;
	private WifiAdmin mWifiAdmin = null;
	private INetWorkView iNetWorkView = null;
	
	private String wifiName = "";
	private String wifiPwd = "";
	
	private boolean isConnectRunning = false;
	private int retryTimes = 0;
	private CheckSsidTimer mCheckSsidTimer;
	private boolean isCheckingSsid = false;
	private final int CHECK_SSID = 1;
	
	private String connectSSID = "";
	private Handler mHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == CHECK_SSID)
			{
				checkSsidProcess();
				isCheckingSsid = false;
			}
		};
	};
	public NetWorkModel(Activity context, INetWorkView iNetWorkView)
	{
		this.context = context;
		this.iNetWorkView = iNetWorkView;
		checkWifis = new CheckSsidTimer(context);
		
		mCheckSsidTimer = new CheckSsidTimer(context);
		mCheckSsidTimer.setHandler(mHandler, CHECK_SSID);
		mCheckSsidTimer.setTimeInterval(3000);
		mCheckSsidTimer.setMaxTime(20);
		
		if(mWifiAdmin == null)
		{
			mWifiAdmin = new WifiAdmin(context)
			{
				@Override
				public void onNotifyWifiConnected()
				{
					// TODO Auto-generated method stub
				}
				
				@Override
				public void onNotifyWifiConnectFailed()
				{
					// TODO Auto-generated method stub
				}
				
				@Override
				public void myUnregisterReceiver(BroadcastReceiver receiver)
				{
					// TODO Auto-generated method stub
					//mContext.unregisterReceiver(receiver);
				}
				
				@Override
				public Intent myRegisterReceiver(BroadcastReceiver receiver,
						IntentFilter filter)
				{
					// TODO Auto-generated method stub
					return null;
				}
			};
		}
		if(wifiAp == null)
			wifiAp = new WifiApAdmin(context);
	}
	
	public boolean isWifiEnabled()
	{
		return mWifiAdmin.isWifiEnabled();
	}
	public void openWifi()
	{
		closeWifiAp(context);
		if(!isWifiEnabled())
			mWifiAdmin.openWifi();
	}
	public void startScanWifi()
	{
		if(!isWifiEnabled())
			openWifi();
		mWifiAdmin.startScan();
		setWifiList(mWifiAdmin.getWifiList());
	}
	
	public void connectWifi(final String name, String pwd)
	{
		
		connectSSID = name;
		
		pwd = pwd.replace(" ", "");
		
		wifiName = name;
		wifiPwd = pwd;
		
		Log.e("", "wifiName-->"+wifiName);
		Log.e("", "wifiPwd-->"+wifiPwd);
		
		if(isConnectRunning)
			return;
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				// TODO Auto-generated method stub
				iNetWorkView.connectWifiSatrt(name);//开始连接网络
				isConnectRunning = true;
				doConnect();
				isConnectRunning = false;
			}
		}).start();
	}
	
	private void doConnect()
	{
		if(mWifiAdmin.isWifiEnabled())
		{
			if(mWifiAdmin.checkState() == WifiManager.WIFI_STATE_ENABLED)
			{
				startCheckSSID();
			}
		}
		else
		{
			openWifi();
			while (true)
			{
				retryTimes ++;
				try
				{
					Thread.sleep(2000);
				} 
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(mWifiAdmin.checkState() == WifiManager.WIFI_STATE_ENABLED)
				{
					closeWifiAp(context);
					boolean result = mWifiAdmin.addNetwork(wifiName, wifiPwd);
					if(result)
					{
						startCheckSSID();
					}
					else
						doconenctWifiFail();//网络连接失败
					
					break;
				}
				if(retryTimes >= 12)
				{
					doconenctWifiFail();//网络连接失败
					break;
				}
			}
		}
	}
	
	private void doconenctWifiFail()
	{
		context.runOnUiThread(new Runnable() 
		{
			@Override
			public void run() 
			{
				// TODO Auto-generated method stub
				createWifiAp();
				iNetWorkView.connectWifiFailed();
			}
		});
	}
	
	private void startCheckSSID()
	{
		if(isCheckingSsid)
			return ;
		isCheckingSsid = true;
		mCheckSsidTimer.startTimer();
	}
	public void stopCheckSSID()
	{
		if(!mCheckSsidTimer.isOver() && !mCheckSsidTimer.isStop())
			mCheckSsidTimer.stopTimer();
	}
	private void checkSsidProcess()
	{
		if(mCheckSsidTimer.isStop())
			return;
		
		if(mCheckSsidTimer.isOver())
		{
			doconenctWifiFail();
		}
		else
		{
			String currentSSID = SystemUtils.getConnectWifiSsid(context);
			if(currentSSID.equals(connectSSID))
			{
				stopCheckSSID();
				iNetWorkView.connectWifiSuccess();
			}
		}
	}
	
	
	private WifiApAdmin wifiAp = null;
	private CheckSsidTimer checkWifis = null;
	private List<ScanResult> wifiList = new ArrayList<ScanResult>(); 
	private int reCreatApCount = 0;
	private String localSsid = "";
	
	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(mWifiAdmin.ifHasWifi(localSsid))
			{
				connetNetwork(localSsid);
				checkWifis.stopTimer();
			}
			else if(checkWifis.isOver())
			{
				checkWifis.stopTimer();
				createWifiAp();
			}
		};
	};
	
	private void startCheckWifis()
	{
		checkWifis.setHandler(handler, 0);
		checkWifis.setTimeInterval(3000);
		checkWifis.setMaxTime(5);
		checkWifis.startTimer();
	}
	
	public void setWifiList(List<ScanResult> list)
	{
		if(list != null && list.size() > 0)
		{
			Constant.wifiList.clear();
			Constant.wifiList.addAll(list);
		}
	}
	public List<ScanResult> getWifiList()
	{
		return wifiList;
	}
	
	public void startCheckNetwork()
	{
		localSsid = LocalDataEntity.newInstance(context).getWifiName();
		mWifiAdmin.startScan();
		List<ScanResult> tempList = mWifiAdmin.getWifiList();
		setWifiList(tempList);
		if(!SystemUtils.isNetConnected(context))
		{
			if(!TextUtils.isEmpty(localSsid))//
			{
				if(!isWifiEnabled())
				{
					openWifi();
				}
				startCheckWifis();
			}
			else
				createWifiAp();
		}
		else
		{
			if(TextUtils.isEmpty(localSsid))
			{
				createWifiAp();
			}
			else if(!localSsid.equals(SystemUtils.getConnectWifiSsid(context)))
			{
				if(!mWifiAdmin.isWifiEnabled())
				{
					mWifiAdmin.openWifi();
				}
				startCheckWifis();
			}
			else
			{
				wifiAp.closeWifiAp(context);
				iNetWorkView.connectWifiSuccess();
			}
		}
	}
	
	private void connetNetwork(String ssid)
	{
		connectWifi(ssid, LocalDataEntity.newInstance(context).getWifiPwd());
	}
	
	public void createWifiAp()
	{
		wifiAp.setCreateWifiApLisnter(new CreateWifiApLisnter()
		{
			@Override
			public void onCreateWifiApSuccess()
			{
				// TODO Auto-generated method stub
				//ViewUtils.sendMessage(mHandler, CREATE_AP_SUCCESS);
				//iNetWorkView.createApSuccess();
				DeviceInfor infor = new DeviceInfor();
				infor.setWifiName(getWifiApName());
				infor.setWifiPwd(Constant.WIFI_HOT_PWD);
				MinaServer.getInstance().restartServer();
				//开始倒计时关闭热点
				mHandler.postDelayed(new Runnable() 
				{
					@Override
					public void run() 
					{
						// TODO Auto-generated method stub
						closeWifiAp(context);
						openWifi();
					}
				}, 60 * 5000);
			}
			
			@Override
			public void onCreateWifiApIng()
			{
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onCreateWifiApFail()
			{
				// TODO Auto-generated method stub
				reCreatApCount ++;
				if(reCreatApCount > 3)
				{
					//ViewUtils.sendMessage(mHandler, CREATE_AP_FAIL);
					iNetWorkView.createApFail();
					reCreatApCount = 0;
				}
				else
				{
					createWifiAp();
				}
			}
		});
		wifiAp.startWifiAp(getWifiApName());
	}
	
	public void closeWifiAp(Context context)
	{
		wifiAp.closeWifiAp(context);
	}
	
	public String getWifiApName()
	{
		 return LocalDataEntity.newInstance(context).getDeviceId();//"DianYin"+Constant.UUID_TAG;// + 
	}
	
	
	/**
	 * 从U盘的配置文件配置WIFI
	 * @param path
	 * @param deviceInforRecv
	 */
	public void getWifiSetInfoFromUsb(String path, DeviceInfor deviceInforRecv)
	{
		String wifiText = path + "/zhunit_wifi.txt";
		if(wifiText.contains("file:///"))
			wifiText = wifiText.replace("file:///", "");
		File file = new File(wifiText);
		if(file.exists())
		{
			try 
			{
				long len = file.length();
				FileInputStream fis = new FileInputStream(file);
				byte[] buffer = new byte[(int) len];
				
				//int readLen = fis.read(buffer);
				
				String wifiInfo = new String(buffer, "GB2312");
				String json = XmlUtils.xml2JSON(wifiInfo);
				if(TextUtils.isEmpty(json))
					return;
				JSONObject jsonObj = new JSONObject(json);
				if(jsonObj == null)
					return;
				String ZNT = getInforFromJason(jsonObj, "ZNT-BOX");
				if(TextUtils.isEmpty(ZNT))
					return;
				JSONObject jsonObj1 = new JSONObject(ZNT);
				String wifi_name = getInforFromJason(jsonObj1, "wifi_name");
				if(TextUtils.isEmpty(wifi_name))
					return;
				String wifi_pwd = getInforFromJason(jsonObj1, "wifi_pwd");
				String act_code = getInforFromJason(jsonObj1, "act_code");
				if(TextUtils.isEmpty(act_code))
					return;
				String dev_name = getInforFromJason(jsonObj1, "dev_name");
				if(TextUtils.isEmpty(dev_name))
					return;
				if(deviceInforRecv == null)
					deviceInforRecv = new DeviceInfor();
				deviceInforRecv.setWifiName(wifi_name);
				deviceInforRecv.setWifiPwd(wifi_pwd);
				deviceInforRecv.setActCode(act_code);
				deviceInforRecv.setName(dev_name);
				if(!TextUtils.isEmpty(act_code))
					LocalDataEntity.newInstance(context).setDeviceCode("");
				
				/*String curSsid = getCurrentSsid();
				if(TextUtils.isEmpty(curSsid) || !curSsid.equals(wifi_name))*/
				{
					connectWifi(deviceInforRecv.getWifiName(), deviceInforRecv.getWifiPwd());
				}
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch blocke
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		else
			LogFactory.createLog().e("wifi配置文件不存在");
	}
	
	private String getInforFromJason(JSONObject json, String key)
	{
		if(json == null || key == null)
			return "";
		if(json.has(key))
		{
			try
			{
				String result = json.getString(key);
				if(result.equals("null"))
					result = "";
				return result;
				//return StringUtils.decodeStr(result);
			} 
			catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "";
	}
}
