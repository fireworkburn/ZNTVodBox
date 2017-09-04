package com.znt.diange.entity;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.znt.diange.mina.cmd.DeviceInfor;
import com.znt.diange.mina.entity.UserInfor;
import com.znt.diange.utils.MySharedPreference;
import com.znt.diange.utils.SystemUtils;

public class LocalDataEntity 
{

	private Context context = null;
	private final String RENDER_DEVICE = "RENDER_DEVICE";
	private final String SERVER_DEVICE = "SERVER_DEVICE";
	private final String MUSIC_INDEX = "MUSIC_INDEX";
	private final String FIRST_INIT = "FIRST_INIT";
	private final String IS_SONG_HINT_SHOW = "IS_SONG_HINT_SHOW";
	
	private final String DEVICE_ID = "DEVICE_ID";
	private final String DEVICE_CODE = "DEVICE_CODE";
	private final String DEVICE_VERSION = "DEVICE_VERSION";
	private final String DEVICE_NAME = "DEVICE_NAME";
	private final String DEVICE_IP = "DEVICE_IP";
	private final String WIFI_SSID = "WIFI_SSID";
	private final String WIFI_PWD = "WIFI_PWD";
	private final String WIFI_MAC = "WIFI_MAC";
	private final String USER_ID = "USER_ID";
	private final String THIRD_ID = "THIRD_ID";
	private final String THIRD_TOKEN = "THIRD_TOKEN";
	private final String USER_NAME = "USER_NAME";
	private final String USER_PWD = "USER_PWD";
	private final String USER_HEAD = "USER_HEAD";
	private final String USER_ACCOUNT = "USER_ACCOUNT";
	private final String USER_DEVICES = "USER_DEVICES";
	private final String LOGIN_TYPE = "LOGIN_TYPE";
	private final String COIN = "COIN";
	private final String ADMIN = "ADMIN";
	private final String DEVICE_ADDR = "DEVICE_ADDR";
	private final String LAT = "LAT";
	private final String LON = "LON";
	private final String PLAY_PERMISSION = "PLAY_PERMISSION";
	private final String PLAY_RES = "PLAY_RES";
	private final String LAST_REFRESH_TIME = "LAST_REFRESH_TIME";
	
	private MySharedPreference sharedPre = null;
	
	public LocalDataEntity(Context context)
	{
		this.context = context;
		sharedPre = MySharedPreference.newInstance(context);
	}
	public static LocalDataEntity newInstance(Context context)
	{
		return new LocalDataEntity(context);
	}
	
	public void setLoginType(String type)
	{
		sharedPre.setData(LOGIN_TYPE, type);
	}
	public String getLoginType()
	{
		return sharedPre.getData(LOGIN_TYPE, "");
	}
	public void setThirdId(String id)
	{
		sharedPre.setData(THIRD_ID, id);
	}
	public String getThirdId()
	{
		return sharedPre.getData(THIRD_ID, "");
	}
	public void setThirdToken(String token)
	{
		sharedPre.setData(THIRD_TOKEN, token);
	}
	public String getThirdToken()
	{
		return sharedPre.getData(THIRD_TOKEN, "");
	}
	
