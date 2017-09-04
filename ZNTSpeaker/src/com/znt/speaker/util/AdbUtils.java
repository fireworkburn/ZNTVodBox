
package com.znt.speaker.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.znt.speaker.R;

public class AdbUtils
{

	public static NotificationManager mNotificationManager;
	public static WakeLock mWakeLock;

	public static final int START_NOTIFICATION_ID = 1;
	public static final int ACTIVITY_SETTINGS = 2;

	public static void saveWiFiState(Context context, boolean wifiState)
	{
		SharedPreferences settings = context.getSharedPreferences("wireless", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("wifiState", wifiState);
		editor.commit();
	}
	
	public static boolean isAdbOpen = false;

	@SuppressWarnings("deprecation")
	public static boolean adbStart(Context context, String port)
	{
		try
		{
			AdbUtils.setProp("service.adb.tcp.port", port);
			try
			{
				if (AdbUtils.isProcessRunning("adbd"))
				{
					AdbUtils.runRootCommand("stop adbd");
				}
			}
			catch (Exception e)
			{}
			AdbUtils.runRootCommand("start adbd");
			try
			{
				
			}
			catch (Exception e)
			{}
			SharedPreferences settings = context.getSharedPreferences("wireless", 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean("mState", true);
			editor.commit();

			// Try to auto connect
			if (AdbUtils.prefsAutoCon(context))
			{
				AdbUtils.autoConnect(context, "c");
			}

			// Wake Lock
			if (AdbUtils.prefsScreenOn(context))
			{
				final PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
				mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, context.getClass().getName());
				mWakeLock.acquire();
			}

			if (AdbUtils.prefsNoti(context))
			{
				
			}
			isAdbOpen = true;
		}
		catch (Exception e)
		{
			return false;
		}
		return true;
	}

	public static boolean adbStop(Context context) throws Exception
	{
		try
		{
			if (isAdbOpen)
			{
				isAdbOpen = false;
				setProp("service.adb.tcp.port", "-1");
				runRootCommand("stop adbd");
				runRootCommand("start adbd");
			}

			SharedPreferences settings = context.getSharedPreferences("wireless", 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean("mState", false);
			editor.commit();

			// Try to auto disconnect
			if (AdbUtils.prefsAutoCon(context))
			{
				AdbUtils.autoConnect(context, "d");
			}

			// Wake Lock
			if (mWakeLock != null)
			{
				mWakeLock.release();
			}

			if (AdbUtils.mNotificationManager != null)
			{
				AdbUtils.mNotificationManager.cancelAll();
			}
		}
		catch (Exception e)
		{
			return false;
		}
		return true;

	}

	public static void autoConnect(Context context, String mode)
	{
		String autoConIP = AdbUtils.prefsAutoConIP(context);
		String autoConPort = AdbUtils.prefsAutoConPort(context);

		if (autoConIP.trim().equals("") || autoConPort.trim().equals(""))
		{
			Toast.makeText(context, R.string.autcon_incomplete, Toast.LENGTH_LONG).show();
			return;
		}

		String urlRequest = "http://" + autoConIP + ":" + autoConPort + "/" + mode + "/" + AdbUtils.getWifiIp(context);

		try
		{
			new AutoConnectTask(urlRequest).execute();
		}
		catch (Exception e)
		{}

	}

	public static boolean isProcessRunning(String processName) throws Exception
	{
		boolean running = false;
		Process process = null;
		process = Runtime.getRuntime().exec("ps");
		BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line = null;
		while ((line = in.readLine()) != null)
		{
			if (line.contains(processName))
			{
				running = true;
				break;
			}
		}
		in.close();
		process.waitFor();
		return running;
	}

	public static boolean hasRootPermission()
	{
		Process process = null;
		DataOutputStream os = null;
		boolean rooted = true;
		try
		{
			process = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();
			if (process.exitValue() != 0)
			{
				rooted = false;
			}
		}
		catch (Exception e)
		{
			rooted = false;
		}
		finally
		{
			if (os != null)
			{
				try
				{
					os.close();
					process.destroy();
				}
				catch (Exception e)
				{}
			}
		}
		return rooted;
	}

	public static boolean runRootCommand(String command)
	{
		Process process = null;
		DataOutputStream os = null;
		try
		{
			process = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(command + "\n");
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();
		}
		catch (Exception e)
		{
			return false;
		}
		finally
		{
			try
			{
				if (os != null)
				{
					os.close();
				}
				process.destroy();
			}
			catch (Exception e)
			{}
		}
		return true;
	}

	public static boolean setProp(String property, String value)
	{
		return runRootCommand("setprop " + property + " " + value);
	}

	public static String getWifiIp(Context context)
	{
		WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		int ip = mWifiManager.getConnectionInfo().getIpAddress();
		return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "." + ((ip >> 24) & 0xFF);
	}

	public static boolean checkWifiState(Context context)
	{
		try
		{
			WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
			if (!mWifiManager.isWifiEnabled() || wifiInfo.getSSID() == null)
			{
				return false;
			}

			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}


	public static boolean prefsOnBoot(Context context)
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		return pref.getBoolean(context.getResources().getString(R.string.pref_onboot_key), false);
	}

	public static boolean prefsVibrate(Context context)
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		return pref.getBoolean(context.getResources().getString(R.string.pref_vibrate_key), true);
	}

	public static boolean prefsSound(Context context)
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		return pref.getBoolean(context.getResources().getString(R.string.pref_sound_key), true);
	}

	public static boolean prefsNoti(Context context)
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		return pref.getBoolean(context.getResources().getString(R.string.pref_noti_key), true);
	}

	public static boolean prefsHaptic(Context context)
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		return pref.getBoolean(context.getResources().getString(R.string.pref_haptic_key), true);
	}

	public static boolean prefsWiFiOn(Context context)
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		return pref.getBoolean(context.getResources().getString(R.string.pref_wifi_on_key), false);
	}

	public static boolean prefsWiFiOff(Context context)
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		return pref.getBoolean(context.getResources().getString(R.string.pref_wifi_off_key), false);

	}

	public static boolean prefsAutoCon(Context context)
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		return pref.getBoolean(context.getResources().getString(R.string.pref_autocon_key), false);
	}

	public static String prefsAutoConIP(Context context)
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		return pref.getString(context.getResources().getString(R.string.pref_autoconip_key), "");
	}

	public static String prefsAutoConPort(Context context)
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		return pref.getString(context.getResources().getString(R.string.pref_autoconport_key), "8555");
	}

	public static boolean prefsScreenOn(Context context)
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		return pref.getBoolean(context.getResources().getString(R.string.pref_screenon_key), false);
	}

}