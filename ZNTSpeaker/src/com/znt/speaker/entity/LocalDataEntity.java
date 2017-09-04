package com.znt.speaker.entity;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Build;
import android.provider.Settings.Secure;
import android.text.TextUtils;

import com.znt.diange.mina.cmd.DeviceInfor;
import com.znt.diange.mina.entity.PermissionType;
import com.znt.diange.mina.entity.PlayRes;
import com.znt.speaker.R;
import com.znt.speaker.util.MySharedPreference;
import com.znt.speaker.util.SystemUtils;

public class LocalDataEntity 
{

	private Context context = null;
	
	private static LocalDataEntity INSTANCE = null;
	
	private final String DEVICE_ID = "WS_DEVICE_ID";
	private final String DEVICE_NAME = "WS_DEVICE_NAME";
	private final String WIFI_PWD = "WS_WIFI_PWD";
	private final String WIFI_SSID = "WS_WIFI_SSID";
	private final String IS_INIT = "WS_IS_INIT";
	private final String PLAY_PERMISSION = "PLAY_PERMISSION";
	private final String PLAY_RES = "PLAY_RES";
	private final String LOCAL_MUSIC_INDEX = "LOCAL_MUSIC_INDEX";
	private final String SEEK_POS = "SEEK_POS";
	private final String CUR_LAST_UPDATE_TIME = "CUR_LAST_UPDATE_TIME";
	private final String DEVICE_ADDR = "DEVICE_ADDR";
	private final String DEVICE_LAT = "DEVICE_LAT";
	private final String DEVICE_LON = "DEVICE_LON";
	private final String DEVICE_CODE = "DEVICE_CODE";
	private final String ACT_CODE = "ACT_CODE";
	private final String MUSIC_UPDATE_TIME = "MUSIC_UPDATE_TIME";
	private final String PLAN_TIME = "PLAN_TIME";
	private final String PLAN_ID = "PLAN_ID";
	private final String DB_VERSION = "DB_VERSION";
	private final String VOLUME = "VOLUME";
	private final String DOWNLOADFLAG = "DOWNLOADFLAG";
	private final String VIDEO_WHIRL = "VIDEO_WHIRL";
	
	private MySharedPreference sharedPre = null;
	
	public LocalDataEntity(Context context)
	{
		this.context = context;
		sharedPre = MySharedPreference.newInstance(context);
	}
	public static LocalDataEntity newInstance(Context context)
	{
		if(INSTANCE == null)
		{
			synchronized (LocalDataEntity.class) 
			{
				if(INSTANCE == null)
					INSTANCE = new LocalDataEntity(context);
			}
		}
		return INSTANCE;
	}
	
	public void setIsInit(boolean isInit)
	{
		sharedPre.setData(IS_INIT, isInit);
	}
	public boolean isInit()
	{
		return sharedPre.getData(IS_INIT, true);
	}
	
	public void setDbVersion(int version)
	{
		sharedPre.setData(DB_VERSION, version);
	}
	public int getDbVersion()
	{
		return sharedPre.getData(DB_VERSION, 0);
	}
	
	public void setVolume(String volume)
	{
		sharedPre.setData(VOLUME, volume);
	}
	public int getVolume()
	{
		String volume = sharedPre.getData(VOLUME, "0");
		if(!TextUtils.isEmpty(volume))
			return Integer.parseInt(volume);
		else
			return 0;
	}
	public void setDownloadFlag(String downloadFlag)
	{
		sharedPre.setData(DOWNLOADFLAG, downloadFlag);
	}
	public String getDownloadFlag()
	{
		return sharedPre.getData(DOWNLOADFLAG, "");
	}
	
	public void clearWifiRecord()
	{
		sharedPre.setData(WIFI_SSID, "");
		sharedPre.setData(WIFI_PWD, "");
	}
	
	public void setDeviceInfor(DeviceInfor infor)
	{
		if(infor == null)
			return;
		
		/*if(!TextUtils.isEmpty(infor.getId()))
			sharedPre.setData(DEVICE_ID, getDeviceId());*/
		if(!TextUtils.isEmpty(infor.getName()))
			sharedPre.setData(DEVICE_NAME, infor.getName());
		if(!TextUtils.isEmpty(infor.getWifiName()))
			sharedPre.setData(WIFI_SSID, infor.getWifiName());
		//if(!TextUtils.isEmpty(infor.getWifiPwd()))
			sharedPre.setData(WIFI_PWD, infor.getWifiPwd());
		if(!TextUtils.isEmpty(infor.getAddr()))
			sharedPre.setData(DEVICE_ADDR, infor.getAddr());
		if(!TextUtils.isEmpty(infor.getCode()))
			sharedPre.setData(DEVICE_CODE, infor.getCode());
		if(!TextUtils.isEmpty(infor.getActCode()))
			sharedPre.setData(ACT_CODE, infor.getActCode());
	}
	public DeviceInfor getDeviceInfor()
	{
		DeviceInfor infor = new DeviceInfor();
		//String id = sharedPre.getData(DEVICE_ID, getDeviceId());
		Build b = new Build();
		String name = sharedPre.getData(DEVICE_NAME, context.getResources().getString(R.string.device_name_default) + "_" + b.MODEL);
		String wifissid = sharedPre.getData(WIFI_SSID, "");
		String wifiPwd = sharedPre.getData(WIFI_PWD, "");
		String code = sharedPre.getData(DEVICE_CODE, "");
		String actCode = sharedPre.getData(ACT_CODE, "");
		
		infor.setId(getDeviceId());
		infor.setName(name);
		infor.setWifiName(wifissid);
		infor.setWifiPwd(wifiPwd);
		infor.setCode(code);
		infor.setActCode(actCode);
		return infor;
	}
	
