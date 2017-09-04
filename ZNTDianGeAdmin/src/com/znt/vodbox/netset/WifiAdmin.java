package com.znt.vodbox.netset;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.znt.vodbox.entity.Constant;
import com.znt.vodbox.utils.SystemUtils;

/**
 * @Description: TODO
 * @author ychuang
 * 
 */
public abstract class WifiAdmin
{
	private static final String TAG = "WifiAdmin";

	private WifiManager mWifiManager;
	private WifiInfo mWifiInfo;
	// 扫描出的网络连接列表
	private List<ScanResult> mWifiList = new ArrayList<ScanResult>();
	private List<WifiConfiguration> mWifiConfiguration;

	private WifiLock mWifiLock;

	private String mPasswd = "";
	private String mSSID = "";

	private Context mContext = null;

	public WifiAdmin(Context context)
	{
		mContext = context;
		// 取得WifiManager对象
		mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		// 取得WifiInfo对象
		mWifiInfo = mWifiManager.getConnectionInfo();
		Log.v(TAG, "getIpAddress = " + mWifiInfo.getIpAddress());
	}

	// 打开WIFI
	public void openWifi()
	{
		WifiApAdmin.closeWifiAp(mContext);
		
		if (!mWifiManager.isWifiEnabled())
		{
			if(!mWifiManager.setWifiEnabled(true))
				Toast.makeText(mContext, "打开WIFI失败", 0).show();
		}
	}

	// 关闭WIFI
	public void closeWifi()
	{
		if (mWifiManager.isWifiEnabled())
		{
			mWifiManager.setWifiEnabled(false);
		}
	}
	
	public boolean isWifiEnabled()
	{
		return mWifiManager.isWifiEnabled();
	}

	public abstract Intent myRegisterReceiver(BroadcastReceiver receiver,
			IntentFilter filter);

	public abstract void myUnregisterReceiver(BroadcastReceiver receiver);

	public abstract void onNotifyWifiConnected();

	public abstract void onNotifyWifiConnectFailed();

	// 添加一个网络并连接
	public void addNetwork(WifiConfiguration wcg)
	{
		WifiApAdmin.closeWifiAp(mContext);
		int wcgID = mWifiManager.addNetwork(wcg);
		boolean b = mWifiManager.enableNetwork(wcgID, true);
		startTimer();
	}

	public static final int TYPE_NO_PASSWD = 0x11;
	public static final int TYPE_WEP = 0x12;
	public static final int TYPE_WPA = 0x13;

	private int time = 0;
	private final int MAX_TIME = 30;
	private boolean isTimerRunning = false;
	private Handler handler = new Handler();
	private Runnable task = new Runnable()
	{
		@Override
		public void run()
		{
			// TODO Auto-generated method stub
			time ++;
			String curssid = SystemUtils.getConnectWifiSsid(mContext);
			if(mSSID.equals(curssid))//连接成功
			{
				onNotifyWifiConnected();
				stopTimer();
			}
			else if(isTimerRunning)
				handler.postDelayed(task, 1000);
			if(time >= MAX_TIME)
			{
				onNotifyWifiConnectFailed();
				stopTimer();
			}
		}
	};

	private void startTimer()
	{
		
		if (handler != null)
		{
			stopTimer();
		}
		isTimerRunning = true;
		handler.postDelayed(task, 1000);
	}

	private void stopTimer()
	{
		if (handler != null && isTimerRunning)
		{
			handler.removeCallbacks(task);
			time = 0;
		}
		isTimerRunning = false;
	}