	public void setUserName(String name)
	{
		sharedPre.setData(USER_NAME, name);
	}
	public String getUserName()
	{
		return sharedPre.getData(USER_NAME, "DG-" + Build.MODEL);
	}
	public String getUserHead()
	{
		return sharedPre.getData(USER_HEAD, "");
	}
	public void addUserDevices(String devId)
	{
		String devices = sharedPre.getData(USER_DEVICES, "");
		devices += devId;
		sharedPre.setData(USER_DEVICES, devices);
	}
	public void setUserInfor(UserInfor userInfor)
	{
		sharedPre.setData(USER_ID, userInfor.getUserId());
		sharedPre.setData(USER_NAME, userInfor.getUserName());
		sharedPre.setData(USER_PWD, userInfor.getPwd());
		sharedPre.setData(USER_ACCOUNT, userInfor.getAccount());
		sharedPre.setData(USER_HEAD, userInfor.getHead());
		sharedPre.setData(USER_DEVICES, userInfor.getBindDevices());
		sharedPre.setData(ADMIN, userInfor.isAdmin());
	}
	public UserInfor getUserInfor()
	{
		UserInfor userInfor = new UserInfor();
		String userId = sharedPre.getData(USER_ID, SystemUtils.getDeviceId(context));
		String userName = sharedPre.getData(USER_NAME, "DG-" + Build.MODEL);
		String userPwd = sharedPre.getData(USER_PWD, "");
		String head = sharedPre.getData(USER_HEAD, "");
		String userAccount = sharedPre.getData(USER_ACCOUNT, "");
		String userDevices = sharedPre.getData(USER_DEVICES, "");
		//String userIp = NetWorkUtils.getLocalIpAddress(context);
		//boolean admin = sharedPre.getData(ADMIN, false);
		userInfor.setUserId(userId);
		userInfor.setUserName(userName);
		userInfor.setHead(head);
		String selectDevice = getDeviceId();
		if(!TextUtils.isEmpty(selectDevice) 
				&& !TextUtils.isEmpty(userDevices)
				&& userDevices.contains(selectDevice))
			userInfor.setAdmin(true);
		else
			userInfor.setAdmin(false);
		userInfor.setPwd(userPwd);
		userInfor.setAccount(userAccount);
		userInfor.setBindDevices(userDevices);
		//userInfor.setUserIp(userIp);
		return userInfor;
	}
	public void clearUserInfor()
	{
		sharedPre.setData(USER_ID, "");
		sharedPre.setData(USER_NAME, "");
		sharedPre.setData(USER_PWD, "");
		sharedPre.setData(USER_ACCOUNT, "");
		sharedPre.setData(THIRD_ID, "");
		sharedPre.setData(THIRD_TOKEN, "");
		sharedPre.setData(USER_DEVICES, "");
		setLoginType("");
		sharedPre.setData(ADMIN, false);
	}
	
	public void updateDeviceName(String deviceName)
	{
		sharedPre.setData(DEVICE_NAME, deviceName);
	}
	public void updateDeviceVersion(String version)
	{
		sharedPre.setData(DEVICE_VERSION, version);
	}
	
	public void setDeviceInfor(DeviceInfor deviceInfor)
	{
		sharedPre.setData(DEVICE_ID, deviceInfor.getId());
		setDeviceCode(deviceInfor.getCode());
		sharedPre.setData(DEVICE_VERSION, deviceInfor.getVersion());
		sharedPre.setData(DEVICE_NAME, deviceInfor.getName());
		if(!TextUtils.isEmpty(deviceInfor.getIp()))
			sharedPre.setData(DEVICE_IP, deviceInfor.getIp());
		if(!TextUtils.isEmpty(deviceInfor.getWifiName()))
			sharedPre.setData(WIFI_SSID, deviceInfor.getWifiName());
		if(!TextUtils.isEmpty(deviceInfor.getWifiPwd()))
			sharedPre.setData(WIFI_PWD, deviceInfor.getWifiPwd());
		if(!TextUtils.isEmpty(deviceInfor.getWifiMac()))
			sharedPre.setData(WIFI_MAC, deviceInfor.getWifiMac());
		if(!TextUtils.isEmpty(deviceInfor.getAddr()))
			sharedPre.setData(DEVICE_ADDR, deviceInfor.getAddr());
	}
	public void clearDeviceInfor()
	{
		sharedPre.setData(DEVICE_ID, "");
		sharedPre.setData(DEVICE_CODE, "");
		sharedPre.setData(DEVICE_VERSION, "");
		sharedPre.setData(DEVICE_NAME, "");
		sharedPre.setData(DEVICE_IP, "");
		sharedPre.setData(WIFI_SSID, "");
		sharedPre.setData(WIFI_PWD, "");
		sharedPre.setData(WIFI_MAC, "");
		sharedPre.setData(DEVICE_ADDR, "");
	}
	public DeviceInfor getDeviceInfor()
	{
		DeviceInfor infor = new DeviceInfor();
		
		String id = getDeviceId();
		String name = getDeviceName();
		String wifiName = getWifiName();
		String wifiPwd = getWifiPwd();
		String version  =getDeviceVersion();
		String addr  = getDeviceAddr();
		String mac = getWifiMac();
		String ip = getDeviceIp();
		//if(!TextUtils.isEmpty(id) && !TextUtils.isEmpty(name) && !TextUtils.isEmpty(wifiName))
		{
			infor.setId(id);
			infor.setName(name);
			infor.setWifiName(wifiName);
			infor.setWifiPwd(wifiPwd);
			infor.setVersion(version);
			infor.setCode(getDeviceCode());
			infor.setWifiMac(mac);
			infor.setIp(ip);
		}
		infor.setAddr(addr);
		return infor;
	}
	public String getDeviceId()
	{
		return sharedPre.getData(DEVICE_ID, "");
	}
	public void setDeviceCode(String code)
	{
		if(!TextUtils.isEmpty(code))
			sharedPre.setData(DEVICE_CODE, code);
	}
	public String getDeviceCode()
	{
		return sharedPre.getData(DEVICE_CODE, "");
	}
	public String getDeviceVersion()
	{
		return sharedPre.getData(DEVICE_VERSION, "");
	}
	public String getDeviceName()
	{
		return sharedPre.getData(DEVICE_NAME, "");
	}
	public String getWifiName()
	{
		return sharedPre.getData(WIFI_SSID, "");
	}
	public String getWifiPwd()
	{
		return sharedPre.getData(WIFI_PWD, "");
	}
	public String getWifiMac()
	{
		return sharedPre.getData(WIFI_MAC, "");
	}
	public String getDeviceIp()
	{
		return sharedPre.getData(DEVICE_IP, "");
	}
	