	public String getDeviceId()
	{
		if(TextUtils.isEmpty(Constant.DEVICE_MAC))
		{
			String macAddr = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);;
			Constant.DEVICE_MAC = macAddr +  Constant.UUID_TAG;
			return Constant.DEVICE_MAC;//sharedPre.getData(DEVICE_ID, NetWorkUtils.getMacAddress() + Constant.UUID_TAG);
		}
		else
			return Constant.DEVICE_MAC;
	}
	
	
	public String encodeDeviceInfor()
	{
		
		Build b = new Build();
		String device_name = sharedPre.getData(DEVICE_NAME, "鍔╀綘绉戞妧_" + b.MODEL);
		String device_ssid = sharedPre.getData(WIFI_SSID, "");
		String device_pwd = sharedPre.getData(WIFI_PWD, "");
		
		JSONObject json = new JSONObject();
		try
		{
			json.put("device_name", device_name);
			json.put("device_id", getDeviceId());
			json.put("device_code", getDeviceCode());
			json.put("device_version", SystemUtils.getVersionName(context));
			json.put("device_ssid", device_ssid);
			json.put("device_pwd", device_pwd);
		} 
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json.toString();
	}
	
	public void updateWifi(String wifiName, String wifiPwd)
	{
		if(!TextUtils.isEmpty(wifiName))
			sharedPre.setData(WIFI_SSID, wifiName);
		//if(!TextUtils.isEmpty(wifiPwd))
			sharedPre.setData(WIFI_PWD, wifiPwd);
	}
	public String getWifiName()
	{
		return sharedPre.getData(WIFI_SSID, "");
	}
	public String getWifiPwd()
	{
		return sharedPre.getData(WIFI_PWD, "");
	}
	
	public String getMusicUpdateTime()
	{
		return sharedPre.getData(MUSIC_UPDATE_TIME, "");
	}
	public void setMusicUpdateTime(String time)
	{
		sharedPre.setData(MUSIC_UPDATE_TIME, time);
	}
	
	public void setPlanId(String planId)
	{
		sharedPre.setData(PLAN_ID, planId);
	}
	public String getPlayId()
	{
		return sharedPre.getData(PLAN_ID, "");
	}
	public void setPlanTime(String planTime)
	{
		sharedPre.setData(PLAN_TIME, planTime);
	}
	public String getPlayTime()
	{
		return sharedPre.getData(PLAN_TIME, "");
	}
	
	public void setDeviceCode(String code)
	{
		sharedPre.setData(DEVICE_CODE, code);
	}
	public String getDeviceCode()
	{
		return sharedPre.getData(DEVICE_CODE, "");
	}
	
	public void setMusicIndex(int index)
	{
		sharedPre.setData(LOCAL_MUSIC_INDEX, index);
	}
	public int getMusicIndex()
	{
		return sharedPre.getData(LOCAL_MUSIC_INDEX, 0);
	}
	public void setSeekPos(int index)
	{
		sharedPre.setData(SEEK_POS, index);
	}
	public int getSeekPos()
	{
		return sharedPre.getData(SEEK_POS, -1);
	}
	public void setCurLastUpdateTime(long index)
	{
		sharedPre.setData(CUR_LAST_UPDATE_TIME, index);
	}
	public long getCurLastUpdateTime()
	{
		return sharedPre.getDataLong(CUR_LAST_UPDATE_TIME, -1);
	}
	
	public void setPlayPermission(String permission)
	{
		sharedPre.setData(PLAY_PERMISSION, permission);
	}
	public String getPlayPermission()
	{
		return sharedPre.getData(PLAY_PERMISSION, PermissionType.ALL);
	}
	
	/**
	 * 鎾斁鐨勯煶涔愭簮 鏈湴鍜岀綉缁� 
	 * @param res  local  net
	 */
	public void setPlayRes(String res)
	{
		sharedPre.setData(PLAY_RES, res);
	}
	public String getPlayRes()
	{
		return sharedPre.getData(PLAY_RES, PlayRes.LOCAL);
	}
	
	public void clearDeviceInfor()
	{
		setDeviceLocation("", "");
		setDeviceName("");
		setDeviceAddr("");
	}
	public void setDeviceLocation(String lon, String lat)
	{
		sharedPre.setData(DEVICE_LAT, lat);
		sharedPre.setData(DEVICE_LON, lon);
	}
	public void setDeviceAddr(String addr)
	{
		sharedPre.setData(DEVICE_ADDR, addr);
	}
	
	public void setDeviceName(String name)
	{
		sharedPre.setData(DEVICE_NAME, name);
	}
	
	public String getDeviceAddr()
	{
		return sharedPre.getData(DEVICE_ADDR, "");
	}
	public String getDeviceLat()
	{
		return sharedPre.getData(DEVICE_LAT, "");
	}
	public String getDeviceLon()
	{
		return sharedPre.getData(DEVICE_LON, "");
	}
	
	public void setVideoWhirl(String videoWhirl)
	{
		sharedPre.setData(VIDEO_WHIRL, videoWhirl);
	}
	public String getVideoWhirl()
	{
		return sharedPre.getData(VIDEO_WHIRL, "");
	}
	
}
