package com.znt.vodbox.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class NetWorkUtils 
{

	/**
	 * 检查网络是否可用
	 * 
	 * @param paramContext
	 * @return
	 */
	public static boolean checkEnable(Context paramContext) 
	{
		boolean i = false;
		NetworkInfo localNetworkInfo = ((ConnectivityManager) paramContext
				.getSystemService("connectivity")).getActiveNetworkInfo();
		if ((localNetworkInfo != null) && (localNetworkInfo.isAvailable()))
			return true;
		return false;
	}

	/**
	 * 将ip的整数形式转换成ip形式
	 * 
	 * @param ipInt
	 * @return
	 */
	public static String int2ip(int ipInt) {
		StringBuilder sb = new StringBuilder();
		sb.append(ipInt & 0xFF).append(".");
		sb.append((ipInt >> 8) & 0xFF).append(".");
		sb.append((ipInt >> 16) & 0xFF).append(".");
		sb.append((ipInt >> 24) & 0xFF);
		return sb.toString();
	}

	/**
	 * 获取当前ip地址
	 * 
	 * @param context
	 * @return
	 */
	public static String getLocalIpAddress(Context context) {
		try {
			// for (Enumeration<NetworkInterface> en = NetworkInterface
			// .getNetworkInterfaces(); en.hasMoreElements();) {
			// NetworkInterface intf = en.nextElement();
			// for (Enumeration<InetAddress> enumIpAddr = intf
			// .getInetAddresses(); enumIpAddr.hasMoreElements();) {
			// InetAddress inetAddress = enumIpAddr.nextElement();
			// if (!inetAddress.isLoopbackAddress()) {
			// return inetAddress.getHostAddress().toString();
			// }
			// }
			// }
			WifiManager wifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			int i = wifiInfo.getIpAddress();
			return int2ip(i);
		} catch (Exception ex) {
			return "";
		}
		// return null;
	}
	
	//检查连接的有效xìng
    public static boolean checkURL(String url) 
    {
        boolean value = false;
        try 
        {
                HttpURLConnection conn = (HttpURLConnection) new URL(url)
                                .openConnection();
                int code = conn.getResponseCode();
                if (code != 200) 
                {
                        value = false;
                } 
                else 
                {
                        value = true;
                }
        } 
        catch (MalformedURLException e) 
        {
                // TODO Auto-generated catch block
                e.printStackTrace();
        } 
        catch (IOException e) 
        {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }
        return value;
    }
	
}