	public void setWifiInfor(String name, String wifiPwd)
	{
		sharedPre.setData(WIFI_SSID, name);
		if(name.endsWith(Constant.UUID_TAG))
			wifiPwd = Constant.WIFI_HOT_PWD;
		sharedPre.setData(WIFI_PWD, wifiPwd);
	}
	
	/*public void setWifiPwd(String wifiPwd)
	{
		sharedPre.setData(WIFI_PWD, wifiPwd);
	}*/
	
	public void setAdmin(boolean authority)
	{
		sharedPre.setData(ADMIN, authority);
	}
	public boolean isAdmin()
	{
		return sharedPre.getData(ADMIN, false);
	}
	
	public void setMusiIndex(int index)
	{
		sharedPre.setData(MUSIC_INDEX, index);
	}
	public int getMusiIndex()
	{
		return sharedPre.getData(MUSIC_INDEX, 0);
	}
	
	public void setRenderDevice(String name)
	{
		sharedPre.setData(RENDER_DEVICE, name);
	}
	public String getRenderDevice()
	{
		return sharedPre.getData(RENDER_DEVICE, "");
	}
	
	public void setServerDevice(String name)
	{
		sharedPre.setData(SERVER_DEVICE, name);
	}
	public String getServerDevice()
	{
		return sharedPre.getData(SERVER_DEVICE, "");
	}
	
	public void setFirstInit(boolean isFirst)
	{
		sharedPre.setData(FIRST_INIT, isFirst);
	}
	public boolean isFirstInit()
	{
		return sharedPre.getData(FIRST_INIT, true);
	}
	
	public void setCoin(int coin)
	{
		sharedPre.setData(COIN, coin);
	}
	public int getCoin()
	{
		return sharedPre.getData(COIN, 0);
	}
	public void removeCoin(int coin)
	{
		int total = getCoin();
		if(coin >= total)
			setCoin(0);
		else
			setCoin(total - coin);
	}
	
	public void setRefreshTime(long time)
	{
		sharedPre.setData(LAST_REFRESH_TIME, time);
	}
	public long getRefreshTime()
	{
		return sharedPre.getDataLong(LAST_REFRESH_TIME, System.currentTimeMillis());
	}
	
	public void setSongHintShow(boolean isShow)
	{
		sharedPre.setData(IS_SONG_HINT_SHOW, isShow);
	}
	public boolean getSongHintShow()
	{
		return sharedPre.getData(IS_SONG_HINT_SHOW, true);
	}
	
	public void setPlayPermission(String permission)
	{
		sharedPre.setData(PLAY_PERMISSION, permission);
	}
	public String getPlayPermission()
	{
		return sharedPre.getData(PLAY_PERMISSION, "");
	}
	
	public void setPlayRes(String playRes)
	{
		sharedPre.setData(PLAY_RES, playRes);
	}
	public String getPlayRes()
	{
		return sharedPre.getData(PLAY_RES, "");
	}
	
	public void setLat(String lat)
	{
		sharedPre.setData(LAT, lat);
	}
	public void setLon(String lon)
	{
		sharedPre.setData(LON, lon);
	}
	
	public void setDeviceAddr(String addr)
	{
		sharedPre.setData(DEVICE_ADDR, addr);
	}
	
	public String getDeviceAddr()
	{
		return sharedPre.getData(DEVICE_ADDR, "");
	}
	public String getLat()
	{
		return sharedPre.getData(LAT, "");
	}
	public String getLon()
	{
		return sharedPre.getData(LON, "");
	}
	
}
