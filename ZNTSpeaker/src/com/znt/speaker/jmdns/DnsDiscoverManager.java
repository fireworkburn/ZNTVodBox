
package com.znt.speaker.jmdns; 

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import com.znt.diange.mina.cmd.DeviceInfor;
import com.znt.speaker.db.DBManager;
import com.znt.speaker.entity.Constant;
import com.znt.speaker.entity.LocalDataEntity;
import com.znt.speaker.util.LogFactory;
import com.znt.speaker.util.SystemUtils;

/** 
 * @ClassName: DnsDiscoverManager 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-12-23 下午4:53:44  
 */
public class DnsDiscoverManager
{
	private Activity activity = null;
	
	android.net.wifi.WifiManager.MulticastLock lock;
	android.os.Handler handler = new android.os.Handler();
	private String LOGTAG = getClass().getSimpleName();
	
	//private String type = "_test._tcp.local.";
	static final String AIR_TUNES_SERVICE_TYPE = "_raop._tcp.local.";
	
	private JmDNS jmdns = null;
	private ServiceListener listener = null;
	private ServiceInfo serviceInfo;
	
	private String dnsName = "";
	
	private boolean isRunning = false;
	
	public DnsDiscoverManager(Activity activity)
	{
		this.activity = activity;
	}
	
	public void openDns(final String dnsName) 
	{
		
		/*String localUuid = LocalDataEntity.newInstance(activity).getDeviceInfor().getId();
		if(TextUtils.isEmpty(localUuid))
			return;
		
		if(isRunning)
			return;
		isRunning = true;
		
		this.dnsName = dnsName;
		
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) activity.getSystemService(android.content.Context.WIFI_SERVICE);
				lock = wifi.createMulticastLock(getClass().getSimpleName());
				lock.setReferenceCounted(false);
				try 
				{
					InetAddress addr = getLocalIpAddress();
					String hostname = addr.getHostName();
					lock.acquire();
					Log.d(LOGTAG, "Addr : " + addr);
					Log.d(LOGTAG, "Hostname : " + hostname);
					jmdns = JmDNS.create(addr, hostname);

					*//**
					 * Advertising a JmDNS Service Construct a service
					 * description for registering with JmDNS. 
					 * Parameters: 
					 * type : fully qualified service type name, such as _dynamix._tcp.local
					 * name : unqualified service instance name, such as DynamixInstance 
					 * port : the local port on which the service runs text string describing the service
					 * text : text describing the service
					 *//*
					serviceInfo = ServiceInfo.create(AIR_TUNES_SERVICE_TYPE,
							dnsName, 7433,
							"Service Advertisement for Ambient znt");
					
					A Key value map that can be advertised with the service
					serviceInfo.setText(getDeviceDetailsMap());
					jmdns.registerService(serviceInfo);
					
					isRunning = true;
					
					LogFactory.createLog().e("################DNS服务开启完成-->"+dnsName);
					
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
					isRunning = false;
					return;
				}
			}
		}).start();*/

	}
	
	public void stopDns() 
	{
		//Unregister services
		/*if (jmdns != null && isRunning) 
		{
			try 
			{
				if (listener != null) 
				{
					jmdns.removeServiceListener(AIR_TUNES_SERVICE_TYPE, listener);
					listener = null;
				}
				jmdns.unregisterAllServices();
				if(jmdns != null)
					jmdns.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			jmdns = null;
			isRunning = false;
		}
		//Release the lock
		if(lock != null)
			lock.release();*/
	}
	
	public void restartDns() 
	{
		//Unregister services
		/*new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				stopDns();
				openDns(dnsName);
			}
		}).start();*/
	}

	public InetAddress getLocalIpAddress() 
	{
		WifiManager wifiManager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		InetAddress address = null;
		try 
		{
			address = InetAddress.getByName(String.format(Locale.ENGLISH,
					"%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
					(ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff)));
		} 
		catch (UnknownHostException e) 
		{
			e.printStackTrace();
		}
		return address;
	}

	private Map<String, String> getDeviceDetailsMap() 
	{
		Map<String, String> info = new HashMap<String, String>();
		
		DeviceInfor deviceInfor = LocalDataEntity.newInstance(activity).getDeviceInfor();
		String wifiName = SystemUtils.getConnectWifiSsid(activity);
		if(!deviceInfor.getWifiName().equals(wifiName))
		{
			String wifiPwd = DBManager.newInstance(activity).getWifiPwdByName(wifiName);
			deviceInfor.setWifiName(wifiName);
			deviceInfor.setWifiPwd(wifiPwd);
		}
		JSONObject jsonObj = new JSONObject();
		try
		{
			jsonObj.put("device_name", deviceInfor.getName());
			jsonObj.put("device_version", SystemUtils.getVersionName(activity));
			jsonObj.put("device_ssid", deviceInfor.getWifiName());
			jsonObj.put("device_id", deviceInfor.getId());
			jsonObj.put("device_code", deviceInfor.getCode());
			if(!TextUtils.isEmpty(deviceInfor.getWifiPwd()))
				jsonObj.put("device_pwd", deviceInfor.getWifiPwd());
			else
			{
				if(deviceInfor.getWifiName() != null && deviceInfor.getWifiName().contains(Constant.UUID_TAG))
					jsonObj.put("device_pwd", "00000000");
				else
					jsonObj.put("device_pwd", "");
			}
		} catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		info.put("device_infor", jsonObj.toString());
		return info;
	}
}
 
