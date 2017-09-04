package com.znt.speaker.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

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
	
	public static String getMacAddress() {
       
		String macSerial = null;
        String str = "";
        try {
                Process pp = Runtime.getRuntime().exec(
                                "cat /sys/class/net/wlan0/address ");
                InputStreamReader ir = new InputStreamReader(pp.getInputStream());
                LineNumberReader input = new LineNumberReader(ir);


                for (; null != str;) {
                        str = input.readLine();
                        if (str != null) {
                                macSerial = str.trim();// 去空格
                                break;
                        }
                }
        } catch (IOException ex) {
                // 赋予默认值
                ex.printStackTrace();
        }
        if(TextUtils.isEmpty(macSerial))
        	return "";
        if(macSerial.contains(":"))
        	macSerial = macSerial.replace(":", "");
        return macSerial.trim();
    }
	public static String callCmd(String cmd,String filter) {   
	     String result = "";   
	     String line = "";   
	     try {
	         Process proc = Runtime.getRuntime().exec(cmd);
	         InputStreamReader is = new InputStreamReader(proc.getInputStream());   
	         BufferedReader br = new BufferedReader (is);   
	          
	         //执行命令cmd，只取结果中含有filter的这一行
	         while ((line = br.readLine ()) != null && line.contains(filter)== false) {   
	             //result += line;
	             Log.i("test","line: "+line);
	         }
	          
	         result = line;
	         Log.i("test","result: "+result);
	     }   
	     catch(Exception e) {   
	         e.printStackTrace();   
	     }   
	     return result;   
	 }
	public static boolean checkEthernet(Context context)
	{
		ConnectivityManager conn =(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = conn.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
		if(networkInfo == null)
			return false;
		return networkInfo.isConnected();
	}
}