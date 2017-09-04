package com.znt.speaker.entity;

import android.text.TextUtils;

public class DeviceStatusInfor 
{
	private String lastMusicUpdate = "";//曲库更新时间
	private String vodFlag = "";//0-不允许点播 1-点播盒子内置歌曲 2-可点播第三方歌曲
	private String lastBootTime  = "";//盒子最后启动时间
	private String sysLastVersionNum = "";
	private String planId = "";
	private String planTime = "";
	private String pushStatus  = "";// 1-表示有新点播的歌曲
	private String playStatus   = "";// 0-播放 1-暂停
	private String playingPos = "0";
	private String volume = "0";
	private String videoWhirl = "";
	private String downloadFlag = "0";//0-不下载 1-允许下载
	
	public void setVodFlag(String vodFlag)
	{
		this.vodFlag = vodFlag;
	}
	public String getVodFlag()
	{
		return vodFlag;
	}
	
	public void setVolume(String volume)
	{
		this.volume = volume;
	}
	public int getVolume()
	{
		if(!TextUtils.isEmpty(volume))
			return Integer.parseInt(volume);
		else
			return -1;
	}
	
	public void setDownloadFlag(String downloadFlag)
	{
		this.downloadFlag = downloadFlag;
	}
	public String getDownloadFlag()
	{
		return downloadFlag;
	}
	public boolean isDownloadEnable()
	{
		return downloadFlag.equals("1");
	}
	public boolean isDownloadStop()
	{
		return downloadFlag.equals("2");
	}
	
	public void setPlayingPos(String playingPos)
	{
		this.playingPos = playingPos;
	}
	public String getPlayingPos()
	{
		return playingPos;
	}
	
	public void setPushStatus(String pushStatus)
	{
		this.pushStatus = pushStatus;
	}
	public String getPushStatus()
	{
		return pushStatus;
	}
	
	public void setPlayStatus(String playStatus)
	{
		this.playStatus = playStatus;
	}
	public String getPlayStatus()
	{
		return playStatus;
	}
	public boolean isPlay()
	{
		return playStatus.equals("0");
	}
	public void setPlanId(String planId)
	{
		this.planId = planId;
	}
	public String getPlanId()
	{
		return planId;
	}
	public void setPlanTime(String planTime)
	{
		this.planTime = planTime;
	}
	public String getPlanTime()
	{
		return planTime;
	}
	
	public void setMusicLastUpdate(String lastMusicUpdate)
	{
		this.lastMusicUpdate = lastMusicUpdate;
	}
	public String getMusicLastUpdate()
	{
		return lastMusicUpdate;
	}
	
	public void setLastVersionNum(String sysLastVersionNum)
	{
		this.sysLastVersionNum  = sysLastVersionNum;
	}
	public String getLastVersionNum()
	{
		return sysLastVersionNum;
	}
	
	public void setVideoWhirl(String videoWhirl)
	{
		this.videoWhirl  = videoWhirl;
	}
	public String getVideoWhirl()
	{
		if(videoWhirl == null)
			videoWhirl = "";
		return videoWhirl;
	}
}
