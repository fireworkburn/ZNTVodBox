
package com.znt.diange.mina.cmd; 

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

/** 
 * @ClassName: DeviceInfor 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-7-16 上午10:27:56  
 */
public class DeviceInfor implements Serializable
{

	/** 
	* @Fields serialVersionUID : TODO
	*/ 
	private static final long serialVersionUID = 1L;
	
	
	private String name = "";//音响名称
	private String id = "";//音响id
	private String wifiName = "";//WIFI名称
	private String wifiPwd = null;//WIFI密码
	private String wifiMac = null;//WIFI  mac地址
	private String version = "";
	private boolean isAvailable = false;
	private boolean isSelected = false;
	private boolean isAdmin = false;
	private Type type = Type.DNS;
	private String musicCount = "0";
	private String cover = "";
	private String curPlaySong = "";
	private String lastBootTime = "";
	private String softVersion = "";
	private String onlineStatus = "";
	private String volume = "0";
	private String endTime = "";
	private String netInfo = "";
	private String actCode = "";
	private String playingSongType = "";
	private String videoWhirl = "";
	public enum Type
	{
		DNS,
		DLNA
	}
	
	public void setEndTime(String endTime)
	{
		this.endTime = endTime;
	}
	public String getEndTime()
	{
		return endTime;
	}
	
	public void setVolume(String volume)
	{
		this.volume = volume;
	}
	public int getVolume()
	{
		if(!TextUtils.isEmpty(volume))
		{
			int volumeInt = Integer.parseInt(volume);
			if(volumeInt < 0 || volumeInt > 15)
				volumeInt = 0;
			return volumeInt;
		}
		else
			return -1;
	}
	
	public void setMusicCount(String musicCount)
	{
		this.musicCount = musicCount;
	}
	public String getMusicCount()
	{
		return musicCount;
	}
	public void setCover(String cover)
	{
		this.cover = cover;
	}
	public String getCover()
	{
		return cover;
	}
	public void setCurPlaySong(String curPlaySong)
	{
		this.curPlaySong = curPlaySong;
	}
	public String getCurPlaySong()
	{
		return curPlaySong;
	}
	public void setLastBootTime(String lastBootTime)
	{
		this.lastBootTime = lastBootTime;
	}
	public String getLastBootTime()
	{
		return lastBootTime;
	}
	public void setOnlineStatus(String onlineStatus)
	{
		this.onlineStatus = onlineStatus;
	}
	public boolean isOnline()
	{
		if(TextUtils.isEmpty(onlineStatus))
			return false;
		return onlineStatus.equals("1");
	}
	public void setSoftVersion(String softVersion)
	{
		this.softVersion = softVersion;
	}
	public String getSoftVersion()
	{
		return softVersion;
	}
	/**
	*callbacks
	*/
	@Override
	public boolean equals(Object obj)
	{
		// TODO Auto-generated method stub
		if(!(obj instanceof DeviceInfor))
			return false;
		DeviceInfor temp = (DeviceInfor)obj;
		return temp.getId().equals(getId());
	}
	
	/**
	*callbacks
	*/
	@Override
	public int hashCode()
	{
		// TODO Auto-generated method stub
		return id.hashCode();
	}
	
	public JSONObject toJson()
	{
		JSONObject json = new JSONObject();
		try
		{
			json.put("name", name);
			json.put("id", id);
			json.put("code", code);
			json.put("wifiName", wifiName);
			json.put("wifiPwd", wifiPwd);
			json.put("wifiMac", wifiMac);
			json.put("version", version);
			json.put("actCode", actCode);
		} 
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}
	
	public void toClass(String jsonStr)
	{
		setName(getInforFromStr("name", jsonStr));
		setId(getInforFromStr("id", jsonStr));
		setWifiName(getInforFromStr("wifiName", jsonStr));
		setWifiPwd(getInforFromStr("wifiPwd", jsonStr));
		setVersion(getInforFromStr("version", jsonStr));
		setCode(getInforFromStr("code", jsonStr));
		setWifiMac(getInforFromStr("wifiMac", jsonStr));
		setActCode(getInforFromStr("actCode", jsonStr));
	}
	
	public void setAvailable(boolean isAvailable)
	{
		this.isAvailable = isAvailable;
	}
	public boolean isAvailable()
	{
		return isAvailable;
	}
	
	public void setAdmin(boolean isAdmin)
	{
		this.isAdmin = isAdmin;
	}
	public boolean isAdmin()
	{
		return isAdmin;
	}
	
	public void setSelected(boolean isSelected)
	{
		this.isSelected = isSelected;
	}
	public boolean isSelected()
	{
		return isSelected;
	}
	
	public void setVersion(String version)
	{
		this.version = version;
	}
	public String getVersion()
	{
		return version;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	public String getName()
	{
		return name;
	}
	
	public void setId(String id)
	{
		if(id.startsWith("uuid:"))
			id = id.replace("uuid:", "");
		this.id = id;
	}
	public String getId()
	{
		return id;
	}
	
	public void setWifiName(String wifiName)
	{
		this.wifiName = wifiName;
	}
	public String getWifiName()
	{
		if(wifiName == null)
			wifiName = "";
		return wifiName;
	}
	
	public void setWifiPwd(String wifiPwd)
	{
		this.wifiPwd = wifiPwd;
	}
	public String getWifiPwd()
	{
		if(wifiPwd == null)
			wifiPwd = "";
		return wifiPwd;
	}
	
	public void setWifiMac(String wifiMac)
	{
		this.wifiMac = wifiMac;
	}
	public String getWifiMac()
	{
		if(wifiMac == null)
			wifiMac = "";
		return wifiMac;
	}
	
	public void setType(Type type)
	{
		this.type = type;
	}
	public Type getType()
	{
		return type;
	}
	
	private String getInforFromStr(String key, String content)
	{
		String result = "";
		try
		{
			JSONObject json = new JSONObject(content);
			if(json.has(key))
				result = json.getString(key);
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	private String ip = "";
	private String addr = "";
	private String lat = "";
	private String lon = "";
	private String code = "";
	
	public void setIp(String ip)
	{
		this.ip = ip;
	}
	public String getIp()
	{
		return ip;
	}
	
	public void setAddr(String addr)
	{
		this.addr = addr;
	}
	public String getAddr()
	{
		return addr;
	}
	
	public void setLat(String lat)
	{
		this.lat = lat;
	}
	public String getLat()
	{
		return lat;
	}
	
	public void setLon(String lon)
	{
		this.lon = lon;
	}
	public String getLon()
	{
		return lon;
	}
	
	public void setCode(String code)
	{
		this.code = code;
	}
	public String getCode()
	{
		return code;
	}
	
	public void setNetInfor(String netInfo)
	{
		this.netInfo = netInfo;
	}
	public String getNetInfo()
	{
		return netInfo;
	}
	
	public void setPlayingSongType(String playingSongType)
	{
		this.playingSongType = playingSongType;
	}
	public String getPlayingSongType()
	{
		return playingSongType;
	}
	
	public void setActCode(String actCode)
	{
		this.actCode = actCode;
	}
	public String getActCode()
	{
		return actCode;
	}
	
	public void setVideoWhirl(String videoWhirl)
	{
		this.videoWhirl = videoWhirl;
	}
	public String getVideoWhirl()
	{
		return videoWhirl;
	}
}
 
