package com.znt.speaker.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CurPlanInfor implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String planName = "";
	private String planId = "";
	private String startDate = "";
	private String endDate = "";
	
	private List<CurPlanSubInfor> subPlanList = new ArrayList<CurPlanSubInfor>();
	
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
	
	public void setStartDate(String startDate)
	{
		this.startDate = startDate;
	}
	public String getStartDate()
	{
		return startDate;
	}
	
	public void setEndDate(String endDate)
	{
		this.endDate = endDate;
	}
	public String getEndDate()
	{
		return endDate;
	}
	
	public void addSubPlanInfor(CurPlanSubInfor curPlanSubInfor)
	{
		subPlanList.add(curPlanSubInfor);
	}
	public void setSubPlanList(List<CurPlanSubInfor> subPlanList)
	{
		this.subPlanList = subPlanList;
	}
	public List<CurPlanSubInfor> getSubPlanList()
	{
		return subPlanList;
	}
	
	public boolean isPlanNone()
	{
		return subPlanList.size() == 0;
	}
}
