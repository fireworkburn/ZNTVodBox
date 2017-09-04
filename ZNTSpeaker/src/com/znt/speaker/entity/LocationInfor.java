/*  
* @Project: com.znt.zuodeng.activity.SplashActivity 
* @User: Administrator 
* @Description: 社区商服项目
* @Author： yan.yu
* @Company：http://www.neldtv.org/
* @Date 2015-2-5 上午10:43:25 
* @Version V1.0   
*/ 

package com.znt.speaker.entity; 

import java.io.Serializable;

import android.text.TextUtils;

/** 
 * @ClassName: LocationInfor 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-2-5 上午10:43:25  
 */
public class LocationInfor implements Serializable
{

	/** 
	* @Fields serialVersionUID : TODO
	*/ 
	private static final long serialVersionUID = 1L;
	
	private String poi = "";
	private String lat = "";
	private String lon = "";
	private String city = "";
	private String province = "";
	private String district = "";
	private String addr = "";
	private String status = "1";
	
	public void setPoi(String poi)
	{
		this.poi = poi;
	}
	public String getPoi()
	{
		if(poi == null)
			poi = "";
		return poi;
	}
	
	public void setLat(String lat)
	{
		this.lat = lat;
	}
	public String getLat()
	{
		if(lat == null)
			lat = "";
		return lat;
	}
	
	public void setLon(String lon)
	{
		this.lon = lon;
	}
	public String getLon()
	{
		if(lon == null)
			lon = "";
		return lon;
	}
	
	public void setCity(String city)
	{
		this.city = city;
	}
	public String getCity()
	{
		return city;
	}

	public void setProvince(String province)
	{
		this.province = province;
	}
	public String getProvince()
	{
		return province;
	}
	
	public void setDistrict(String district)
	{
		this.district = district;
	}
	public String getDistrict()
	{
		return district;
	}

	public void setAddr(String addr)
	{
		this.addr = addr;
	}
	public String getAddr()
	{
		if(TextUtils.isEmpty(addr))
			return getAddrDesc();
		return addr;
	}
	public String getAddrDesc()
	{
		/*if(TextUtils.isEmpty(addr_desc))
			addr_desc = getArean() + getPoi();
		return addr_desc;*/
		return getArean() + getPoi();
	}
	public String getArean()
	{
		return province + city + district;
	}
	
	public void setStatus(String status)
	{
		this.status = status;
	}
	public String getStatus()
	{
		return status;
	}
	public boolean isSelected()
	{
		return status.equals("0");
	}
}
 
