package com.znt.diange.mina.entity;

import java.io.Serializable;

public class UpdateInfor implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String apkUrl = "";
	private String versionName = "";
	private String versionNum = "";
	private String updateType = "0";//0, 普通升级  1，强制升级
			
			
	public void setApkUrl(String apkUrl)
	{
		this.apkUrl = apkUrl;
	}
	public String getApkUrl()
	{
		return apkUrl;
	}
	
	public void setVersionName(String versionName)
	{
		this.versionName = versionName;
	}
	public String getVersionName()
	{
		return versionName;
	}
	
	public void setVersionNum(String versionNum)
	{
		this.versionNum = versionNum;
	}
	public String getVersionNum()
	{
		return versionNum;
	}
	
	public void setUpdateType(String updateType)
	{
		this.updateType = updateType;
	}
	public String getUpdateType()
	{
		return updateType;
	}
			
}