	@Override
	protected void finalize()
	{
		try
		{
			super.finalize();
		} catch (Throwable e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*public WifiConfiguration createWifiInfo(String SSID, String password,
			int type)
	{

		Log.v(TAG, "SSID = " + SSID + "## Password = " + password
				+ "## Type = " + type);
		mSSID = SSID;
		mPasswd = password;

		WifiConfiguration config = new WifiConfiguration();
		config.allowedAuthAlgorithms.clear();
		config.allowedGroupCiphers.clear();
		config.allowedKeyManagement.clear();
		config.allowedPairwiseCiphers.clear();
		config.allowedProtocols.clear();
		config.SSID = "\"" + SSID + "\"";

		WifiConfiguration tempConfig = this.IsExsits(SSID);
		if (tempConfig != null)
		{
			mWifiManager.removeNetwork(tempConfig.networkId);
		}

		// 分为三种情况：1没有密码2用wep加密3用wpa加密
		if (type == TYPE_NO_PASSWD)
		{// WIFICIPHER_NOPASS
			config.wepKeys[0] = "";
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;

		} else if (type == TYPE_WEP)
		{ // WIFICIPHER_WEP
			config.hiddenSSID = true;
			config.wepKeys[0] = "\"" + password + "\"";
			config.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.SHARED);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			config.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.WEP104);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		} else if (type == TYPE_WPA)
		{ // WIFICIPHER_WPA
			config.preSharedKey = "\"" + password + "\"";
			config.hiddenSSID = true;
			config.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.OPEN);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			config.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.TKIP);
			// config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.status = WifiConfiguration.Status.ENABLED;
		}

		return config;
	}*/
	public WifiConfiguration createWifiInfo(String SSID, String Password)
	{
		int type = TYPE_NO_PASSWD;
		if(TextUtils.isEmpty(Password))
			type = TYPE_NO_PASSWD;
		else
			type = TYPE_WPA;
		
		WifiConfiguration tempConfig = isExsits(SSID);

		if (tempConfig != null) 
		{
			mWifiManager.removeNetwork(tempConfig.networkId);
		}
		
		WifiConfiguration config = new WifiConfiguration();
		config.allowedAuthAlgorithms.clear();
		config.allowedGroupCiphers.clear();
		config.allowedKeyManagement.clear();
		config.allowedPairwiseCiphers.clear();
		config.allowedProtocols.clear();
		config.SSID = "\"" + SSID + "\"";
		// nopass
		if (type == TYPE_NO_PASSWD)
		{
			// config.wepKeys[0] = "";
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			// config.wepTxKeyIndex = 0;
		}
		// wep
		else if (type == TYPE_WEP)
		{
			if (!TextUtils.isEmpty(Password)) {
				if (isHexWepKey(Password)) {
					config.wepKeys[0] = Password;
				} else {
					config.wepKeys[0] = "\"" + Password + "\"";
				}
			}
			config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
			config.allowedAuthAlgorithms.set(AuthAlgorithm.SHARED);
			config.allowedKeyManagement.set(KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		}
		// wpa
		else if (type == TYPE_WPA)
		{
			config.preSharedKey = "\"" + Password + "\"";
			config.hiddenSSID = true;
			config.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.OPEN);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			config.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.TKIP);
			// 此处需要修改否则不能自动重联
			// config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.status = WifiConfiguration.Status.ENABLED;
		}
		return config;
	}
	
	private static boolean isHexWepKey(String wepKey) {
		final int len = wepKey.length();

		// WEP-40, WEP-104, and some vendors using 256-bit WEP (WEP-232?)
		if (len != 10 && len != 26 && len != 58) {
			return false;
		}

		return isHex(wepKey);
	}

	private static boolean isHex(String key) {
		for (int i = key.length() - 1; i >= 0; i--) {
			final char c = key.charAt(i);
			if (!(c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a'
					&& c <= 'f')) {
				return false;
			}
		}

		return true;
	}
	
	// 查看以前是否也配置过这个网络
	private WifiConfiguration isExsits(String SSID) {
		List<WifiConfiguration> existingConfigs = mWifiManager
				.getConfiguredNetworks();
		for (WifiConfiguration existingConfig : existingConfigs) {
			if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
				return existingConfig;
			}
		}
		return null;
	}

	public static final int WIFI_CONNECTED = 0x01;
	public static final int WIFI_CONNECT_FAILED = 0x02;
	public static final int WIFI_CONNECTING = 0x03;

	/**
	 * 判断wifi是否连接成功,不是network
	 * 
	 * @param context
	 * @return
	 */
	public int isWifiContected(Context context)
	{
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiNetworkInfo = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		Log.v(TAG,
				"isConnectedOrConnecting = "
						+ wifiNetworkInfo.isConnectedOrConnecting());
		Log.d(TAG,
				"wifiNetworkInfo.getDetailedState() = "
						+ wifiNetworkInfo.getDetailedState());
		if (wifiNetworkInfo.getDetailedState() == DetailedState.OBTAINING_IPADDR
				|| wifiNetworkInfo.getDetailedState() == DetailedState.CONNECTING)
		{
			return WIFI_CONNECTING;
		} else if (wifiNetworkInfo.getDetailedState() == DetailedState.CONNECTED)
		{
			return WIFI_CONNECTED;
		} else
		{
			Log.d(TAG,
					"getDetailedState() == "
							+ wifiNetworkInfo.getDetailedState());
			return WIFI_CONNECT_FAILED;
		}
	}

	private WifiConfiguration IsExsits(String SSID)
	{
		List<WifiConfiguration> existingConfigs = mWifiManager
				.getConfiguredNetworks();
		for (WifiConfiguration existingConfig : existingConfigs)
		{
			if (existingConfig.SSID.equals("\"" + SSID + "\"") /*
																 * &&
																 * existingConfig
																 * .
																 * preSharedKey.
																 * equals("\"" +
																 * password +
																 * "\"")
																 */)
			{
				return existingConfig;
			}
		}
		return null;
	}
	
    public boolean isWifiHasPwd(String ssid) 
    {
    	boolean result = true;
        List<ScanResult> list = mWifiManager.getScanResults();
        for (ScanResult scResult : list) 
        {
            if (!TextUtils.isEmpty(scResult.SSID) && scResult.SSID.equals(ssid))
            {
                String capabilities = scResult.capabilities;
                Log.i("hefeng","capabilities=" + capabilities);
                if (!TextUtils.isEmpty(capabilities))
                {
                    if (capabilities.contains("WPA") || capabilities.contains("wpa"))
                    {
                        Log.i("hefeng", "wpa");
                    } 
                    else if (capabilities.contains("WEP") || capabilities.contains("wep")) 
                    {
                        Log.i("hefeng", "wep");
                        result = true;
                    } 
                    else
                    {
                        Log.i("hefeng", "no");
                        result = false;
                    }
                }
                
                break;
            }
        }
        
        return result;
    }

	// 断开指定ID的网络
	public void disconnectWifi(int netId)
	{
		mWifiManager.disableNetwork(netId);
		mWifiManager.disconnect();
	}

	// 检查当前WIFI状态
	public int checkState()
	{
		return mWifiManager.getWifiState();
	}

	// 锁定WifiLock
	public void acquireWifiLock()
	{
		mWifiLock.acquire();
	}

	// 解锁WifiLock
	public void releaseWifiLock()
	{
		// 判断时候锁定
		if (mWifiLock.isHeld())
		{
			mWifiLock.acquire();
		}
	}

	// 创建一个WifiLock
	public void creatWifiLock()
	{
		mWifiLock = mWifiManager.createWifiLock("Test");
	}

	// 得到配置好的网络
	public List<WifiConfiguration> getConfiguration()
	{
		return mWifiConfiguration;
	}

	// 指定配置好的网络进行连接
	public void connectConfiguration(int index)
	{
		// 索引大于配置好的网络索引返回
		if (index > mWifiConfiguration.size())
		{
			return;
		}
		// 连接配置好的指定ID的网络
		mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId,
				true);
	}

	public void startScan()
	{
		mWifiManager.startScan();
		fillWifiList();
		mWifiConfiguration = mWifiManager.getConfiguredNetworks();
	}

	// 得到网络列表
	public List<ScanResult> getWifiList()
	{
		return mWifiList;
	}
	
	public boolean ifHasWifi(String ssid)
	{
		boolean result = false;
		if(mWifiManager != null)
			mWifiManager.startScan();
		fillWifiList();
		if(mWifiList != null && mWifiList.size() > 0)
		{
			int size = mWifiList.size();
			for(int i=0;i<size;i++)
			{
				ScanResult scanResult = mWifiList.get(i);
				if(scanResult.SSID.equals(ssid))
				{
					result = true;
					break;
				}
			}
		}
		
		return result;
	}
	
	public String ifHasWifiAp()
	{
		String ssid = null;
		if(mWifiManager != null)
			mWifiManager.startScan();
		fillWifiList();
		if(mWifiList != null && mWifiList.size() > 0)
		{
			int size = mWifiList.size();
			for(int i=0;i<size;i++)
			{
				ScanResult scanResult = mWifiList.get(i);
				if(scanResult.SSID.endsWith(Constant.UUID_TAG))
				{
					ssid = scanResult.SSID;
					break;
				}
			}
		}
		
		return ssid;
	}
	
	private void fillWifiList()
	{
		if(mWifiList != null)
		{
			mWifiList.clear();
		}
		List<ScanResult> tempList = mWifiManager.getScanResults();
		if(tempList != null)
			mWifiList.addAll(tempList);
	}

	// 查看扫描结果
	public StringBuilder lookUpScan()
	{
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < mWifiList.size(); i++)
		{
			stringBuilder
					.append("Index_" + new Integer(i + 1).toString() + ":");
			// 将ScanResult信息转换成一个字符串包
			// 其中把包括：BSSID、SSID、capabilities、frequency、level
			stringBuilder.append((mWifiList.get(i)).toString());
			stringBuilder.append("/n");
		}
		return stringBuilder;
	}

	// 得到MAC地址
	public String getMacAddress()
	{
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
	}

	// 得到接入点的BSSID
	public String getBSSID()
	{
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
	}

	// 得到IP地址
	public int getIPAddress()
	{
		return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
	}

	// 得到连接的ID
	public int getNetworkId()
	{
		return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
	}

	// 得到WifiInfo的所有信息包
	public String getWifiInfo()
	{
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
	}
}
