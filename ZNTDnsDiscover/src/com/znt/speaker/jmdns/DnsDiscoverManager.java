
package com.znt.speaker.jmdns; 

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;

import com.znt.dnsdiscover.MainActivity;

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
	
	private boolean isRunning = false;
	
	public DnsDiscoverManager(Activity activity, Handler handler)
	{
		this.activity = activity;
		this.handler = handler;
	}
	
	public static final int OPEN_DNS_START = 20;
	public static final int OPEN_DNS_SUCCESS = 21;
	public static final int OPEN_DNS_FAIL = 22;
	public static final int CLOSE_DNS_START = 23;
	public static final int CLOSE_DNS_FINISH = 24;
	
	public void openDns(final String dnsName, final boolean isScan) 
	{
		if(isRunning)
			return;
		isRunning = true;
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				if(handler != null)
					handler.obtainMessage(OPEN_DNS_START).sendToTarget();
				
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
					if(isScan)
					{
						listener = new ServiceListener() 
						{

							/*
							 * Note:This event is only the service added event. The
							 * service info associated with this event does not
							 * include resolution information.
							 */
							@Override
							public void serviceAdded(ServiceEvent event) 
							{
								/*
								 * Request service information. The information
								 * about the service is requested and the
								 * ServiceListener.resolveService method is called
								 * as soon as it is available.
								 */
								jmdns.requestServiceInfo(event.getType(),
										event.getName(), 1000);
							}

							/*
							 * A service has been resolved. Its details are now
							 * available in the ServiceInfo record.
							 */
							@Override
							public void serviceResolved(ServiceEvent ev)
							{
								if(handler != null)
									handler.obtainMessage(MainActivity.MSG_DEVICE_ADD, ev).sendToTarget();
								
								/*String iaddr = ev.getInfo().getAddress().getHostAddress();
								String name = ev.getInfo().getName();
								String Key = ev.getInfo().getKey();
								String NiceTextString = ev.getInfo().getNiceTextString();
								String QualifiedName = ev.getInfo().getQualifiedName();
								Map<Fields, String> nameMap = ev.getInfo().getQualifiedNameMap();
								String textStr = ev.getInfo().getTextString();
								String url = ev.getInfo().getURL();
								
								Log.d(LOGTAG, "Service resolved: "
										+ ev.getInfo().getQualifiedName()
										+ " port:" + ev.getInfo().getPort());
								Log.d(LOGTAG, "Service Type : "
										+ ev.getInfo().getType());*/
							}

							@Override
							public void serviceRemoved(ServiceEvent ev) 
							{
								Log.d(LOGTAG, "Service removed: " + ev.getName());
								if(handler != null)
									handler.obtainMessage(MainActivity.MSG_DEVICE_REMOVE, ev).sendToTarget();
							}

						};
						jmdns.addServiceListener(AIR_TUNES_SERVICE_TYPE, listener);
					}
					else
					{
						/**
						 * Advertising a JmDNS Service Construct a service
						 * description for registering with JmDNS. 
						 * Parameters: 
						 * type : fully qualified service type name, such as _dynamix._tcp.local
						 * name : unqualified service instance name, such as DynamixInstance 
						 * port : the local port on which the service runs text string describing the service
						 * text : text describing the service
						 */
						
						serviceInfo = ServiceInfo.create(AIR_TUNES_SERVICE_TYPE,
								dnsName, 7433,
								"Service Advertisement for Ambient neldtv");
						
						/*A Key value map that can be advertised with the service*/
						serviceInfo.setText(getDeviceDetailsMap());
						jmdns.registerService(serviceInfo);
					}

					isRunning = true;
					
					if(handler != null)
						handler.obtainMessage(OPEN_DNS_SUCCESS).sendToTarget();
					
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
					isRunning = false;
					if(handler != null)
						handler.obtainMessage(OPEN_DNS_FAIL).sendToTarget();
					return;
				}
			}
		}).start();

	}
	
	public void stopDns() 
	{
		//Unregister services
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				if (jmdns != null) 
				{
					if(handler != null)
						handler.obtainMessage(CLOSE_DNS_START).sendToTarget();
					
					if (listener != null) 
					{
						jmdns.removeServiceListener(AIR_TUNES_SERVICE_TYPE, listener);
						listener = null;
					}
					jmdns.unregisterAllServices();
					try 
					{
						jmdns.close();
					} 
					catch (IOException e) 
					{
						e.printStackTrace();
					}
					jmdns = null;
					
					if(handler != null)
						handler.obtainMessage(CLOSE_DNS_FINISH).sendToTarget();
				}
				//Release the lock
				lock.release();
				
				isRunning = false;
			}
		}).start();
	}
	
	public boolean isRunning()
	{
		return isRunning;
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
		info.put("device_name", "wifi_speaker");
		info.put("device_id", "61363398");
		info.put("device_version", "1.2.8");
		return info;
	}
}
 
