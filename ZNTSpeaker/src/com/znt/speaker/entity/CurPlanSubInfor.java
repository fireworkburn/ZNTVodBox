package com.znt.speaker.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.znt.diange.mina.entity.SongInfor;

public class CurPlanSubInfor implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String planName = "";
	private String planId = "";
	private String startTime = "";
	private String endTime = "";
	private List<SongInfor> songList = new ArrayList<SongInfor>();
	
	public void setPlanName(String planName)
	{
		this.planName = planName;
	}
	public String getPlanName(String name)
	{
		return planName;
	}
	
	public void setPlanId(String planId)
	{
		this.planId = planId;
	}
	public String getPlanId()
	{
		return planId;
	}
	
	public void setStartTime(String startTime)
	{
		this.startTime = startTime;
	}
	public String getStartTime()
	{
		return startTime;
	}
	
	public void setEndTime(String endTime)
	{
		this.endTime = endTime;
	}
	public String getEndTime()
	{
		return endTime;
	}
	
	public void setSongList(List<SongInfor> songList)
	{
		this.songList = songList;
	}
	public List<SongInfor> getSongList()
	{
		return songList;
	}
}
